package org.user_interface;

import org.storage.SqlStorage;
import org.user_interface.ui.Bot;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class Main {
    public static void main(String[] args) {

        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            SqlStorage.connect("library", "librarian", "tabula_rasa");
            botsApi.registerBot(new Bot());

        } catch (TelegramApiException|ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

