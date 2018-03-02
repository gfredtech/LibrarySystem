package org.user_interface.ui;

import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.user_interface.commands.Command;
import org.user_interface.commands.LoginCommand;
import org.user_interface.commands.SignUpCommand;
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
                        command.run().setReplyMarkup(new ReplyKeyboardRemove()); // hides keyboard in case it's showing already
                        execute(command.run());
                        previous = "/login";

                    } catch (TelegramApiException ex) {
                        ex.printStackTrace();
                    }
                    break;

                case "/signup":
                    command = new SignUpCommand(message.getFrom(), message.getChat());
                    try {
                        command.run().setReplyMarkup(new ReplyKeyboardRemove()); // hides keyboard in case it's showing already
                        execute(command.run());
                        previous = "/signup";
                    } catch (TelegramApiException ex) {
                        ex.printStackTrace();
                    }
                    break;

                default:
                    if(previous!= null && previous.equals("/signup")) {

                        signUpName = message.getText();
                        sendMessage(message.getChatId(), "Enter your e-mail address");
                        previous = "/signup_name";

                    } else
                    if(previous!= null && previous.equals("/signup_name")) {
                        signUpEmail = message.getText();
                        sendMessage(message.getChatId(), "Enter your phone number");
                        previous = "/signup_email";




                    } else if(previous!= null && previous.equals("/signup_email")) {
                        signUpPhone = message.getText();

                        sendMessage(message.getChatId(), "Set a password for your account");
                        previous = "/signup_done";

                    } else if(previous!= null && previous.equals("/signup_done")) {
                        signUpPassword = message.getText();
                        System.out.println(signUpName + " " + signUpEmail + " " + signUpPhone + " " + signUpPassword);
                        String accountDetails = "Name: " + signUpName +
                                "\nEmail: " + signUpEmail + "\nPhone Number: " + signUpPhone;
                        setInlineKeyBoard(message.getChatId(), accountDetails);


                    }

                    else if (previous!= null && previous.equals("/login")) {
                        username = message.getText();
                        sendMessage(message.getChatId(), "Enter password");
                        previous = "/username";

                    } else if (previous!= null && previous.equals("/username")) {
                        password = message.getText();
                        if (username.equals("admin") && password.equals("root")) {
                            showKeyboard(message.getChatId());
                            previous = "/menu";

                        } else {
                            sendMessage(message.getChatId(), "Unsuccessful. Please try again.");
                            previous = "/start";
                        }

                    } else
                        if(message.getText().equals("Checkout document")) {
                            if (previous!= null && previous.equals("/menu")) {
                                sendMessage(message.getChatId(), "Enter the name of the document you want to check out");
                            } else {
                                sendMessage(message.getChatId(), "Please /login or /signup first before you can execute this command.");
                            }
                        }
                break;

            }

        } else if(update.hasCallbackQuery()) {
            String call_data = update.getCallbackQuery().getData();
            Long ChatId =  update.getCallbackQuery().getMessage().getChatId();

            if(call_data.equals("confirm")) {
                createAccount(signUpName, signUpEmail, signUpPhone);
                sendMessage(ChatId,
                        "Account created successfully! Use /login to login to your account");
            } else if(call_data.equals("cancel")) {
                sendMessage(ChatId,
                        "Signup cancelled. Use /login, or /signup again if you want to create an account");
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

    void createAccount(String signUpName, String signUpEmail, String signUpPhone) {
        //TODO: add account to database
    }

    void setInlineKeyBoard(Long ChatId, String message) {
        SendMessage msg = new SendMessage();
        msg.setText(message);
        msg.setChatId(ChatId);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(new InlineKeyboardButton().
                setText("Confirm").setCallbackData("confirm"));
        row.add(new InlineKeyboardButton()
                .setText("Cancel").setCallbackData("cancel"));

        rows.add(row);
        markup.setKeyboard(rows);
        msg.setReplyMarkup(markup);

        try {
            execute(msg);
        }catch (TelegramApiException e) {
            e.printStackTrace();
        }


    }

    String previous;

    String signUpName, signUpEmail, signUpPhone, signUpPassword;
}

