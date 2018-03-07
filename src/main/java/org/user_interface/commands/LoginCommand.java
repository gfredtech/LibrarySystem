package org.user_interface.commands;

import org.resources.User;
import org.storage.QueryParameters;
import org.storage.SqlStorage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import java.util.HashMap;
import java.util.NoSuchElementException;


public class LoginCommand extends Command {
    HashMap<Long, User> currentUser = new HashMap<>();

    @Override
    public String run(AbsSender sender, Update update, String currentState) {
        Long chatId = update.getMessage().getChatId();
        String data;
        switch (currentState) {
            case "login_username":
                String message = "Enter your username";
                sendMessage(sender, update, message);
                data = "login_password";
                return data;

            case "login_password":
                String username = update.getMessage().getText();
                try {


                   currentUser.put(chatId,
                            SqlStorage.getInstance().findUsers(new QueryParameters().add("login", username)).get(0));
                    sendMessage(sender, update, "Enter your password");
                    return "login_validator";
                } catch (NoSuchElementException e) {
                    sendMessage(sender, update, "User not found");
                    return "start";
                }

            case "login_validator":
                String password = update.getMessage().getText();

                System.out.println(password.hashCode());
                System.out.println(currentUser.get(chatId).getPasswordHash());
                if (password.hashCode() == currentUser.get(chatId).getPasswordHash()) {
                    keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId), "Success!");
                    return "logged_in";

                } else {
                    sendMessage(sender, update, "Password is incorrect. Please try again.");
                    return "login_validator";
                }


        }

        return null;
    }

   public User returnNewlyLoggedInUser(Update update) {
        Long chatId = update.getMessage().getChatId();
      return currentUser.getOrDefault(chatId, null);
    }
}
