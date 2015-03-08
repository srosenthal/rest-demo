package com.stephen_rosenthal;

import com.google.common.base.CharMatcher;

import java.util.Objects;

/**
 * Utility for detecting likely duplicate customers
 */
public final class DuplicateDetector {

    private DuplicateDetector() {
    }

    /**
     * Detects whether two customers are likely duplicates. Flags the following cases:
     * 1. Emails are exact or approximate matches.
     * 2. First and last names are exact or approximate matches.
     */
    public static boolean areCustomersLikelyDuplicates(Customer customer1, Customer customer2) {
        if (areEmailsLikelyDuplicates(customer1.getEmail(), customer2.getEmail())) {
            return true;
        } else if (
                areNamesLikelyDuplicates(customer1.getFirstName(), customer2.getFirstName())
                        && areNamesLikelyDuplicates(customer1.getLastName(), customer2.getLastName())) {
            return true;
        }

        return false;
    }

    /**
     * Detects whether two email addresses are likely duplicates.
     */
    public static boolean areEmailsLikelyDuplicates(String email1, String email2) {
        return Objects.equals(normalizeEmail(email1), normalizeEmail(email2));
    }

    /**
     * Detects whether two names are likely duplicates
     */
    public static boolean areNamesLikelyDuplicates(String name1, String name2) {
        return Objects.equals(normalizeName(name1), normalizeName(name2));
    }

    /**
     * Normalize an email address, for duplicate detection.
     * For some email providers, the result may be a different but valid address.
     * This method assumes the input email address is valid.
     */
    private static String normalizeEmail(String raw) {
        int index = raw.indexOf("@");
        String local = raw.substring(0, index);
        String domain = raw.substring(index + 1);

        // Remove any periods from the local part; in GMail they are meaningless
        local = CharMatcher.is('.').removeFrom(local);

        // Cut off anything following '+' or '-' characters
        // (GMail uses '+' for tags; some others use '-')
        index = CharMatcher.anyOf("+-").indexIn(local);
        if (index > 0) {
            local = raw.substring(0, index);
        }

        // Convert to lowercase and strip any leading or trailing whitespace.
        return local.toLowerCase().trim() + "@" + domain.toLowerCase().trim();
    }

    /**
     * Normalize a name by shifting to lowercase and removing whitespace.
     */
    private static String normalizeName(String name) {
        if (name == null) {
            return null;
        }
        return name.toLowerCase().trim();
    }
}
