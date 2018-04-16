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
public class AddItemCommand<T extends Item> implements Command {

    public AddItemCommand(Item item, User executor) {
        this.itemDesc = item;
        this.executor = executor;
    }


    @Override
    public Result execute(LibraryStorage storage) {
        if(!executor.hasPrivilege(User.Privilege.Addition)) {
            return Result.failure("Access denied; the 'Addition' privilege is required");
        }
        try {
            storage.add(Resource.fromItem(itemDesc), ItemSerializer.serialize(itemDesc));
            return Result.Success;
        } catch (Storage.QueryExecutionError e) {
            return Result.failure(e.getMessage());
        }
    }

    private final Item itemDesc;
    private final User executor;
}
