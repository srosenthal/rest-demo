package com.stephen_rosenthal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Objects;

/**
 * Models a page of customer results from a List operation.
 * Contains a subset of the fields in org.springframework.data.domain.Page.
 */
public class CustomerPage {
    private final List<Customer> customers;
    private final int numberOfPages;
    private final int page;
    private final int pageSize;
    private final boolean first;
    private final boolean last;

    @JsonCreator
    public CustomerPage(
            @JsonProperty(value = "customers") List<Customer> customers,
            @JsonProperty(value = "numberOfPages") int numberOfPages,
            @JsonProperty(value = "page") int page,
            @JsonProperty(value = "pageSize") int pageSize,
            @JsonProperty(value = "first") boolean first,
            @JsonProperty(value = "last") boolean last) {
        Objects.requireNonNull(customers);
        this.customers = customers;
        this.numberOfPages = numberOfPages;
        this.page = page;
        this.pageSize = pageSize;
        this.first = first;
        this.last = last;
    }

    public CustomerPage(Page<Customer> page) {
        this(page.getContent(), page.getTotalPages(), page.getNumber(), page.getSize(), page.isFirst(), page.isLast());
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public boolean isFirst() {
        return first;
    }

    public boolean isLast() {
        return last;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomerPage that = (CustomerPage) o;

        if (first != that.first) return false;
        if (last != that.last) return false;
        if (numberOfPages != that.numberOfPages) return false;
        if (page != that.page) return false;
        if (pageSize != that.pageSize) return false;
        if (!customers.equals(that.customers)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(customers, numberOfPages, page, pageSize, first, last);
    }
}
