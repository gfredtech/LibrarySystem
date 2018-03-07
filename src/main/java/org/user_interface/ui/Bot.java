package org.user_interface.ui;


import javafx.util.Pair;
import org.resources.*;
import org.user_interface.commands.*;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.*;

public class Bot extends TelegramLongPollingBot {

    LoginCommand loginCommand = new LoginCommand();
    StartCommand startCommand = new StartCommand();
    SignUpCommand signUpCommand = new SignUpCommand();
    CheckoutCommand checkoutCommand = new CheckoutCommand();
    ReturnCommand returnCommand = new ReturnCommand();



    // entry point of bot
    @Override
    public void onUpdateReceived(Update update) {

        Message message = update.getMessage();
        String currentState;



        if (update.hasMessage() && message.hasText()) {

            Long chatId = message.getChatId();
            String x = message.getText();
            switch (x) {
                case "/start":
                    startCommand.run(this, update, null);
                    break;

                case "/login":
                    currentState = loginCommand.run(this, update, "login_username");
                    previous.put(chatId, currentState);

                    break;

                case "/signup":
                    currentState = signUpCommand.run(this, update, "signup_start");
                    previous.put(chatId, currentState);

                    break;

                case "Checkout Document":
                    // first,  pass currently logged in user to CheckoutCommand module
                    currentUser.put(chatId, loginCommand.returnNewlyLoggedInUser(update));
                    System.out.println("ayo");
                    checkoutCommand.getCurrentUser(update, currentUser.get(chatId));
                    currentState = checkoutCommand.run(this, update, "checkout_start");
                    previous.put(chatId, currentState);

                    break;

                case "Return Document":
                    // first, pass currently logged in user to CheckoutCommand module
                    currentUser.put(chatId, loginCommand.returnNewlyLoggedInUser(update));
                    System.out.println(currentUser.get(chatId).getName());
                    returnCommand.getCurrentUser(update, currentUser.get(chatId));
                    currentState = returnCommand.run(this, update, "return_start");
                    previous.put(chatId, currentState);

                    break;

                    
                default:
                    String input = previous.get(chatId);

                    if (input != null) {
                        input = input.substring(0, input.indexOf("_"));
                        switch (input) {
                            case "login":
                                currentState = loginCommand.run(this, update, previous.get(chatId));
                                previous.put(chatId, currentState);
                                break;


                            case "signup":

                                currentState = signUpCommand.run(this, update, previous.get(chatId));
                                previous.put(chatId, currentState);

                                break;

                            case "checkout":
                                currentState = checkoutCommand.run(this, update, previous.get(chatId));
                                System.out.println("checkout is at" + currentState);
                                previous.put(chatId, currentState);
                                break;

                            case "return":
                                currentState = returnCommand.run(this, update, previous.get(chatId));
                                previous.put(chatId, currentState);
                                break;
                        }
                    }

            }
        }else if(update.hasCallbackQuery()) {
            String call_data = update.getCallbackQuery().getData();
            Long chatId =  update.getCallbackQuery().getMessage().getChatId();

            String previousData = previous.get(chatId);
            previousData = previousData.substring(0, previousData.indexOf("_"));

            switch (previousData) {
                case "signup":
                    signUpCommand.run(this, update, call_data);
                    previous.put(chatId, "signup_confirm");
                    break;

                case "checkout":
                    currentState = checkoutCommand.run(this, update, call_data);
                    previous.put(chatId, currentState);
                    break;

                case "return":
                    currentState = returnCommand.run(this, update, call_data);
                    previous.put(chatId, currentState);
                    break;

            }

            ////////////////////////////////////////////////////////////////////////////////////////////////////
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

    HashMap<Long, String> previous = new HashMap<>();

    Map<Long, User> currentUser = new HashMap<>();


}

