package org.controller;

import org.storage.QueryParameters;
import org.storage.Storage;
import org.storage.resources.*;

/**
 * @author Developed by Vladimir Scherba
 */

public class ReturnCommand implements Command {

    public ReturnCommand(UserEntry user, ItemEntry item) {
        this.user = user;
        this.item = item;
    }

    @Override
    public Result execute(Storage storage) {
        QueryParameters p = new QueryParameters()
                        .add("user_id", user.getUser().getCardNumber())
                        .add("item_id", item.getId());

        try {
            storage.removeAll(Resource.Checkout, p);
            return Result.Success;
        } catch (Storage.QueryExecutionError e) {
            return Result.failure(e.getMessage());
        }
    }

    private UserEntry user;
    private ItemEntry item;
}