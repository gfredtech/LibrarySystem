package org.user_interface.commands;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.user_interface.ui.Interface;

public class MenuCommand extends Command {

    @Override
    public String run(AbsSender sender, Update update, String info) {
        if (info.equals("main")) {
            String message = update.hasMessage() ? update.getMessage().getText()
                    : update.getCallbackQuery().getData();

            message = message.split("\\s")[0].trim().toLowerCase();
            if(update.hasMessage()) {
                if (message.equals("checkout")) {
                    return new Interface().handleMessageUpdate(sender, update, "checkout_startnext");
                } else if (message.equals("return")) {
                    return new Interface().handleMessageUpdate(sender, update, "return_startnext");
                } else if (message.equals("edit")) {
                    return new Interface().handleMessageUpdate(sender, update, "edit_startnext");
                } else if (message.equals("add")) {
                    return new Interface().handleMessageUpdate(sender, update, "add_startnext");
                } else if (message.equals("renew")) {
                    return new Interface().handleMessageUpdate(sender, update, "renew_startnext");
                } else if (message.equals("fine")) {
                    return new Interface().handleMessageUpdate(sender, update, "fine_startnext");

                } else return new ErrorCommand().run(sender, update, null);

            } else {
                return new Interface().handleMessageUpdate(sender, update, null);
            }
        }
        return null;
    }
}