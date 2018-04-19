package org.controller;

import org.items.User;
import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.Resource;
import org.storage.resources.UserEntry;



public class RemoveUserCommand implements Command {

    public RemoveUserCommand(UserEntry executor, UserEntry toRemove) {
        this.executor = executor;
        this.removed = toRemove;
    }

    @Override
    public Command.Result execute(LibraryStorage storage) {
        if(removed.getUser().getType().equals("Librarian") &&
                !executor.getUser().getType().equals("Admin")) {
            return Result.failure("Only admin can remove a librarian");
        }
        if(executor.getUser().getType().equals("Librarian") &&
            !executor.getUser().hasPrivilege(User.Privilege.Deletion)) {
            return Result.failure("Deletion privilege is required");
        }
        storage.removeAll(Resource.Checkout, new QueryParameters().add("user_id", removed.getId()));
        storage.removeAll(Resource.PendingRequest, new QueryParameters().add("user_id", removed.getId()));
        storage.removeAll(Resource.User, removed.toQueryParameters());
        return Result.Success;
    }

    private final UserEntry removed;
    private final UserEntry executor;
}
