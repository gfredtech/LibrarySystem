package org.controller;

import org.items.User;
import org.storage.EntrySerializer;
import org.storage.Storage;
import org.storage.resources.Resource;

public class AddUserCommand implements Command {

    public AddUserCommand(User user) {
        this.user = user;
    }

    @Override
    public Command.Result execute(Storage storage) {
        try {
            storage.add(Resource.User, EntrySerializer.serialize(user));
            return Result.Success;

        } catch (Storage.QueryExecutionError e) {
            return Result.failure(e.getMessage());
        }
    }

    private final User user;
}
