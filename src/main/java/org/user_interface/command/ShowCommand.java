package org.user_interface.command;

import org.resources.Book;
import org.resources.CheckoutInfo;
import org.resources.User;
import org.storage.Storage;


/**
 * Output some information about the system state
 */
public class ShowCommand implements Command {

    /**
     * @param what part of the system data to be shown
     */
    public ShowCommand(Storage storage, String what) {
        this.storage = storage;
        this.whatToShow = what;
    }

    @Override
    public String run() {
        StringBuilder out = new StringBuilder();
        switch (whatToShow) {
            case "users":
                out.append("Name | Card number | Type | Phone number | Address\n");
                for(User u: storage.users) {
                    out.append(u.getName());
                    out.append(" ");
                    out.append(u.getCardNumber());
                    out.append(" ");
                    out.append(u.getType());
                    if(u.getSubtype() != null) {
                        out.append("(");
                        out.append(u.getSubtype());
                        out.append(")");
                    }
                    out.append(" ");
                    out.append(u.getPhoneNumber());
                    out.append(" ");
                    out.append(u.getAddress());
                    out.append("\n");
                }
                break;
            case "books":
                for(Book b: storage.books) {
                    out.append("Title: ");
                    out.append(b.getTitle());
                    out.append("\nAuthors: ");
                    out.append(b.getAuthors());
                    out.append("\nCopies num: ");
                    out.append(b.getCopiesNum());
                    out.append("\nNotes: ");
                    if(b.isReference()) {
                        out.append("reference\n");
                    } else if(b.isBestseller()) {
                        out.append("bestseller\n");
                    } else {
                        out.append("none\n");
                    }
                    out.append("Publisher: ");
                    out.append(b.getPublisher());
                    out.append("\nYear: ");
                    out.append(b.getPublicationYear().getYear());
                    out.append("\nKeywords: ");
                    out.append(b.getKeywords());
                    out.append("\n\n");
                }
                break;
            case "checkouts":
                out.append("Item | Patron Name | Patron Card Number | Overdue\n");
                for(CheckoutInfo c: storage.checkouts) {
                    out.append(c.item.getTitle());
                    out.append(" ");
                    out.append(c.patron.getName());
                    out.append(" ");
                    out.append(c.patron.getCardNumber());
                    out.append(" ");
                    out.append(c.overdue);
                    out.append("\n");
                }
        }
        return out.toString();
    }

    private Storage storage;
    private String whatToShow;
}
