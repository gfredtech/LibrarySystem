package org.controller;

import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.CheckoutEntry;
import org.storage.resources.Resource;

import java.time.LocalDate;



/**
 * This command renews an item check out.
 * It cannot be performed in case of an outstanding request of the item
 * @see CheckOutCommand
 * @see OutstandingRequestCommand
 */
public class RenewCommand implements Command {

    public RenewCommand(CheckoutEntry c, LocalDate renewDate) {
        checkout = c;
        this.renewDate = renewDate;
    }

    public RenewCommand(CheckoutEntry c) {
        checkout = c;
        renewDate = LocalDate.now();
    }

    @Override
    public Result execute(LibraryStorage storage) {
        boolean outstandingRequest =
                storage.find(Resource.PendingRequest,
                        new QueryParameters()
                                .add("is_outstanding", true)
                                .add("item_id", checkout.getItem().getId()))
                        .size() > 0;
        if (outstandingRequest) {
            return Result.failure("The item is under an outstanding request and cannot be renewed");
        }

        LocalDate newDue;
        switch (checkout.getPatron().getUser().getType()) {
            case "Visiting":
                newDue = renewDate.plusWeeks(1);
                break;
            case "Faculty":
            case "Student":
                if (checkout.isRenewed()) {
                    return Result.failure("You can renew an item only once");
                }
                newDue = CheckOutCommand.calculateOverdueDate(
                            checkout.getPatron(),
                            checkout.getItem(), renewDate);
                break;
            default:
                return Result.failure("Invalid user type: " +
                        checkout.getPatron().getUser().getType());
        }

        storage.updateAll(Resource.Checkout,
                checkout.toQueryParameters(),
                new QueryParameters().add("due_date", newDue).add("is_renewed", true));
        return Result.Success;
    }

    private CheckoutEntry checkout;
    private LocalDate renewDate;
}
