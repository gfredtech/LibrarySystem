package org.user_interface.ui;

import org.resources.User;
import org.storage.QueryParameters;
import org.storage.SqlStorage;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class SignUpExecutor implements Executor {


    SignUpExecutor(Bot bot) {
        this.bot = bot;
        state = "signup";
        isDone = false;
    }

    public void processUpdate(Update update) {
        Message message = update.getMessage();

        Long chatId = message.getChatId();
        String text = message.getText();

        switch (state) {
            case "signup":
                SendMessage msg = new SendMessage();
                msg.setChatId(chatId);
                msg.setText("Enter your full name:");
                msg.setReplyMarkup(new ReplyKeyboardRemove()); // hides keyboard in case it's showing already
                try {
                    bot.execute(msg);
                    state = "name";
                } catch (TelegramApiException ex) {
                    ex.printStackTrace();
                }
                break;

            case "name":
                user.setName(message.getText());
                bot.sendMessage(chatId, "Enter your login");
                state = "login";
                break;

            case "login":
                user.setLogin(message.getText());
                bot.sendMessage(chatId, "Enter your password");
                state = "password";
                break;

            case "password":
                user.setPasswordHash(message.getText().hashCode());
                bot.sendMessage(chatId, "Enter your phone number");
                state = "phone_number";
                break;

            case "phone_number":
                user.setPhoneNumber(message.getText());
                bot.sendMessage(chatId, "Enter your address");
                state = "address";
                break;

            case "address":
                user.setPhoneNumber(message.getText());
                state = "";
                bot.sendMessage(chatId, "Are you a Librarian, Student or Faculty member?");
                break;

            case "confirm":
                System.out.println(user);
                String accountDetails = "Name: " + user.getName() +
                        "\n\nLogin: " + user.getLogin() + "\n\nPhone Number: " + user.getPhoneNumber();
                state = "";

                bot.sendMessage(chatId, "Please Confirm or Cancel");

                break;

            default:
                switch (text) {
                    case "Confirm":
                        bot.createAccount(user);
                        state = "";
                        bot.sendMessage(chatId,
                                "Account created successfully! Use /login to login to your account");
                        isDone = true;
                        break;

                    case "Cancel":
                        bot.sendMessage(chatId,
                                "Signup cancelled. Use /login, or /signup again if you want to create an account");
                        isDone = true;
                        state = "";
                        break;

                    case "Student":
                        user.setType("Patron");
                        user.setSubtype("Student");
                        state = "confirm";
                        processUpdate(update);
                        break;

                    case "Faculty":
                        user.setType("Patron");
                        user.setSubtype("Faculty");
                        state = "confirm";
                        processUpdate(update);
                        break;
                }

        }
    }

    public boolean isDone() {
        return isDone;
    }

    Bot bot;
    boolean isDone;
    String state;
    User user = new User(-1);
}
