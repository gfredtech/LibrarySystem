package org.controller;

import org.items.Item;
import org.storage.ItemSerializer;
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
    public Result execute(Storage storage) {
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
