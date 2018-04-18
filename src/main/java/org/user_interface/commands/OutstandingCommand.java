package org.user_interface.commands;

import org.controller.OutstandingRequestCommand;
import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.PendingRequestEntry;
import org.storage.resources.Resource;

import java.util.List;

public class OutstandingCommand extends Command {
    @Override
    public String run(String info) {
        Long chatId = update.hasMessage() ? update.getMessage().getChatId():
                update.getCallbackQuery().getMessage().getChatId();

        switch (info){
            case "start":
                entries = showCheckoutQueue(chatId);
                if(entries == null) return "menu_main";

                return "outstanding_select";

            case "select":
                makeOutstandingRequest(chatId);
                return "menu_main";
        }

        return null;
    }

    private List<PendingRequestEntry> showCheckoutQueue(Long chatId) {

        List<PendingRequestEntry> pendingRequestEntries =
                LibraryStorage.getInstance().find(Resource.PendingRequest,
                        new QueryParameters());

        StringBuilder builder = new StringBuilder();

        if(pendingRequestEntries.size() != 0) {
            sendMessage("here's a list of all documents pending request(s). select " +
                    "the one you'd like to place an outstanding request for");
            int i = 1;
            for(PendingRequestEntry e: pendingRequestEntries) {
                builder.append(i).append(". ")
                        .append(e.getItem().getItem().getTitle()).append(" requested by ")
                        .append(e.getUser().getUser().getName())
                        .append("\n");
            }

            sendMessage(builder.toString());
            return pendingRequestEntries;
        } else keyboardUtils.showMainMenuKeyboard(
                currentUser.get(chatId).getUser(),
                "There are no documents pending request.");
        return null;

    }

    private void makeOutstandingRequest(Long chatId) {
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

        keyboardUtils.showMainMenuKeyboard(currentUser.get(chatId).getUser(),
                message);

    }

    private static List<PendingRequestEntry> entries;
}
