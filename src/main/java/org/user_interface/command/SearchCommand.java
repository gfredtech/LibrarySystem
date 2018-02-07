package org.user_interface.command;

import org.storage.Storage;

import java.util.HashMap;
import java.util.List;



public class SearchCommand implements Command {

    public SearchCommand(Storage storage, List<String> args) {
        documentType = args.get(0);
        searchParameters = args.subList(1, args.size());
    }

    public String run() {
        return storage.searchForItem(documentType, searchParameters);
    }

    Storage storage;
    List<String> searchParameters;
    String documentType;
}
