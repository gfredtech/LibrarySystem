package org.resources;

import java.time.LocalDate;


/**
 * Data structure representing a record about an item check-out
 */
public class CheckoutRecord {

    public User patron;
    public LocalDate overdue;
    public Item item;
}
