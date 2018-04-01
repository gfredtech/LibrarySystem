package org.user_interface.commands;
import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.Resource;
import org.storage.resources.UserEntry;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

public class LoginCommand extends Command {

    @Override
    public String run(AbsSender sender, Update update, String currentState) {
        Long chatId = update.getMessage().getChatId();
        String data;
        switch (currentState) {
            case "start":
                String message = "Enter your username and password, separated by a space:";
                sendMessage(sender, update, message);
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
                    sendMessage(sender, update, "You have less/more than the required input.");
                    return "login_start";
                }

                UserEntry user = LibraryStorage.getInstance().find(Resource.User,
                        new QueryParameters().add("login", username)).get(0);


                     if (user == null) {
                        sendMessage(sender, update, "User not found! Please use /login to try again.");
                        return "start_start";
                    } else {
                        currentUser.put(chatId, user);
                        System.out.println(password.hashCode());
                        System.out.println(currentUser.get(chatId).getUser().getPasswordHash());
                        if (password.hashCode() == currentUser.get(chatId).getUser().getPasswordHash()) {
                            String notifications = new NotificationHandler().init(currentUser.get(chatId).getId());
                            if(notifications.length() > 0)
                                sendMessage(sender, update, "*You have Notifications*\n"
                                        + notifications);
                            keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId).getUser(), "Success!");
                            return "menu_main";
                        } else {
                            sendMessage(sender, update, "Password is incorrect. Please try again.");
                            return "login_password";
                        }
                    }


            case "logout":
                currentUser.remove(chatId);
                documentCursor.remove(chatId);
                sendMessage(sender, update, "Logout successful. You can use /login to sign back in.");
                return "start_start";
        }
        return null;
    }
}
