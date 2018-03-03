package org.user_interface.commands;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;

public class SignUpCommand implements Command{
    User user;
    Chat chat;

    @Override
    public SendMessage run() {
        SendMessage msg = new SendMessage();
        msg.setChatId(chat.getId());
        msg.setText("Enter your full name:");
        return msg;
    }

    public SignUpCommand(User user, Chat chat) {
        this.user = user;
        this.chat = chat;
    }
}