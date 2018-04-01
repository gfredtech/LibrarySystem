package org;


import org.storage.LibraryStorage;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.user_interface.ui.Bot;


/**
 * The main class of the project.
 * It establishes connection to the database specified in command line arguments
 * and then launches a Telegram bot that allows users to interact with the libarary system
 */
public class Application {

    public static void main(String[] args) {
        String databaseName = args[0];
        String userName = args[1];
        String password = args[2];

        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            LibraryStorage.connect(databaseName, userName, password);
            botsApi.registerBot(new Bot());

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
