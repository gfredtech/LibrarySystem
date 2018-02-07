package org.user_interface.command;

import org.resources.Book;
import org.resources.CheckoutInfo;
import org.resources.User;
import org.storage.Storage;

import java.time.LocalDate;


public class CheckoutCommand implements  Command {

    public CheckoutCommand(Storage storage, int userCard, String title) {
        this.patronCard = userCard;
        this.bookName = title;
        this.storage = storage;
    }

    @Override
    public String run() {
        User patron = null;
        for(User u: storage.users) {
            if(u.getCardNumber() == patronCard) {
                patron = u;
            }
        }
        if(patron == null) {
            return "Sorry, but no user with such name is found.";
        }
        for(Book b: storage.books) {
            if(b.getTitle().equals(bookName)) {
                int checkoutNum = 0;
                for(CheckoutInfo c: storage.checkouts) {
                    if(c.item.getTitle().equals(bookName)) {
                        if(c.patron.getCardNumber() == patronCard) {
                            return "Sorry, but you have already checked out the book.";
                        }
                        checkoutNum++;
                    }
                }
                if(b.isReference()) {
                    return "Sorry, this is a reference book and cannot be checked out.";
                }
                if(checkoutNum >= b.getCopiesNum()) {
                    return "Sorry, but all such books are already checked out.";
                }
                CheckoutInfo c = new CheckoutInfo();
                c.item = b;
                c.patron = patron;
                if(b.isBestseller()) {
                    c.overdue = LocalDate.now().plusWeeks(2);
                } else if (patron.getSubtype().equals("Faculty")) {
                    c.overdue = LocalDate.now().plusWeeks(4);
                } else {
                    c.overdue = LocalDate.now().plusWeeks(3);
                }
                storage.checkouts.add(c);
                return "The book is successfully checked out until "+c.overdue+".";
            }
        }
        return "Sorry, the book is not found in the library.";
    }

    Storage storage;
    int patronCard;
    String bookName;
}
