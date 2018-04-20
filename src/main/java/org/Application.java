package org;


import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.storage.LibraryStorage;
import org.telegram.telegrambots.ApiContext;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.user_interface.ui.Bot;


/**
 * The main class of the project.
 * It establishes connection to the database specified in command line arguments
 * and then launches a Telegram bot that allows users to interact with the libarary system
 */

public class Application {

    private static String PROXY_HOST = "socks5.svoi-an.us" /* proxy host */;
    private static Integer PROXY_PORT = 1080 /* proxy port */;
    private static String PROXY_USER = "privet" /* proxy user */;
    private static String PROXY_PASSWORD = "roskomnadzor" /* proxy password */;

    public static void main(String[] args) {
        String databaseName = args[0];
        String userName = args[1];
        String password = args[2];

        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();

        DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(PROXY_HOST, PROXY_PORT),
                new UsernamePasswordCredentials(PROXY_USER, PROXY_PASSWORD));

        HttpHost httpHost = new HttpHost(PROXY_HOST, PROXY_PORT);

        RequestConfig requestConfig = RequestConfig.custom().setProxy(httpHost).setAuthenticationEnabled(true).build();
        botOptions.setRequestConfig(requestConfig);
        botOptions.setCredentialsProvider(credsProvider);
        botOptions.setHttpProxy(httpHost);

        try {

            LibraryStorage.connect(databaseName, userName, password);
            Bot bot  = new Bot(botOptions);
            botsApi.registerBot(bot);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
