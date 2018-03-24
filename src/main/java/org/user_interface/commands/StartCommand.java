package org.user_interface.commands;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;

public class StartCommand extends Command {

    @Override
    public String run(AbsSender sender, Update update, String info) {
        User user = update.getMessage().getFrom();
        if(info.equals("start")) {
            String welcome = "Welcome " + user.getFirstName()
                    + ". This is the Inno Library Bot. Click /login if you already have an account";
            sendMessage(sender, update, welcome);
            return "start_start";
        }
        return null;
    }


}
