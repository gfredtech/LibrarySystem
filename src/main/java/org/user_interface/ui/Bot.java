package org.user_interface.ui;

import org.user_interface.commands.Command;
import org.user_interface.commands.LoginCommand;
import org.user_interface.commands.StartCommand;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {

    String username, password;

    // entry point of bot
    @Override
    public void onUpdateReceived(Update update) {

        Message message = update.getMessage();
        if (update.hasMessage() && message.hasText()) {
            String x = message.getText();
            Command command;
            switch (x) {
                case "/start":
                    command = new StartCommand(message.getFrom(), message.getChat());

                    SendMessage reply = command.run();
                    reply.setReplyMarkup(new ReplyKeyboardRemove()); // hides keyboard in case it's showing already
                    try {
                        execute(reply);
                        previous = "/start";

                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }

                    break;

                case "/login":
                    command = new LoginCommand(message.getFrom(), message.getChat());

                    try {
                        execute(command.run());
                        previous = "/login";

                    } catch (TelegramApiException ex) {
                        ex.printStackTrace();
                    }
                    break;

                default:
                    if (previous.equals("/login")) {
                        username = message.getText();
                        sendMessage(message.getChatId(), "Enter password");
                        previous = "/username";

                    } else if (previous.equals("/username")) {
                        password = message.getText();
                        if (username.equals("admin") && password.equals("root")) {
                            showKeyboard(message.getChatId());
                            previous = "/menu";

                        } else {
                            sendMessage(message.getChatId(), "Unsuccessful. Please try again.");
                            previous = "/start";
                        }

                    } else {
                        if(message.getText().equals("Checkout document")) {
                            if (previous.equals("/menu")) {
                                sendMessage(message.getChatId(), "Enter the name of the document you want to check out");
                            } else {
                                sendMessage(message.getChatId(), "Please /login or /signup first before you can execute this command.");
                            }
                        }
                    }
            }

        }
    }

    @Override
    public String getBotUsername() {
        return "konyvtar_bot";
    }

    @Override
    public String getBotToken() {
        return "404457992:AAE0dHw07sHw8woSFiMJSebrQCK2aUyN8CM";
    }

    void showKeyboard(Long chatId) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("\uD83D\uDE80Checkout document");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("\uD83D\uDD0ESearch");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("⚙️Settings");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("Logout");
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);

        SendMessage message = new SendMessage().setChatId(chatId).setText("Success!");
        message.setReplyMarkup(keyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }



    void sendMessage(Long chatId, String message) {
        try {
            execute(new SendMessage().
                    setChatId(chatId).setText(message));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    String previous;
}
