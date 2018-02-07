package org.resources;

import java.time.LocalDate;


/**
 * Data structure representing a record about an item check-out
 */
public class CheckoutInfo {

    public User patron;
    public LocalDate overdue;
    public Book item;
}
