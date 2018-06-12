package org.user_interface.ui;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.HashMap;

public class Bot extends TelegramLongPollingBot {
    private Interface handler = new Interface();

    // entry point of bot
    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("started");
        Message message = update.getMessage();

        if (update.hasMessage() && message.hasText()) {
            Long chatId = update.getMessage().getChatId();
            String userState = currentState.getOrDefault(chatId, "start_start");

            currentState.put(chatId, handler.handleMessageUpdate(this, update, userState));

        } else if(update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            currentState.put(chatId, handler.handleCallbackUpdate(this, update, currentState.getOrDefault(chatId, "error")));
        }
    }

    public Bot(DefaultBotOptions options) {
        super(options);
    }

    public Bot() {
        super();
    }

    @Override
    public String getBotUsername() { // Telegram bot username here
        return "";
    }


    @Override
    public String getBotToken() { return "";  } //Bot API Token here

    private static HashMap<Long, String> currentState = new HashMap<>();



}

