package org.controller;

import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import org.items.Item;
import org.storage.EntrySerializer;
import org.storage.Storage;
import org.storage.resources.Resource;


public class AddItemCommand implements Command {

    public AddItemCommand(Item item) {
        this.item = item;
    }

    @Override
    public Result execute(Storage storage) {
        try {
            storage.add(Resource.fromItem(item),
                    EntrySerializer.serialize(item));
            return Result.Success;
        } catch (Storage.QueryExecutionError e) {
            return Result.failure(e.getMessage());
        }
    }

    private final Item item;
}
