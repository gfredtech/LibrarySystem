package org.user_interface.command;


import org.storage.Storage;

public class ExitCommand implements Command {

    public ExitCommand(Storage s) {
        this.s = s;
    }

    @Override
    public String run() {
        s.saveCheckouts();
        return "Session is over.";
    }


    private Storage s;
}
