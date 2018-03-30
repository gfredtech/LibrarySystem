package org.controller;

import org.items.Book;
import org.items.Item;
import org.items.User;
import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.Storage;
import org.storage.resources.*;

import java.time.LocalDate;

/**
 * @author Developed by Vladimir Scherba
 */

public class CheckOutCommand implements Command {

    public CheckOutCommand(UserEntry user, ItemEntry itemEntry) {
        this.user = user;
        this.itemEntry = itemEntry;
        this.dateOfCheckout = LocalDate.now();
    }

    public CheckOutCommand(UserEntry user, ItemEntry itemEntry, LocalDate dateOfCheckout) {
        this.user = user;
        this.itemEntry = itemEntry;
        this.dateOfCheckout = dateOfCheckout;
    }



    public Command.Result execute(Storage storage) {
        QueryParameters p = new QueryParameters()
                .add("item_id", itemEntry.getId());
        int checkoutNum = storage.getNumOfEntries(Resource.Checkout, p);
        Item item = itemEntry.getItem();

        if(item.isReference()) {
            return  Result.failure(
                    "A reference item cannot be checked out: "+item.getTitle());
        }

        p = new QueryParameters()
                .add("user_id", user.getId())
                .add("item_id", itemEntry.getId());
        boolean itemIsAlreadyCheckedOutByTheUser =
                ! storage.find(Resource.Checkout, p).isEmpty();
        if(itemIsAlreadyCheckedOutByTheUser) {
            final String result = String.format(
                    "A copy of the item %s is already checked out by the user %s",
                            item.getTitle(), user.getUser().getCardNumber());
            return Result.failure(result);
        }

        QueryParameters params = new QueryParameters()
                .add("item_id", itemEntry.getId())
                .add("item_type", itemEntry.getResourceType().getTableName())
                .add("user_id", user.getId());

        if(checkoutNum >= item.getCopiesNum()) {
            params.add("request_date", dateOfCheckout);
            storage.add(Resource.PendingRequest, params);

            return Result.warning(
                    "There are no copies available of "+ item.getTitle()+
                    "; A request is placed in the queue.");
        }

        params.add("due_date", calculateOverdueDate(user, itemEntry, dateOfCheckout));
        storage.add(Resource.Checkout, params);
        return Result.Success;
    }



    static LocalDate calculateOverdueDate(UserEntry user, ItemEntry itemEntry,
                                          LocalDate dateOfCheckout) {
        LocalDate overdue;
        String userType = user.getUser().getType();
        if(userType.equals("Visiting")) {
            overdue = dateOfCheckout.plusWeeks(1);

        } else if(itemEntry.getResourceType() == Resource.Book) {
            Book b = (Book)itemEntry.getItem();
            if(b.isBestseller()) {
                overdue = dateOfCheckout.plusWeeks(2);
            } else if (user.getUser().getType().equals("Faculty")) {
                overdue = dateOfCheckout.plusWeeks(4);
            } else {
                overdue = dateOfCheckout.plusWeeks(3);
            }
        } else {
            overdue = dateOfCheckout.plusWeeks(2);
        }
        return overdue;
    }

    private UserEntry user;
    private ItemEntry itemEntry;
    private LocalDate dateOfCheckout;
}