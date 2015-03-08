package com.stephen_rosenthal;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * REST API for managing customers
 */
@Controller
@RequestMapping("/customers")
public class CustomersController {

    @Autowired
    private CustomerRepository customerRepository;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> createCustomer(@RequestBody Customer customer) throws URISyntaxException {
        customerRepository.save(customer);
        URI uri = new URI(String.format("/customers/%s", customer.getId()));
        return ResponseEntity.status(HttpStatus.SEE_OTHER).location(uri).build();
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Customer> getCustomer(@PathVariable String id) {
        Customer customer = customerRepository.findOne(id);
        if (customer != null) {
            return new ResponseEntity<>(customer, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /** List all of the customers in the database */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<Customer> listCustomers() {
        return Lists.newArrayList(customerRepository.findAll());
    }

    /** Look up customers that are similar to another, specified by ID */
    @RequestMapping(method = RequestMethod.GET, params = {"likeId"})
    @ResponseBody
    public ResponseEntity<List<Customer>> listSimilarCustomers(@RequestParam String likeId) {
        Customer otherCustomer = customerRepository.findOne(likeId);
        if (otherCustomer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Iterate over all customers in the database to find matches
        List<Customer> customers = new ArrayList<>();
        for (Customer customer : customerRepository.findAll()) {
            if (DuplicateDetector.areCustomersLikelyDuplicates(customer, otherCustomer)) {
                customers.add(customer);
            }
        }
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Customer update(@PathVariable String id, @RequestBody Customer customer) {
        customer.setId(id);
        customerRepository.save(customer);
        return customer;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (customerRepository.exists(id)) {
            customerRepository.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
