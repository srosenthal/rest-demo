package com.stephen_rosenthal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Placeholder interface used by Spring Data
 */
public interface CustomerRepository extends PagingAndSortingRepository<Customer, String> {
    /**
     * Find "similar" customers - customer that have either of the following:
     * (1) the same normalized email address (trimming and ignoring case, periods, anything after '+' or '-').
     * (2) have the same first AND last names (trimming and ignoring case).
     *
     * Note: Spring Data parses the name of this method, so it can only be renamed carefully.
     */
    Page<Customer> findByNormalizedEmailOrFirstNameAndLastNameAllIgnoreCase(
            String normalizedEmail, String firstName, String lastName, Pageable pageable);
}
