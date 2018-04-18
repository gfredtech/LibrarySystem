package org.user_interface.ui;

import org.telegram.telegrambots.api.objects.Update;
import org.user_interface.commands.*;

import java.util.HashMap;

public class Interface {

    private static HashMap<String, Command> initialize() {
        HashMap<String, Command> commandHashMap = new HashMap<>();
        commandHashMap.put("menu", new MenuCommand());
        commandHashMap.put("start", new StartCommand());
        commandHashMap.put("login", new LoginCommand());
        commandHashMap.put("signup", new SignUpCommand());
        commandHashMap.put("checkout", new CheckoutItemCommand());
        commandHashMap.put("return", new ReturnItemCommand());
        commandHashMap.put("edit", new EditCommand());
        commandHashMap.put("add", new AddCommand());
        commandHashMap.put("fine", new FineCommand());
        commandHashMap.put("renew", new RenewItemCommand());
        commandHashMap.put("error", new ErrorCommand());
        commandHashMap.put("outstanding", new OutstandingCommand());
        commandHashMap.put("search", new SearchCommand());
        return commandHashMap;

    }

   public String handleMessageUpdate(Update update, String currentState) {
        String message = update.hasMessage() ? update.getMessage().getText()
                :update.getCallbackQuery().getData();

        if(currentState == null) return new ErrorCommand().run(update, message);

        String userState = currentState.substring(0, currentState.lastIndexOf("_"));
        String userCommand = currentState.substring(currentState.lastIndexOf("_") + 1);

       if(message.equals("/login") || message.equals("/start")) {
           message = message.substring(message.lastIndexOf("/") + 1);

           return initialize().get(message).run(update, "start");
       } else {
           System.out.println(userCommand + " from " + userState);
           return initialize().getOrDefault(userState, new ErrorCommand()).run(update, userCommand);
       }
    }

    String handleCallbackUpdate(Update update, String currentState) {

        if(currentState.equals("error")) return initialize().getOrDefault(
                "error", new ErrorCommand()).run(update, "error");

        String userState = currentState.substring(0, currentState.lastIndexOf("_"));
        String userCommand = currentState.substring(currentState.lastIndexOf("_") + 1);

        return initialize().getOrDefault(userState, new ErrorCommand()).run(update, userCommand);
    }
}
