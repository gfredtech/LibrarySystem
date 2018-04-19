package org.controller;

import org.items.Item;
import org.items.User;
import org.storage.ItemSerializer;
import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.Storage;
import org.storage.resources.Resource;

import java.util.Arrays;

/**
 * This command adds an item to a storage
 * @see Command
 */
public class AddItemCommand<T extends Item> implements Command {

    public AddItemCommand(T item, User executor) {
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
            storage.add(Resource.ActionLog, getLog());
            return Result.Success;
        } catch (Storage.QueryExecutionError e) {
            return Result.failure(e.getMessage());
        }
    }

    private QueryParameters getLog() {
        return new QueryParameters()
                .add("user_id", executor.getCardNumber())
                .add("action_type", "AddItem")
                .add("action_parameters",
                        Arrays.asList(Resource.fromItem(itemDesc).getTableName()+
                                " {"+itemDesc.getTitle()+"}"));
    }

    private final T itemDesc;
    private final User executor;
}
