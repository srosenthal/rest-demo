package com.stephen_rosenthal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.CharMatcher;
import com.google.common.base.MoreObjects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

/**
 * Simple model for a customer.
 */
@Entity
public class Customer {

    @Id
    @GeneratedValue
    private String id;

    @Column
    private String email;

    @Column
    private String normalizedEmail;

    @Column
    private String firstName;

    @Column
    private String lastName;

    public Customer() {
        /* Zero-argument constructor for Jackson, etc. Other callers should prefer the other constructors */
    }

    public Customer(String email, String firstName, String lastName) {
        this(null, email, firstName, lastName);
    }

    public Customer(String id, String email, String firstName, String lastName) {
        setId(id);
        setEmail(email.trim());
        setFirstName(firstName.trim());
        setLastName(lastName.trim());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        Objects.requireNonNull(email);
        this.email = email;
        setNormalizedEmail(normalizeEmail(email));
    }

    @JsonIgnore // Should be persisted, but NOT visible in the REST API
    public String getNormalizedEmail() {
        return normalizedEmail;
    }

    public void setNormalizedEmail(String normalizedEmail) {
        Objects.requireNonNull(normalizedEmail);
        this.normalizedEmail = normalizedEmail;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        Objects.requireNonNull(firstName);
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        Objects.requireNonNull(lastName);
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        if (id != null ? !id.equals(customer.id) : customer.id != null) return false;
        if (email != null ? !email.equals(customer.email) : customer.email != null) return false;
        if (normalizedEmail != null ? !normalizedEmail.equals(customer.normalizedEmail) : customer.normalizedEmail != null)
            return false;
        if (firstName != null ? !firstName.equals(customer.firstName) : customer.firstName != null) return false;
        if (lastName != null ? !lastName.equals(customer.lastName) : customer.lastName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, normalizedEmail, firstName, lastName);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("email", email)
                .add("normalizedEmail", normalizedEmail)
                .add("firstName", firstName)
                .add("lastName", lastName)
                .toString();
    }

    /**
     * Normalize an email address, for duplicate detection.
     * For some email providers, the result may be a different but valid address.
     * This method assumes the input email address is valid.
     */
    private static String normalizeEmail(String email) {
        Objects.requireNonNull(email);

        int index = email.indexOf("@");
        String local = email.substring(0, index);
        String domain = email.substring(index + 1);

        // Remove any periods from the local part; in GMail they are meaningless
        local = CharMatcher.is('.').removeFrom(local);

        // Cut off anything following '+' or '-' characters
        // (GMail uses '+' for tags; some others use '-')
        index = CharMatcher.anyOf("+-").indexIn(local);
        if (index > 0) {
            local = email.substring(0, index);
        }

        // Convert to lowercase and strip any leading or trailing whitespace.
        return local.toLowerCase().trim() + "@" + domain.toLowerCase().trim();
    }
}
