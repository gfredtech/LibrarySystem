package org.user_interface.ui;

import org.storage.SearchParameters;
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
import java.util.HashMap;
import java.util.List;

public class Bot extends TelegramLongPollingBot {

    String username, password;

    // entry point of bot
    @Override
    public void onUpdateReceived(Update update) {

        Message message = update.getMessage();


        if (update.hasMessage() && message.hasText()) {

            Long chatId = message.getChatId();
            String x = message.getText();
            Command command;
            switch (x) {
                case "/start":
                    command = new StartCommand(message.getFrom(), message.getChat());

                    SendMessage reply = command.run();
                    reply.setReplyMarkup(new ReplyKeyboardRemove()); // hides keyboard in case it's showing already
                    try {
                        execute(reply);
                        previous.put(chatId, "/start");

                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }

                    break;

                case "/login":
                    command = new LoginCommand(message.getFrom(), message.getChat());

                    SendMessage login = command.run();
                    login.setReplyMarkup(new ReplyKeyboardRemove()); // hides keyboard in case it's showing already

                    try {

                        execute(login);
                        previous.put(chatId, "/login");

                    } catch (TelegramApiException ex) {
                        ex.printStackTrace();
                    }
                    break;

                case "/signup":
                    command = new SignUpCommand(message.getFrom(), message.getChat());

                    SendMessage signup = command.run();
                    signup.setReplyMarkup(new ReplyKeyboardRemove()); // hides keyboard in case it's showing already
                    try {

                        execute(signup);
                        previous.put(chatId, "/signup");
                    } catch (TelegramApiException ex) {
                        ex.printStackTrace();
                    }
                    break;

                default:
                    if(previous.get(chatId).equals("/signup")) {

                        signUpName = message.getText();
                        sendMessage(chatId, "Enter your e-mail address");
                        previous.put(chatId, "/signup_name");

                    } else
                    if(previous.get(chatId).equals("/signup_name")) {
                        signUpEmail = message.getText();
                        sendMessage(chatId, "Enter your phone number");
                        previous.put(chatId, "/signup_email");




                    } else if(previous.get(chatId).equals("/signup_email")) {
                        signUpPhone = message.getText();

                        sendMessage(chatId, "Set a password for your account");
                        previous.put(chatId, "/signup_done");

                    } else if(previous.get(chatId).equals("/signup_done")) {
                        signUpPassword = message.getText();
                        System.out.println(signUpName + " " + signUpEmail + " " + signUpPhone + " " + signUpPassword);
                        String accountDetails = "Name: " + signUpName +
                                "\n\nEmail: " + signUpEmail + "\n\nPhone Number: " + signUpPhone;
                        setInlineKeyBoard(chatId, accountDetails, new ArrayList<String>() {{
                            add("Confirm");
                            add("Cancel");
                        }});


                    }

                    else if (previous.get(chatId).equals("/login")) {
                        username = message.getText();
                        sendMessage(chatId, "Enter password");
                        previous.put(chatId, "/username");

                    } else if (previous.get(chatId).equals("/username")) {
                        password = message.getText();
                        if (username.equals("admin") && password.equals("root")) {
                            showMainMenuKeyboard(chatId, true);
                            previous.put(chatId, "/menu");

                        } else {
                            sendMessage(chatId, "Unsuccessful. Please try again.");
                            previous.put(chatId, "/start");
                            
                        }

                    } else if(previous.get(chatId).equals("/checkout")) {
                        String bookName = message.getText();
                        //TODO: search for book. return list of books that are similar to the book being searched for
                        previous.put(chatId, "book_list");


                    }
                    else if(message.getText().equals("Checkout document")) {
                            if (previous.get(chatId).equals("/menu")) {
                                sendMessage(chatId, "Enter the name of the document you want to check out");
                                previous.put(chatId, "/checkout");
                            } else {
                                sendMessage(chatId, "Please /login or /signup first before you can execute this command.");
                                previous.put(chatId, "/fail");
                            }
                        } else if(previous.get(chatId).equals("/checkout")) {

                    } else if(message.getText().equals("Edit")) {
                       showCRUDkeyboard(chatId);
                       previous.put(chatId, "/edit");
                    } else if(message.getText().equals("Edit/Modify Document")) {
                        //TODO: Edit, modify, delete document

                    } else if(message.getText().equals("Edit/Modify User")) {

                    }

                break;

            }

        } else if(update.hasCallbackQuery()) {
            String call_data = update.getCallbackQuery().getData();
            Long ChatId =  update.getCallbackQuery().getMessage().getChatId();

            if(call_data.equals("Confirm")) {
                createAccount(signUpName, signUpEmail, signUpPhone);
                previous.put(ChatId, "/Confirm");
                sendMessage(ChatId,
                        "Account created successfully! Use /login to login to your account");
            } else if(call_data.equals("Cancel")) {
                if(previous.get(ChatId).equals("/Confirm")) {
                    sendMessage(ChatId, "Already signed up");
                } else {
                sendMessage(ChatId,
                        "Signup cancelled. Use /login, or /signup again if you want to create an account");
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

    void showMainMenuKeyboard(Long chatId, boolean isLibrarian) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("\uD83D\uDE80Checkout document");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("\uD83D\uDD0ESearch");
        keyboard.add(row);

        if(isLibrarian) {
            row = new KeyboardRow();
            row.add("Edit");
            keyboard.add(row);
        }

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

    void setInlineKeyBoard(Long ChatId, String message, List<String> commands) {
        SendMessage msg = new SendMessage();
        msg.setChatId(ChatId);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        for(String i: commands) {
            row.add(new InlineKeyboardButton().
                    setText(i).setCallbackData(i.trim()));
            rows.add(row);
            row = new ArrayList<>();
        }
        markup.setKeyboard(rows);
        msg.setReplyMarkup(markup);
        msg.setChatId(ChatId);
        msg.setText(message);
        try {
            execute(msg);
        }catch (TelegramApiException e) {
            e.printStackTrace();
        }


    }

    void showCRUDkeyboard(Long ChatId) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("Edit/Modify Document");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("Edit/Modify User");
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);

        SendMessage message = new SendMessage().setChatId(ChatId).setText("Success!");
        message.setReplyMarkup(keyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    HashMap<Long, String> previous = new HashMap<>();

    String signUpName, signUpEmail, signUpPhone, signUpPassword;
}

