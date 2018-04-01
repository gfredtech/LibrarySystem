package org.controller;

import org.items.User;
import org.storage.ItemSerializer;
import org.storage.Storage;
import org.storage.resources.Resource;

/**
 * This command adds a user to a storage
 * @see Command
 */
public class AddUserCommand implements Command {

    public AddUserCommand(User user) {
        this.user = user;
    }

    @Override
    public Command.Result execute(Storage storage) {
        try {
            storage.add(Resource.User, ItemSerializer.serialize(user));
            return Result.Success;

        } catch (Storage.QueryExecutionError e) {
            return Result.failure(e.getMessage());
        }
    }

    private final User user;
}
