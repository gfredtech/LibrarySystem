package org.user_interface.ui;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.HashMap;

public class Bot extends TelegramLongPollingBot {
    private Interface handler = new Interface();

    // entry point of bot
    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();

        if (update.hasMessage() && message.hasText()) {
            Long chatId = update.getMessage().getChatId();
            String userState = currentState.getOrDefault(chatId, "start_start");

            currentState.put(chatId, handler.handleMessageUpdate(this, update, userState));

        } else if(update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            currentState.put(chatId, handler.handleCallbackUpdate(
                    this, update, currentState.getOrDefault(chatId, "error")));
        }
    }

    @Override
    public String getBotUsername() {
        return "konyvtar_bot";
    }

    @Override
    public String getBotToken() { return "404457992:AAE0dHw07sHw8woSFiMJSebrQCK2aUyN8CM";  }

    private static HashMap<Long, String> currentState = new HashMap<>();
}

