package org.user_interface.command;


/**
 * A command with delayed execution - though it should initialized in the constructor,
 * it is executed only by the call of the 'run' method
 */
public interface Command {
    /**
     * @return the command output
     */
    String run();
}
