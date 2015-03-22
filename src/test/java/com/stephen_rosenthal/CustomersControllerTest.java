package com.stephen_rosenthal;

import com.google.common.collect.Lists;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.internal.mapper.ObjectMapperType;
import com.jayway.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static com.jayway.restassured.RestAssured.delete;
import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class CustomersControllerTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Value("${local.server.port}")
    int port;

    private Customer franklin;
    private Customer teddy;

    @Before
    public void setUp() throws Exception {
        customerRepository.deleteAll();

        // Save some example values to the database
        franklin = new Customer("fdr@whitehouse.gov", "Franklin", "Roosevelt");
        customerRepository.save(franklin);
        teddy = new Customer("teddy@whitehouse.gov", "Teddy", "Roosevelt");
        customerRepository.save(teddy);

        RestAssured.port = port;
    }

    @Test
    public void canCreateNewCustomer() {
        Customer leonard = new Customer("l.nimoy@gmail.com", "Leonard", "Nimoy");
        Response postResponse = given().contentType("application/json").body(leonard, ObjectMapperType.JACKSON_2)
                .post("/customers");
        assertEquals(HttpStatus.SC_OK, postResponse.getStatusCode());

        Customer postBody = postResponse.getBody().as(Customer.class);

        // Don't bother checking ID - it will only be present on the response, not the request
        assertEquals(leonard.getEmail(), postBody.getEmail());
        assertEquals(leonard.getFirstName(), postBody.getFirstName());
        assertEquals(leonard.getLastName(), postBody.getLastName());
    }

    @Test
    public void canGetFranklin() {
        Response response = get("/customers/{id}", franklin.getId());
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        assertEquals(franklin, response.as(Customer.class));
    }

    @Test
    public void canGetTeddy() {
        Response response = get("/customers/{id}", teddy.getId());
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        assertEquals(teddy, response.as(Customer.class));
    }

    @Test
    public void cannotGetNonexistentCustomer() {
        Response response = get("/customers/{id}", "some-id-that-does-not-exist");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void canListCustomers() {
        // Get all results in a single page
        Response response = get("/customers");
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        CustomerPage page = response.as(CustomerPage.class);
        List<Customer> customers = page.getCustomers();
        assertEquals(2, customers.size());
        assertTrue(customers.contains(franklin));
        assertTrue(customers.contains(teddy));

        // Get results paginated, 1 at a time
        response = get("/customers?pageSize=1&page=0");
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        CustomerPage page0 = response.as(CustomerPage.class);
        List<Customer> paginatedCustomers0 = page0.getCustomers();
        assertEquals(1, paginatedCustomers0.size());
        assertFalse(page0.isLast());

        response = get("/customers?pageSize=1&page=1");
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        CustomerPage page1 = response.as(CustomerPage.class);
        List<Customer> paginatedCustomers1 = page1.getCustomers();
        assertEquals(1, paginatedCustomers1.size());
        assertTrue(page1.isLast());

        List<Customer> allPaginatedCustomers = Lists.newArrayList(paginatedCustomers0);
        allPaginatedCustomers.addAll(paginatedCustomers1);
        assertEquals(customers, allPaginatedCustomers);
    }

    @Test
    public void canListCustomersWithSimilarEmailAddresses() {
        // These customers have similar email addresses but different names
        Customer leonard1 = new Customer("leonardsnimoy@gmail.com", "Leonard", "Nimoy");
        Customer leonard2 = new Customer("leonard.s.nimoy@gmail.com", "L", "Nimoy");
        Customer leonard3 = new Customer("LeonardSNimoy-spam@gmail.com", "Leo", "Nimoy");
        Customer leonard4 = new Customer("LeonardSNimoy+autograph@gmail.com", "Lenny", "Nimoy");

        // These customers have different email addresses but the same names (ignoring whitespace and capitalization)
        Customer leonard5 = new Customer("l.nimoy.1@gmail.com", "Leonard", "Nimoy");
        Customer leonard6 = new Customer("l.nimoy.2@gmail.com", "LEONARD", "Nimoy");
        Customer leonard7 = new Customer("l.nimoy.3@gmail.com", "LEONARD", "NIMOY");
        Customer leonard8 = new Customer("l.nimoy.4@gmail.com", "  Leonard  ", "  Nimoy  ");

        // These customers do not match any of the others
        Customer geraldo = new Customer("geraldo@foxnews.com", "Geraldo", "");
        Customer neal = new Customer("nph@gmail.com", "Neal Patrick", "Harris");

        customerRepository.save(leonard1);
        customerRepository.save(leonard2);
        customerRepository.save(leonard3);
        customerRepository.save(leonard4);
        customerRepository.save(leonard5);
        customerRepository.save(leonard6);
        customerRepository.save(leonard7);
        customerRepository.save(leonard8);
        customerRepository.save(geraldo);
        customerRepository.save(neal);

        // Get all of the results in a single page
        Response response = get("/customers?likeId={id}", leonard1.getId());
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        CustomerPage customerPage = response.as(CustomerPage.class);
        List<Customer> customers = customerPage.getCustomers();
        assertEquals(8, customers.size());
        assertTrue(customers.contains(leonard1));
        assertTrue(customers.contains(leonard2));
        assertTrue(customers.contains(leonard3));
        assertTrue(customers.contains(leonard4));
        assertTrue(customers.contains(leonard5));
        assertTrue(customers.contains(leonard6));
        assertTrue(customers.contains(leonard7));
        assertTrue(customers.contains(leonard8));
        assertFalse(customers.contains(geraldo));
        assertFalse(customers.contains(neal));

        // Get paginated results, 4 entries at a time
        response = get("/customers?likeId={id}&pageSize=4&page=0", leonard1.getId());
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        CustomerPage page0 = response.as(CustomerPage.class);
        List<Customer> paginatedCustomers0 = page0.getCustomers();
        assertEquals(4, paginatedCustomers0.size());
        assertFalse(page0.isLast());

        response = get("/customers?likeId={id}&pageSize=4&page=1", leonard1.getId());
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        CustomerPage page1 = response.as(CustomerPage.class);
        List<Customer> paginatedCustomers1 = page1.getCustomers();
        assertEquals(4, paginatedCustomers1.size());
        assertTrue(page1.isLast());

        List<Customer> allPaginated = Lists.newArrayList(paginatedCustomers0);
        allPaginated.addAll(paginatedCustomers1);
        assertEquals(customers, allPaginated);

    }

    @Test
    public void canUpdateExistingUser() {
        franklin.setEmail("franklin.d.roosevelt@gmail.com");

        Response putResponse = given().contentType("application/json").body(franklin, ObjectMapperType.JACKSON_2)
                .put("/customers/{id}", franklin.getId());
        assertEquals(HttpStatus.SC_OK, putResponse.getStatusCode());
        Customer putBody = putResponse.getBody().as(Customer.class);
        assertEquals(franklin, putBody);

        Response getResponse = get("/customers/{id}", franklin.getId());
        assertEquals(HttpStatus.SC_OK, getResponse.getStatusCode());
        Customer getBody = getResponse.getBody().as(Customer.class);
        assertEquals(franklin, getBody);
    }

    @Test
    public void canDeleteFranklin() {
        Response response = delete("/customers/{id}", franklin.getId());
        assertEquals(HttpStatus.SC_NO_CONTENT, response.getStatusCode());

        response = get("/customers/{id}", franklin.getId());
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void cannotDeleteNonexistentCustomer() {
        Response response = delete("/customers/{id}", "some-id-that-does-not-exist");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusCode());
    }
}
