package org.user_interface.commands;

public class ErrorCommand extends Command {
    @Override
    public String run(String info) {
        Long chatId;
        if(update.hasMessage()) chatId = update.getMessage().getChatId();
        else chatId = update.getCallbackQuery().getMessage().getChatId();

        keyboardUtils.showMainMenuKeyboard(currentUser.get(chatId).getUser(),
                "Unknown command, exiting to main menu...");
        return "menu_main";
    }
}
