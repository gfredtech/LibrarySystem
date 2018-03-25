package org.controller;

import org.storage.Storage;
import org.storage.resources.*;

/**
 * @author Developed by Vladimir Scherba
 */

public class ReturnController {

    public ReturnController(Storage s) {
        storage = s;
    }

    public void returnItem(UserEntry user, ItemEntry item) {
        // find a record with the item
        CheckoutRecord r = (CheckoutRecord)storage.getCheckoutRecordsFor(user.getId())
                .stream().filter(c -> c.item.getId() == item.getId()).toArray()[0];

        storage.removeCheckoutRecord(r);
    }

    private Storage storage;
}