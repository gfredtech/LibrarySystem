package org.controller;


import org.items.User;
import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.ItemEntry;
import org.storage.resources.Resource;
import org.storage.resources.UserEntry;

import java.time.LocalDate;
import java.util.Arrays;


public class RemoveItemCommand<T extends ItemEntry> implements Command {

    public RemoveItemCommand(UserEntry executor, T toRemove) {
        this.executor = executor;
        this.removed = toRemove;
    }

    @Override
    public Command.Result execute(LibraryStorage storage) {
        if(executor.getUser().getType().equals("Librarian") &&
                !executor.getUser().hasPrivilege(User.Privilege.Deletion)) {
            return Result.failure("Deletion privilege is required");
        }
        storage.removeAll(Resource.Checkout, new QueryParameters().add("item_id", removed.getId()));
        storage.removeAll(Resource.PendingRequest, new QueryParameters().add("item_id", removed.getId()));
        storage.removeAll(removed.getResourceType(), removed.toQueryParameters());
        storage.add(Resource.ActionLog, getLog());
        return Result.Success;
    }

    private QueryParameters getLog() {
        return new QueryParameters()
                .add("user_id", executor.getId())
                .add("action_type", "RemoveItem")
                .add("action_parameters",
                        Arrays.asList(
                                removed.getResourceType().getTableName()
                                        +" {"+removed.getId()+"}",
                                LocalDate.now().toString()));
    }



    private final T removed;
    private final UserEntry executor;

}
