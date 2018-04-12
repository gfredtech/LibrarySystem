package org.controller;

import org.items.Item;
import org.items.User;
import org.storage.ItemSerializer;
import org.storage.LibraryStorage;
import org.storage.Storage;
import org.storage.resources.Resource;

/**
 * This command adds an item to a storage
 * @see Command
 */
public class AddItemCommand implements Command {

    public AddItemCommand(Item item) {
        this.item = item;
    }


    @Override
    public Result execute(LibraryStorage storage, User executor) {
        if(!executor.hasPrivilege(User.Privilege.Addition)) {
            return Result.failure("Access denied; the 'Addition' privilege is required");
        }
        try {
            storage.add(Resource.fromItem(item),
                    ItemSerializer.serialize(item));
            return Result.Success;
        } catch (Storage.QueryExecutionError e) {
            return Result.failure(e.getMessage());
        }
    }

    private final Item item;
}
