package org.user_interface.commands;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;


public class LoginCommand implements Command {

    private final Chat chat;
    private final User user;

    @Override
    public SendMessage run() {
        SendMessage loginMessage = new SendMessage();
        loginMessage.setChatId(chat.getId());
        loginMessage.setText("Enter your user name");
        return loginMessage;
    }

    public LoginCommand(User user, Chat chat) {
        this.user = user;
        this.chat = chat;
    }



}
