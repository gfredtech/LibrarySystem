package org.controller;

import org.resources.Book;
import org.resources.CheckoutRecord;
import org.resources.Item;
import org.resources.User;
import org.storage.QueryParameters;
import org.storage.Storage;



public class ReturnController {

    public ReturnController(Storage s) {
        storage = s;
    }

    public void returnItem(int user_id, String item_type, int item_id) {
        ReturnController controller = new ReturnController(storage);
        Item i;
        switch(item_type) {
            case "book":
                i = storage.getBook(item_id).get();
                break;
            case "journal_issue":
                i = storage.getJournal(item_id).get();
                break;
            case "av_material":
                i = storage.getAvMaterial(item_id).get();
                break;
            default:
                throw new IllegalArgumentException("Invalid item type");
        }
        User u  = storage.getUser(user_id).get();
        CheckoutRecord r = (CheckoutRecord)storage.getCheckoutRecordsFor(u.getCardNumber())
                .stream().filter(c -> c.item.getId() == i.getId()).toArray()[0];

        storage.removeCheckoutRecord(r);
    }

    private Storage storage;
}
