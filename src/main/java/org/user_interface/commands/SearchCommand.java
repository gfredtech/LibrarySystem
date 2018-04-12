package org.user_interface.commands;

public class SearchCommand extends Command {
    @Override
    protected String run(String info) {
        switch (info) {
            case "startnext":
                searchScreen();
                break;
        }

        return null;
    }


    private void searchScreen() {
        //TODO: later on
    }
}
