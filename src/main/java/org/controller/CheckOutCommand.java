package org.controller;

import org.items.Book;
import org.items.Item;
import org.storage.QueryParameters;
import org.storage.Storage;
import org.storage.resources.ItemEntry;
import org.storage.resources.Resource;
import org.storage.resources.UserEntry;

import java.time.LocalDate;

/**
 * /**
 * This command checks an item out for a user
 * @see Command
 */

public class CheckOutCommand implements Command {

    public CheckOutCommand(UserEntry user, ItemEntry itemEntry) {
        this.user = user;
        this.itemEntry = itemEntry;
        this.dateOfCheckout = LocalDate.now();
    }

    /**
     * @param user the patron
     * @param itemEntry the item to be checked out
     * @param dateOfCheckout the date when checkout attempt is performed
     */
    public CheckOutCommand(UserEntry user, ItemEntry itemEntry, LocalDate dateOfCheckout) {
        this.user = user;
        this.itemEntry = itemEntry;
        this.dateOfCheckout = dateOfCheckout;
    }

    /**
     * @return
     * - Result.Failure if the item is reference
     *   or all item copies are under an outstanding request
     *   or another copy of the item is already checked out by the user.
     * - Result.Warning if there are no available copies of the item and the user is put to a queue
     * - Result.Success if check out is successful
     */
    @Override
    public Command.Result execute(Storage storage) {
        QueryParameters p = new QueryParameters()
                .add("item_id", itemEntry.getId());
        int checkoutNum = storage.getNumOfEntries(Resource.Checkout, p);
        int outstandingsNum = storage.getNumOfEntries(Resource.PendingRequest, p.add("is_outstanding", true));
        Item item = itemEntry.getItem();

        if (item.isReference()) {
            return Result.failure(
                    "A reference item cannot be checked out: " + item.getTitle());
        }

        p = new QueryParameters()
                .add("user_id", user.getId())
                .add("item_id", itemEntry.getId());
        boolean itemIsAlreadyCheckedOutByTheUser =
                !storage.find(Resource.Checkout, p).isEmpty();
        if (itemIsAlreadyCheckedOutByTheUser) {
            final String result = String.format(
                    "A copy of the item %s is already checked out by the user %s",
                    item.getTitle(), user.getUser().getCardNumber());
            return Result.failure(result);
        }

        QueryParameters params = new QueryParameters()
                .add("item_id", itemEntry.getId())
                .add("item_type", itemEntry.getResourceType().getTableName())
                .add("user_id", user.getId());

        if (checkoutNum >= item.getCopiesNum()) {
            if (outstandingsNum < item.getCopiesNum()) {
                params.add("request_date", dateOfCheckout);
                storage.add(Resource.PendingRequest, params);

                return Result.warning(
                        "There are no copies available of " + item.getTitle() +
                                "; A request is placed in the queue.");
            } else {
                return Result.failure("All items are under an outstanding request. Checking is not possible");
            }
        }

        params.add("due_date", calculateOverdueDate(user, itemEntry, dateOfCheckout));
        storage.add(Resource.Checkout, params);
        return Result.Success;
    }


    /**
     * Calculates the date when the item becomes overdue
     * @param user the patron
     * @param itemEntry the item checked out
     * @param dateOfCheckout the date when the item was checked out
     * @return the date when the item becomes overdue
     */
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