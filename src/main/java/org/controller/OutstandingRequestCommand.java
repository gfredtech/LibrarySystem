package org.controller;

import org.storage.QueryParameters;
import org.storage.Storage;
import org.storage.resources.ItemEntry;
import org.storage.resources.Resource;
import org.storage.resources.UserEntry;

import java.time.LocalDate;

public class OutstandingRequestCommand implements Command {

    public OutstandingRequestCommand(UserEntry librarian, ItemEntry item) {
        this.item = item;
        this.user = librarian;
    }

    @Override
    public Result execute(Storage storage) {
        if( !user.getUser().getType().equals("Librarian") ) {
            return Result.failure("Only a librarian can put an outstanding request");
        }

        storage.removeAll(Resource.PendingRequest,
                new QueryParameters().add("item_id", item.getId()));
        storage.add(Resource.PendingRequest,
                new QueryParameters()
                        .add("item_id", item.getId())
                        .add("user_id", user.getId())
                        .add("request_date", LocalDate.now())
                        .add("is_outstanding", true)
                        .add("item_type", item.getResourceType().getTableName()));

        return Result.Success;
    }


    UserEntry user;
    ItemEntry item;
}
