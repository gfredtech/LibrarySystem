package org.user_interface.commands;

import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.CheckoutEntry;
import org.storage.resources.Resource;
import org.storage.resources.UserEntry;
import org.telegram.telegrambots.api.objects.Update;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FineCommand extends Command {
    @Override
    public String run(String info) {
        Long chatId;
        if(update.hasMessage()) chatId = update.getMessage().getChatId();
        else chatId = update.getCallbackQuery().getMessage().getChatId();
        switch (info) {
            case  "startnext":
                UserEntry e = currentUser.get(chatId);
                String auth = authorizationChecker(e);
                if(auth!= null) return auth;
                fineItems = displayFines(chatId);
                if(fineItems == null) return "menu_main";
                return "fine_select";

            case "select":
                System.out.println("reached");
                selectItemForFine(update, chatId);
                return "menu_main";

        }

        return null;
    }

    private List<CheckoutEntry> displayFines(Long chatId) {
        List<CheckoutEntry> items = finesForUsers();
        int i = 1;
        StringBuilder builder = new StringBuilder();
        for(CheckoutEntry e: items) {
            builder.append(i).append(". ");
            builder.append(e.getItem().getItem().getTitle()).append(", ");
            builder.append(e.getPatron().getUser().getName()).append(", ");
            builder.append(LibraryStorage.getInstance().caluclateFee(e)).append(" rubles");
            builder.append("\n");
        }

        if(builder.length() > 0) {
            sendMessage("These are the current overdue items with the name of patron and" +
                    " fine amount. Select the item you'd like to pay the fine for:\n\n" + builder.toString());
            return items;

        } else {
            keyboardUtils.showMainMenuKeyboard(currentUser.get(chatId).getUser(),
                    "There are no overdue items");
            return null;
        }
    }

   private void selectItemForFine(Update update, Long chatId) {
       CheckoutEntry entry;
       String number = update.getMessage().getText();
       int index;
       index = Integer.parseInt(number);


       entry = fineItems.get(index - 1);
       if (entry != null) documentCursor.put(chatId, entry.getItem());

       assert entry != null;
       System.out.println(entry.getItem().getItem().getTitle());

       new org.controller.ReturnCommand(entry.getPatron(), entry.getItem()).execute(
               LibraryStorage.getInstance());
       keyboardUtils.showMainMenuKeyboard(currentUser.get(chatId).getUser(),
                       "Fine paid successfully by " + entry.getPatron().getUser().getName() + " for "
               + entry.getItem().getItem().getTitle());

    }

    private List<CheckoutEntry> finesForUsers() {

        List<CheckoutEntry> checkoutEntries = LibraryStorage.getInstance().find(Resource.Checkout,
                new QueryParameters());

        List<CheckoutEntry> overdue = new ArrayList<>();

        for(CheckoutEntry entry: checkoutEntries) {
            if (entry.getDueDate().isBefore(LocalDate.now())) {
                overdue.add(entry);
            }
        }
        return overdue;

    }
    private static List<CheckoutEntry> fineItems;
}
