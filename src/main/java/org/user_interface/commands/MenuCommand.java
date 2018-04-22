package org.user_interface.commands;

import org.user_interface.ui.Interface;

public class MenuCommand extends Command {

    @Override
    public String run(String info) {
        if (info.equals("main") || info.equals("start")) {
            String message = update.hasMessage() ? update.getMessage().getText()
                    : update.getCallbackQuery().getData();

            message = message.split("\\s")[0].trim().toLowerCase();
            if(update.hasMessage()) {
                switch (message) {
                    case "checkout":
                        return new Interface().handleMessageUpdate(sender, update, "checkout_startnext");
                    case "return":
                        return new Interface().handleMessageUpdate(sender, update, "return_startnext");
                    case "edit":
                        return new Interface().handleMessageUpdate(sender, update, "edit_startnext");
                    case "add":
                        return new Interface().handleMessageUpdate(sender, update, "add_startnext");
                    case "renew":
                        return new Interface().handleMessageUpdate(sender, update, "renew_startnext");
                    case "fine":
                        return new Interface().handleMessageUpdate(sender, update, "fine_startnext");
                    case "search":
                        return new Interface().handleMessageUpdate(sender, update, "search_startnext");
                    case "logout":
                        return new Interface().handleMessageUpdate(sender, update, "login_logout");
                    case "outstanding":
                        return new Interface().handleMessageUpdate(sender, update, "outstanding_start");
                    case "action":
                        return new Interface().handleMessageUpdate(sender, update, "action_start");
                    case "/menu":
                        return new Interface().handleMessageUpdate(sender, update, "menu_start");
                    default:
                        return new ErrorCommand().run(sender, update, null);
                }
            } else {
                return new Interface().handleMessageUpdate(sender, update, null);
            }
        }
        return null;
    }
}