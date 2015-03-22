package com.stephen_rosenthal;

import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

/**
 * REST API for managing customers
 */
@Controller
@RequestMapping("/customers")
public class CustomersController {

    // Default (max) size for List requests
    private static final int DEFAULT_PAGE_SIZE = 20;

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

    /**
     * List all of the customers in the database
     * @param page (optional) 0-indexed page number.
     * @param pageSize (optional) number of records in a page.
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public CustomerPage listCustomers(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        Page<Customer> customers = customerRepository.findAll(getPageable(page, pageSize));
        return new CustomerPage(customers);
    }

    /**
     * Look up customers that are similar to another, specified by ID
     * @param likeId (required) the id field for another customer in the database
     * @param page (optional) 0-indexed page number.
     * @param pageSize (optional) number of records in a page.
     */
    @RequestMapping(method = RequestMethod.GET, params = {"likeId"})
    @ResponseBody
    public ResponseEntity<CustomerPage> listSimilarCustomers(
            @RequestParam String likeId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        Customer otherCustomer = customerRepository.findOne(likeId);
        if (otherCustomer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Page<Customer> customers = customerRepository.findByNormalizedEmailOrFirstNameAndLastNameAllIgnoreCase(
                otherCustomer.getNormalizedEmail(),
                otherCustomer.getFirstName(),
                otherCustomer.getLastName(),
                getPageable(page, pageSize));
        CustomerPage customerPage = new CustomerPage(customers);
        return new ResponseEntity<>(customerPage, HttpStatus.OK);
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

    /**
     * Build a pagination request, setting default values.
     * @param page 0-indexed page number. Must be 0 or greater. Invalid values default to DEFAULT_PAGE_SIZE.
     * @param pageSize Must be in range [1, DEFAULT_PAGE_SIZE]. Invalid values default to DEFAULT_PAGE_SIZE.
     */
    private PageRequest getPageable(Integer page, Integer pageSize) {
        page = Optional.fromNullable(page).or(0);
        pageSize = Optional.fromNullable(pageSize).or(DEFAULT_PAGE_SIZE);
        if (page < 0) {
            page = 0;
        }
        if (pageSize < 1 || pageSize > DEFAULT_PAGE_SIZE) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        return new PageRequest(page, pageSize);
    }
}
