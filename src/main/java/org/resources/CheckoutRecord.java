package org.resources;

import java.time.LocalDate;


/**
 * Data structure representing a record about an item check-out
 */
public class CheckoutRecord {

    public CheckoutRecord(User patron, Item item, LocalDate date) {
        this.patron = patron;
        this.item = item;
        this.dueDate = date;
    }

    public User patron;
    public LocalDate dueDate;
    public Item item;
}
