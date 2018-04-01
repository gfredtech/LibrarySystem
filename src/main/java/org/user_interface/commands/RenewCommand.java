package org.user_interface.commands;

import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.CheckoutEntry;
import org.storage.resources.Resource;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import java.util.HashMap;
import java.util.List;

public class RenewCommand extends Command {
    @Override
    public String run(AbsSender sender, Update update, String info) {
        Long chatId;
        if(update.hasMessage()) chatId = update.getMessage().getChatId();
        else chatId = update.getCallbackQuery().getMessage().getChatId();

        switch (info) {
            case "startnext":

                List<CheckoutEntry> checkoutEntries = listCheckedOutMaterials(sender, update, chatId);
                if(checkoutEntries == null) return "menu_main";
                checkoutEntryMap.put(chatId, checkoutEntries);
                return "renew_indexnumber";

            case "indexnumber":
                selectItemToRenew(sender, update, chatId, checkoutEntryMap.get(chatId));
                break;
        }

        return null;
    }

    private void selectItemToRenew(AbsSender sender, Update update, Long chatId, List<CheckoutEntry> checkoutEntries) {
        CheckoutEntry entry;
        int index = Integer.valueOf(update.getMessage().getText());

        entry = checkoutEntries.get(index - 1);
        if (entry != null) documentCursor.put(chatId, entry.getItem());

        assert entry != null;
        System.out.println(entry.getItem().getItem().getTitle());

        //TODO: renew item here
        keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.
                get(chatId).getUser(), entry.getItem().getItem().getTitle() + "renewed!");

    }

    private List<CheckoutEntry> listCheckedOutMaterials(AbsSender sender,
                                                        Update update,
                                                        Long chatId) {
        List<CheckoutEntry> entries = LibraryStorage.getInstance().find(
                Resource.Checkout, new QueryParameters());

        if(entries == null || entries.isEmpty()) {
            keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId).getUser(),
                    "You have no checked out documents");
            return null;
        }

        StringBuilder builder = new StringBuilder();
        int i = 1;
        for(CheckoutEntry e: entries) {
            builder.append(i).append(". ");
            builder.append(e.getItem().getItem().getTitle()).append("\n");
        }
        sendMessage(sender, update,
                "This is the current list of items checked out by you: select the one" +
                        " you want to renew");
        sendMessage(sender, update, builder.toString());
        return entries;
    }

    private static HashMap<Long, List<CheckoutEntry>> checkoutEntryMap = new HashMap<>();
}
