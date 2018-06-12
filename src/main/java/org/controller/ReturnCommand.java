package org.controller;

import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.Storage;
import org.storage.resources.ItemEntry;
import org.storage.resources.Resource;
import org.storage.resources.UserEntry;

import java.util.Arrays;

/**
 * This command cancels an item check out
 * @see CheckOutCommand
 */

public class ReturnCommand implements Command {

    public ReturnCommand(UserEntry user, ItemEntry item) {
        this.user = user;
        this.item = item;
    }

    @Override
    public Result execute(LibraryStorage storage) {
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

    private QueryParameters getLog() {
        return new QueryParameters()
                .add("user_id", user.getId())
                .add("action_type", "Return")
                .add("action_parameters", Arrays.asList(
                        item.getResourceType().getTableName()+" {"+item.getId()+"}"
                ));
    }

    private UserEntry user;
    private ItemEntry item;
}