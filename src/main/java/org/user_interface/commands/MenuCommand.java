package org.user_interface.commands;

import org.user_interface.ui.Interface;

public class MenuCommand extends Command {

    @Override
    public String run(String info) {
        if (info.equals("main")) {
            String message = update.hasMessage() ? update.getMessage().getText()
                    : update.getCallbackQuery().getData();

            message = message.split("\\s")[0].trim().toLowerCase();
            if(update.hasMessage()) {
                switch (message) {
                    case "checkout":
                        return new Interface().handleMessageUpdate(update, "checkout_startnext");
                    case "return":
                        return new Interface().handleMessageUpdate(update, "return_startnext");
                    case "edit":
                        return new Interface().handleMessageUpdate(update, "edit_startnext");
                    case "add":
                        return new Interface().handleMessageUpdate(update, "add_startnext");
                    case "renew":
                        return new Interface().handleMessageUpdate(update, "renew_startnext");
                    case "fine":
                        return new Interface().handleMessageUpdate(update, "fine_startnext");
                    case "search":
                        return new Interface().handleMessageUpdate(update, "start_startnext");
                    case "logout":
                        return new Interface().handleMessageUpdate(update, "login_logout");
                    case "outstanding":
                        return new Interface().handleMessageUpdate(update, "outstanding_start");
                    default:
                        return new ErrorCommand().run(update, null);
                }

            } else {
                return new Interface().handleMessageUpdate(update, null);
            }
        }
        return null;
    }
}