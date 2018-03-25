package org.controller;

import org.items.Item;
import org.storage.EntrySerializer;
import org.storage.QueryParameters;
import org.storage.Storage;
import org.storage.resources.ItemEntry;
import org.storage.resources.Resource;


public class ItemManagingController {

    public ItemManagingController(Storage s) {
        storage = s;
    }


    public <T extends Item>
    ItemEntry<T> addItem(Resource table, T item) {
        QueryParameters params = EntrySerializer.serialize(item);
        storage.add(table, params);
        return (ItemEntry)storage.find(table, params).get(0);
    }

    private Storage storage;
}
