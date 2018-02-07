package org.user_interface.command;

public class MessageCommand implements Command {

    public MessageCommand(String message) {
        this.message = message;
    }

    @Override
    public String run() {
        return message;
    }

    private String message;
}
