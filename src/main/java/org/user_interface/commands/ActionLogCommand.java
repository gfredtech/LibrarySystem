package org.user_interface.commands;

import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.ActionLogEntry;
import org.storage.resources.Resource;

import java.util.List;

public class ActionLogCommand extends Command {
    @Override
    protected String run(String info) {
        switch (info){
            case "start":
                List<ActionLogEntry> entries = LibraryStorage.
                        getInstance().find(Resource.ActionLog, new QueryParameters());




        }

        return null;
    }
}
