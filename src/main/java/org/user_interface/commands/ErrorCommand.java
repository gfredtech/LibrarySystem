package org.user_interface.commands;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

public class ErrorCommand extends Command {
    @Override
    public String run(AbsSender sender, Update update, String info) {
        Long chatId;
        if(update.hasMessage()) chatId = update.getMessage().getChatId();
        else chatId = update.getCallbackQuery().getMessage().getChatId();

        keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId).getUser(),
                "Unknown command, exiting to main menu...");
        return "menu_main";
    }
}
