package org.user_interface.commands;

import org.controller.OutstandingRequestCommand;
import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.PendingRequestEntry;
import org.storage.resources.Resource;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import java.util.List;

public class OutstandingCommand extends Command {
    @Override
    public String run(AbsSender sender, Update update, String info) {
        Long chatId = update.hasMessage() ? update.getMessage().getChatId():
                update.getCallbackQuery().getMessage().getChatId();
        switch (info){
            case "start":
                entries = showCheckoutQueue(sender, update, chatId);
                if(entries == null) return "menu_main";
                return "outstanding_select";

            case "select":
                makeOutstandingRequest(sender, update, chatId);
                return "menu_main";
        }

        return null;
    }


    List<PendingRequestEntry> showCheckoutQueue(AbsSender sender, Update update, Long chatId) {

        List<PendingRequestEntry> pendingRequestEntries =
                LibraryStorage.getInstance().find(Resource.PendingRequest,
                        new QueryParameters());
        StringBuilder builder = new StringBuilder();
        if(pendingRequestEntries.size() != 0) {
            sendMessage(sender, update, "here's a list of all documents pending request(s). select " +
                    "the one you'd like to place an outstanding request for");
            int i = 1;
            for(PendingRequestEntry e: pendingRequestEntries) {
                builder.append(i).append(". ")
                        .append(e.getItem().getItem().getTitle())
                        .append("\n");
            }

            sendMessage(sender, update, builder.toString());
        } else keyboardUtils.showMainMenuKeyboard(sender, update,
                currentUser.get(chatId).getUser(),
                "There are no documents pending request.");
        return pendingRequestEntries;
    }

    void makeOutstandingRequest(AbsSender sender, Update update, Long chatId) {
        int index = Integer.parseInt(update.getMessage().getText());
        PendingRequestEntry e = entries.get(index);

        OutstandingRequestCommand c =
                new OutstandingRequestCommand(currentUser.get(chatId), e.getItem());

        org.controller.Command.Result res =
                c.execute(LibraryStorage.getInstance());
        String message = "";
        switch (res) {
            case Success:
                message = "Success: " + res.getInfo();
                break;

            case Failure:
                message = "Failure: " + res.getInfo();
                break;

            case Warning:
                message = "Warning: " + res.getInfo();
                break;
        }

        keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId).getUser(),
                message);

    }
    
    static List<PendingRequestEntry> entries;
}
