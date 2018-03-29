package org.controller;

import org.items.Book;
import org.items.Item;
import org.items.User;
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
    }



    public Command.Result execute(Storage storage) {
        QueryParameters p = new QueryParameters()
                .add("item_id", itemEntry.getId());
        int checkoutNum = storage.getNumOfEntries(Resource.Checkout, p);
        Item item = itemEntry.getItem();

        if(checkoutNum >= item.getCopiesNum()) {
            return Result.failure(
                    "There are no copies available of "+ item.getTitle());
        }
        if(item.isReference()) {
            return  Result.failure(
                    "A reference item cannot be checked out: "+item.getTitle());
        }
        p = new QueryParameters()
                .add("user_id", user.getId());
        boolean itemIsAlreadyCheckedOutByTheUser =
                ! storage.find(Resource.Checkout, p).isEmpty();
        if(itemIsAlreadyCheckedOutByTheUser) {
            final String result = String.format(
                    "A copy of the item %s is alredy checked out by the user %s",
                            item.getTitle(), user.getUser().getCardNumber());
            return Result.failure(result);
        }

        QueryParameters params = new QueryParameters()
                .add("item_id", itemEntry.getId())
                .add("item_type", itemEntry.getResourceType().getTableName())
                .add("user_id", user.getId())
                .add("due_date", calculateOverdueDate());
        storage.add(Resource.Checkout, params);
        return Result.Success;
    }



    private LocalDate calculateOverdueDate() {
        LocalDate overdue;
        String userType = user.getUser().getType();
        if(userType.equals("Visiting")) {
            overdue = LocalDate.now().plusWeeks(1);

        } else if(itemEntry.getResourceType() == Resource.Book) {
            Book b = (Book)itemEntry.getItem();
            if(b.isBestseller()) {
                overdue = LocalDate.now().plusWeeks(2);
            } else if (user.getUser().getType().equals("Faculty")) {
                overdue = LocalDate.now().plusWeeks(4);
            } else {
                overdue = LocalDate.now().plusWeeks(3);
            }
        } else {
            overdue = LocalDate.now().plusWeeks(2);
        }
        return overdue;
    }

    private UserEntry user;
    private ItemEntry itemEntry;
}