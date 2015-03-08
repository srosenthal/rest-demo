package com.stephen_rosenthal;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DuplicateDetectorTest {

    @Test
    public void matchingEmailsShouldBeDuplicates() {
        Customer c1 = new Customer(UUID.randomUUID().toString(), "leonard.s.nimoy@gmail.com", "L", "Nimoy");
        Customer c2 = new Customer(UUID.randomUUID().toString(), "leonardsnimoy@gmail.com", "Leo", "Nimoy");
        Customer c3 = new Customer(UUID.randomUUID().toString(), "LeonardSNimoy+autograph@gmail.com", "Lenny", "Nimoy");
        Customer c4 = new Customer(UUID.randomUUID().toString(), "LeonardSNimoy-spam@gmail.com", "Leonard", "Nimoy");

        assertAllMatch(c1, c2, c3, c4);
    }

    @Test
    public void matchingNamesShouldBeDuplicates() {
        Customer c1 = new Customer(UUID.randomUUID().toString(), "l.nimoy.1@gmail.com", "Leonard", "Nimoy");
        Customer c2 = new Customer(UUID.randomUUID().toString(), "l.nimoy.2@gmail.com", "LEONARD", "Nimoy");
        Customer c3 = new Customer(UUID.randomUUID().toString(), "l.nimoy.3@gmail.com", "LEONARD", "NIMOY");
        Customer c4 = new Customer(UUID.randomUUID().toString(), "l.nimoy.4@gmail.com", "  Leonard  ", "  Nimoy  ");

        assertAllMatch(c1, c2, c3, c4);
    }

    @Test
    public void differentNamesAndEmailsShouldNotBeDuplicates() {
        // Some customers that have the same normalized email
        Customer c1 = new Customer(UUID.randomUUID().toString(), "l.nimoy@gmail.com", "L.", "Nimoy");
        Customer c2 = new Customer(UUID.randomUUID().toString(), "geraldo@foxnews.com", "Geraldo", "");
        Customer c3 = new Customer(UUID.randomUUID().toString(), "nph@gmail.com", "Neal Patrick", "Harris");

        assertNoneMatch(c1, c2, c3);
    }

    private void assertAllMatch(Customer... customers) {
        for (Customer c1 : customers) {
            for (Customer c2 : customers) {
                if (c1 != c2) {
                    assertTrue(DuplicateDetector.areCustomersLikelyDuplicates(c1, c2));
                }
            }
        }
    }

    private void assertNoneMatch(Customer... customers) {
        for (Customer c1 : customers) {
            for (Customer c2 : customers) {
                if (c1 != c2) {
                    assertFalse(DuplicateDetector.areCustomersLikelyDuplicates(c1, c2));
                }
            }
        }
    }
}
