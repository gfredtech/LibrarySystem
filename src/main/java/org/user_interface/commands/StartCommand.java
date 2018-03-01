package org.user_interface.commands;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;

public class StartCommand implements Command {

    private final User user;
    private final Chat chat;

    public StartCommand(User user, Chat chat) {
        this.user = user;
        this.chat = chat;
    }

    @Override
    public SendMessage run() {

        SendMessage welcome = new SendMessage();
        welcome.setChatId(chat.getId());
        welcome.setText("Hello " + user.getFirstName() + ". Welcome to Inno Library. Please type /login to login to the system");
        return welcome;

    }
}
