package org.user_interface.commands;
import org.storage.QueryParameters;
import org.storage.SqlStorage;
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
                String message = "Enter your username and password separated by a space";
                sendMessage(sender, update, message);
                data = "login_password";
                return data;

            case "password":
                String credentials = update.getMessage().getText();
                String username = credentials.split("\\s")[0].trim();
                String password = credentials.split("\\s")[1].trim();


                System.out.println(username);
                    UserEntry user = SqlStorage.getInstance().find(
                            Resource.User, new QueryParameters().add("login", username)).get(0);


                     if (user == null) {
                        sendMessage(sender, update, "User not found! Please use /login to try again.");
                        return "start_start";
                    } else {
                        currentUser.put(chatId, user);
                        System.out.println(password.hashCode());
                        System.out.println(currentUser.get(chatId).getUser().getPasswordHash());
                        if (password.hashCode() == currentUser.get(chatId).getUser().getPasswordHash()) {
                            keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId).getUser(), "Success!");
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
