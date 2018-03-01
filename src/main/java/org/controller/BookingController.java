package org.controller;

import org.resources.Book;
import org.resources.CheckoutRecord;
import org.resources.Item;
import org.resources.User;
import org.storage.Storage;

import java.time.LocalDate;

public class BookingController {

    public BookingController(Storage s) {
        storage = s;
    }

    public void checkOut(int user_id, String item_type, int item_id) {
        User u = storage.getUser(user_id).get();

        if(u == null) {
            throw new IllegalArgumentException("User with id " + String.valueOf(user_id) + " not found.");
        }

        Item item;
        switch(item_type) {
            case "book":
                item = storage.getBook(item_id).get();
                break;
            case "journal_issue":
                item = storage.getJournal(item_id).get();
                break;
            case "av_material":
                item = storage.getAvMaterial(item_id).get();
                break;
            default:
                throw new RuntimeException();
        }
        int checkoutNum = storage.getNumOfCheckouts(item_id);
        if(checkoutNum >= item.getCopiesNum()) {
            throw new CheckoutException("There are no copies available of "+item.getTitle());
        }
        if(item.isReference()) {
            throw new CheckoutException("A reference item cannot be checked out: "+item.getTitle());
        }
        LocalDate overdue;
        if(item_type.equals("book")) {
            Book b = (Book)item;
            if(b.isBestseller()) {
                overdue = LocalDate.now().plusWeeks(2);
            } else if (u.getSubtype().equals("Faculty")) {
                overdue = LocalDate.now().plusWeeks(4);
            } else {
                overdue = LocalDate.now().plusWeeks(3);
            }
        } else {
            overdue = LocalDate.now().plusWeeks(2);
        }
        CheckoutRecord c = new CheckoutRecord(u, item, overdue);

        storage.addCheckoutRecord(c);
    }

    public class CheckoutException extends RuntimeException {
        public CheckoutException(String cause) {
            super(cause);
        }
    }

    private Storage storage;
}
