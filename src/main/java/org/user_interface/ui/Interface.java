package org.user_interface.ui;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.user_interface.commands.*;

import java.util.HashMap;

public class Interface {

    public static HashMap<String, Command> initialize() {
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
        commandHashMap.put("renew", new RenewCommand());
        return commandHashMap;

    }

   public String handleMessageUpdate(AbsSender sender, Update update, String currentState) {
        String message = update.hasMessage() ? update.getMessage().getText()
                :update.getCallbackQuery().getData();

        if(currentState == null) return new ErrorCommand().run(sender, update, message);

        String userState = currentState.substring(0, currentState.lastIndexOf("_"));
        String userCommand = currentState.substring(currentState.lastIndexOf("_") + 1);

       if(message.equals("/login") || message.equals("/start")) {
           message = message.substring(message.lastIndexOf("/") + 1);

           return initialize().get(message).run(sender, update, "start");
       } else {
           System.out.println(userCommand + " from " + userState);
           return initialize().getOrDefault(userState, new ErrorCommand()).run(sender, update, userCommand);
       }
    }

    String handleCallbackUpdate(AbsSender sender, Update update, String currentState) {
        System.out.println("editrr"  + currentState);
        String userState = currentState.substring(0, currentState.lastIndexOf("_"));
        String userCommand = currentState.substring(currentState.lastIndexOf("_") + 1);

        return initialize().getOrDefault(userState, new ErrorCommand()).run(sender, update, userCommand);
    }
}
