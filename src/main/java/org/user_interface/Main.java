package org.user_interface;

import org.storage.SqlStorage;
import org.user_interface.ui.Bot;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class Main {
    public static void main(String[] args) {
        String databaseName = args[1];
        String userName = args[2];
        String password = args[3];

        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            SqlStorage.connect(databaseName, userName, password);
            botsApi.registerBot(new Bot());

        } catch (TelegramApiException|ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}



