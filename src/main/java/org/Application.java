package org;


import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.storage.LibraryStorage;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.user_interface.ui.Bot;
import sun.misc.Request;


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
            Bot bot  = new Bot();
            botsApi.registerBot(bot);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
