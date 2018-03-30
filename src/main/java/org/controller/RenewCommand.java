package org.controller;

import org.storage.QueryParameters;
import org.storage.Storage;
import org.storage.resources.CheckoutEntry;
import org.storage.resources.Resource;

import java.time.LocalDate;

public class RenewCommand implements Command {

    public RenewCommand(CheckoutEntry c) {
        checkout = c;
    }

    @Override
    public Result execute(Storage storage) {
        boolean outstandingRequest =
                storage.find(Resource.PendingRequest,
                        new QueryParameters()
                                .add("is_outstanding", true)
                                .add("item_id", checkout.getItem().getId()))
                        .size() > 0;
        if (outstandingRequest) {
            return Result.failure("The item is under an outstanding request and cannot be renewed");
        }

        QueryParameters updated = new QueryParameters();
        LocalDate newDue;
        switch (checkout.getPatron().getUser().getType()) {
            case "Visiting":
                newDue = LocalDate.now().plusWeeks(1);
                break;
            case "Faculty":
            case "Student":
                if (checkout.isRenewed()) {
                    return Result.failure("You can renew an item only once");
                }
                newDue = CheckOutCommand.calculateOverdueDate(
                            checkout.getPatron(),
                            checkout.getItem());
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

    CheckoutEntry checkout;
}
