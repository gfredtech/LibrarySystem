package org.user_interface.command;


/**
 * Inform a user using a simple text message
 */
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
