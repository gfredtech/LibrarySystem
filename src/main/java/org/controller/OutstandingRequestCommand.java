package org.controller;

import org.items.User;
import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.ItemEntry;
import org.storage.resources.Resource;
import org.storage.resources.UserEntry;

import java.time.LocalDate;


/**
 * A command which makes an outstanding request.
 * It may be performed only by a librarian.
 * Users are removed from the queue for the item in case of the request
 */
public class OutstandingRequestCommand implements Command {

    public OutstandingRequestCommand(UserEntry librarian, ItemEntry item) {
        this.item = item;
        this.user = librarian;
    }

    @Override
    public Result execute(LibraryStorage storage, User executor) {
        if(!executor.hasPrivilege(User.Privilege.Addition)) {
            return Result.failure("The 'Addition' privilege is required for the outstanding request");
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


    private UserEntry user;
    private ItemEntry item;
}
