package org.user_interface.commands;

import org.controller.RenewCommand;
import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.CheckoutEntry;
import org.storage.resources.Resource;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import java.util.HashMap;
import java.util.List;

public class RenewItemCommand extends Command {
    @Override
    public String run(String info) {
        Long chatId;
        if(update.hasMessage()) chatId = update.getMessage().getChatId();
        else chatId = update.getCallbackQuery().getMessage().getChatId();

        switch (info) {
            case "startnext":

                List<CheckoutEntry> checkoutEntries = listCheckedOutMaterials(chatId);
                if(checkoutEntries == null) return "menu_main";
                checkoutEntryMap.put(chatId, checkoutEntries);
                return "renew_indexnumber";

            case "indexnumber":
                selectItemToRenew(chatId, checkoutEntryMap.get(chatId));
                return "menu_main";
        }

        return null;
    }

    private void selectItemToRenew(Long chatId, List<CheckoutEntry> checkoutEntries) {
        CheckoutEntry entry;
        int index = Integer.valueOf(update.getMessage().getText());

        entry = checkoutEntries.get(index - 1);
        if (entry != null) documentCursor.put(chatId, entry.getItem());

        assert entry != null;

        org.controller.Command.Result res = new RenewCommand(entry).execute(LibraryStorage.getInstance());

        String message = "";
        switch (res) {
            case Success:
                message = "You have renewed " + entry.getItem().getItem().getTitle() + " successfully!";
                break;

            case Failure:
                message = "Failure: " + res.getInfo();
                break;

            case Warning:
                message = "Warning: " + res.getInfo();
                break;

        }
        keyboardUtils.showMainMenuKeyboard(currentUser.
                get(chatId).getUser(),  message);

    }

    private List<CheckoutEntry> listCheckedOutMaterials(
                                                        Long chatId) {
        List<CheckoutEntry> entries = LibraryStorage.getInstance().find(
                Resource.Checkout, new QueryParameters());

        if(entries == null || entries.isEmpty()) {
            keyboardUtils.showMainMenuKeyboard(currentUser.get(chatId).getUser(),
                    "You have no checked out documents");
            return null;
        }

        StringBuilder builder = new StringBuilder();
        int i = 1;
        for(CheckoutEntry e: entries) {
            builder.append(i).append(". ");
            builder.append(e.getItem().getItem().getTitle()).append("\n");
        }
        sendMessage(
                "This is the current list of items checked out by you: select the one" +
                        " you want to renew");
        sendMessage(builder.toString());
        return entries;
    }

    private static HashMap<Long, List<CheckoutEntry>> checkoutEntryMap = new HashMap<>();
}
