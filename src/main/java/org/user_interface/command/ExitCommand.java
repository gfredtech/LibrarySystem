package org.user_interface.command;



public class ExitCommand implements Command {

    @Override
    public String run() {
        return "Session is over.";
    }

}
