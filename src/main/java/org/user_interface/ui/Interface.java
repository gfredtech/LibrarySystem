package org.user_interface.ui;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.user_interface.commands.*;

import java.util.HashMap;

public class Interface {

    public static HashMap<String, Command> initialize() {
        HashMap<String, Command> commandHashMap = new HashMap<>();
        commandHashMap.put("login", new LoginCommand());
        commandHashMap.put("start", new StartCommand());
        commandHashMap.put("menu", new MenuCommand());
        commandHashMap.put("signup", new SignUpCommand());
        commandHashMap.put("checkout", new CheckoutCommand());
        commandHashMap.put("return", new ReturnItemCommand());
        commandHashMap.put("edit", new EditCommand());
        return commandHashMap;

    }
   public String handleMessageUpdate(AbsSender sender, Update update, String currentState) {
        String message = update.getMessage().getText();
        String userState = currentState.substring(0, currentState.lastIndexOf("_"));
        String userCommand = currentState.substring(currentState.lastIndexOf("_") + 1);
        System.out.println(userState + ", " + userCommand);

       if(message.equals("/login") || message.equals("/start")) {
           message = message.substring(message.lastIndexOf("/") + 1);
           System.out.println("mess " + message);
           return initialize().get(message).run(sender, update, userCommand);
       } else {
           System.out.println(userCommand + " from " + userState);
           return initialize().get(userState).run(sender, update, userCommand);
       }
    }

    String handleCallbackUpdate(AbsSender sender, Update update, String currentState) {
        System.out.println("editrr"  + currentState);
        String userState = currentState.substring(0, currentState.lastIndexOf("_"));
        String userCommand = currentState.substring(currentState.lastIndexOf("_") + 1);

        return initialize().get(userState).run(sender, update, userCommand);
    }
}
