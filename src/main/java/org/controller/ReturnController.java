package org.controller;

import org.storage.Storage;

public class ReturnController {

    public ReturnController(Storage s) {
        storage = s;
    }

    public void returnItem(int user_id, String item_type, int item_id) {
    }

    private Storage storage;
}
