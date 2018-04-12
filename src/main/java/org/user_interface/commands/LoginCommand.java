package org.user_interface.commands;
import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.Resource;
import org.storage.resources.UserEntry;

import java.util.List;

public class LoginCommand extends Command {

    @Override
    public String run(String info) {
        Long chatId = update.getMessage().getChatId();
        String data;
        switch (info) {
            case "start":
                String message = "Enter your username and password, separated by a space:";
                sendMessage(message);
                data = "login_password";
                return data;

            case "password":
                String credentials = update.getMessage().getText();
                String tokens[] = credentials.split("\\s");
                String username, password;
                if(tokens.length == 2) {
                    username = tokens[0].trim();
                    password = tokens[1].trim();
                } else {
                    sendMessage("You have less/more than the required input. Try again.");
                    return "login_password";
                }

                List<UserEntry> users = LibraryStorage.getInstance().find(Resource.User,
                        new QueryParameters().add("login", username));

                if(users.size() == 0) {
                    sendMessage("User not found! Please use /login to try again.");
                    return "start_start";
                }

                UserEntry user = users.get(0);

                     if (user == null) {
                         sendMessage("User not found! Please use /login to try again.");
                         return "start_start";

                    } else {
                        currentUser.put(chatId, user);
                        System.out.println(password.hashCode());
                        System.out.println(currentUser.get(chatId).getUser().getPasswordHash());
                        if (password.hashCode() == currentUser.get(chatId).getUser().getPasswordHash()) {
                            String notifications = new NotificationHandler().init(currentUser.get(chatId).getId());
                            if(notifications.length() > 0)
                                sendMessage("*You have Notifications*\n"
                                        + notifications);

                            keyboardUtils.showMainMenuKeyboard(currentUser.get(chatId).getUser(), "Success!");
                            return "menu_main";
                        } else {
                            sendMessage("Password is incorrect. Please try again.");
                            return "login_password";
                        }
                    }


            case "logout":
                currentUser.remove(chatId);
                documentCursor.remove(chatId);
                sendMessage("Logout successful. You can use /login to sign back in.");
                return "start_start";
        }
        return null;
    }
}
