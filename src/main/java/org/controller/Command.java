package org.controller;

import org.storage.LibraryStorage;

/**
 * A command that should be executed by LibraryManager
 * Any arguments, if needed, are to be passed in a constructor
 * @see LibraryManager#execute(Command)
 */
public interface Command {

    Result execute(LibraryStorage storage);

    enum Result {
        Success, Failure, Warning;

        static Result success(String info) {
            Result r = Success;
            r.info = info;
            return r;
        }
        static Result failure(String info) {
            Result r = Failure;
            r.info = info;
            return r;
        }

        static Result warning(String info) {
            Result r = Warning;
            r.info = info;
            return r;
        }

        // will throw if failure
        public void validate() {
            if (this == Failure)
                throw new RuntimeException("The command execution resulted in a failure: " + this.info);
        }

        public boolean successful() {
            return this == Success;
        }

        public String getInfo() {
            return info;
        }

        private String info;
    }
}
