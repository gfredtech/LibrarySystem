package org.user_interface.commands;
import org.resources.User;
import org.storage.QueryParameters;
import org.storage.SqlStorage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

public class LoginCommand extends Command {

    @Override
    public String run(AbsSender sender, Update update, String currentState) {
        Long chatId = update.getMessage().getChatId();
        String data;
        switch (currentState) {
            case "start":
                String message = "Enter your username and password separated by a space";
                sendMessage(sender, update, message);
                data = "login_password";
                return data;

            case "password":
                String credentials = update.getMessage().getText();
                String username = credentials.split("\\s")[0];
                String password = credentials.split("\\s")[1];

                    User user = SqlStorage.getInstance().findUsers(new QueryParameters().add("login", username)).get(0);
                    if (user == null) {
                        sendMessage(sender, update, "User not found! Please use /login to try again.");
                        return "start_start";
                    } else {
                        currentUser.put(chatId, user);
                        System.out.println(password.hashCode());
                        System.out.println(currentUser.get(chatId).getPasswordHash());
                        if (password.hashCode() == currentUser.get(chatId).getPasswordHash()) {
                            keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId), "Success!");
                            return "menu_main";
                        } else {
                            sendMessage(sender, update, "Password is incorrect. Please try again.");
                            return "login_password";
                        }
                    }
        }
        return null;
    }
}
