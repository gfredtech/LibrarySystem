package org.user_interface.commands;

import javafx.util.Pair;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class StartCommand extends Command {






    @Override
    public String run(AbsSender sender, Update update, String info) {
        User user = update.getMessage().getFrom();
        String welcome = "Welcome " + user.getFirstName()
                + ". This is the Inno Library Bot. Click /login if you already have an account" +
                "or use /signup if you're a new user.";
        sendMessage(sender, update, welcome);

        return null;
    }


}
