package org.user_interface.commands;

import org.telegram.telegrambots.api.objects.User;

public class StartCommand extends Command {

    @Override
    public String run(String info) {
        User user = update.getMessage().getFrom();
        if(info.equals("start")) {
            String welcome = "Welcome " + user.getFirstName()
                    + ". This is the Inno Library Bot. Click /login if you already have an account";
            sendMessage(welcome);
            return "start_start";
        }
        return null;
    }


}
