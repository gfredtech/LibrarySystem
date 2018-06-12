package org.controller;


import org.storage.LibraryStorage;

public class LibraryManager {

    public LibraryManager(LibraryStorage storage) {
        this.storage = storage;
    }

    public Command.Result execute(Command command) {
        return command.execute(storage);
    }

    private final LibraryStorage storage;
}
