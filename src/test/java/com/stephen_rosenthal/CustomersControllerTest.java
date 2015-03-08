package com.stephen_rosenthal;

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

import java.util.Arrays;
import java.util.List;

import static com.jayway.restassured.RestAssured.delete;
import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
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
        postBody.setId(null); // ID is assigned by the server; it won't be in the original request
        assertEquals(leonard, postBody);
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
        Response response = get("/customers");
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        List<Customer> customers = Arrays.asList(response.as(Customer[].class));
        assertEquals(2, customers.size());
        assertTrue(customers.contains(franklin));
        assertTrue(customers.contains(teddy));
    }

    @Test
    public void canListSimilarCustomers() {
        Customer leonard1 = new Customer("leonard.s.nimoy@gmail.com", "L", "Nimoy");
        Customer leonard2 = new Customer("leonardsnimoy@gmail.com", "Leo", "Nimoy");
        Customer leonard3 = new Customer("LeonardSNimoy+autograph@gmail.com", "Lenny", "Nimoy");
        Customer leonard4 = new Customer("LeonardSNimoy-spam@gmail.com", "Leonard", "Nimoy");

        customerRepository.save(leonard1);
        customerRepository.save(leonard2);
        customerRepository.save(leonard3);
        customerRepository.save(leonard4);

        Response response = get("/customers?likeId={id}", leonard1.getId());
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        List<Customer> customers = Arrays.asList(response.as(Customer[].class));
        assertEquals(4, customers.size());
        assertTrue(customers.contains(leonard1));
        assertTrue(customers.contains(leonard2));
        assertTrue(customers.contains(leonard3));
        assertTrue(customers.contains(leonard4));
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
