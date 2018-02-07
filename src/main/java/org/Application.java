package org;


import org.user_interface.UserInterfaceSystem;

public class Application {

    public static void main(String[] args) {
        UserInterfaceSystem system = new UserInterfaceSystem(args);
        system.start();
    }
}
