package org.user_interface.ui;

import org.storage.QueryParameters;
import org.storage.SqlStorage;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.exceptions.TelegramApiException;



public class LoginExecutor implements Executor {

    LoginExecutor(Bot bot) {
        this.bot = bot;
        state = "login";
        isDone = false;
    }

    public void processUpdate(Update update) {
        Message message = update.getMessage();

        Long chatId = message.getChatId();
        String text = message.getText();

        switch (state) {
            case "login":
                try {
                    SendMessage loginMessage = new SendMessage();
                    loginMessage.setReplyMarkup(new ReplyKeyboardRemove()); // hides keyboard in case it's showing already
                    loginMessage.setChatId(chatId);
                    loginMessage.setText("Enter your user name");
                    bot.execute(loginMessage);
                    state = "username";
                } catch (TelegramApiException ex) {
                    ex.printStackTrace();
                }
                break;

            case "username":
                String username = text;
                try {
                    bot.currentUser.put(chatId,
                            SqlStorage.getInstance().findUsers(
                                    new QueryParameters().add("login", username)).get(0));
                    bot.sendMessage(chatId, "Enter password");
                    state = "password";
                } catch (IndexOutOfBoundsException e) {
                    bot.sendMessage(chatId, "User not found");
                    isDone = true;
                }

                break;
            case "password":
                String password = text;

                if (password.hashCode() == bot.currentUser.get(chatId).getPasswordHash()) {
                    if(bot.currentUser.get(chatId).getType().equals("Librarian")) {
                        bot.showMainMenuKeyboard(chatId, "Success");
                    } else {
                        bot.showMainMenuKeyboard(chatId, "Success");
                    }
                    isDone = true;
                } else {
                    bot.sendMessage(chatId, "Login or password is incorrect. Please try again.");
                    state = "login";
                }
                break;

            }

    }

    public boolean isDone() {
        return isDone;
    }

    Bot bot;
    boolean isDone;
    String state;
}