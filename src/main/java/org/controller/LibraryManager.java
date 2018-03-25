package org.controller;


import org.storage.Storage;

public class LibraryManager {

    public LibraryManager(Storage storage) {
        this.storage = storage;
    }

    public Command.Result execute(Command command) {
        return command.execute(storage);
    }

    private final Storage storage;
}
