package org.user_interface.commands;

import org.storage.QueryParameters;
import org.storage.SqlStorage;
import org.storage.resources.CheckoutEntry;
import org.storage.resources.Resource;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class FineCommand extends Command {
    @Override
    public String run(AbsSender sender, Update update, String info) {
        Long chatId;
        if(update.hasMessage()) chatId = update.getMessage().getChatId();
        else chatId = update.getCallbackQuery().getMessage().getChatId();
        switch (info) {
            case  "startnext":
                fineItems = displayFines(sender, update, chatId);
                if(fineItems == null) return null;
                return "fine_select";

            case "select":
                System.out.println("reached");
                selectItemForFine(sender, update, chatId);
                return "main_menu";

        }

        return null;
    }

    private List<CheckoutEntry> displayFines(AbsSender sender, Update update, Long chatId) {
        List<CheckoutEntry> items = finesForUsers();
        int i = 1;
        StringBuilder builder = new StringBuilder();
        for(CheckoutEntry e: items) {
            builder.append(i).append(". ");
            builder.append(e.getItem().getItem().getTitle()).append(", ");
            builder.append(e.getPatron().getUser().getName()).append(", ");
            builder.append(calculateFine(e)).append(" rubles");
            builder.append("\n");
        }

        if(builder.length() > 0) {
            sendMessage(sender, update, "These are the current overdue items with the name of patron and" +
                    " fine amount. Select the item you'd like to pay the fine for:\n\n" + builder.toString());
            return items;

        } else {
            keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId).getUser(),
                    "There are no overdue items");
            return null;
        }
    }

   void selectItemForFine(AbsSender sender, Update update, Long chatId) {
       CheckoutEntry entry;
       String number = update.getMessage().getText();
       int index;
       index = Integer.parseInt(number);


       entry = fineItems.get(index - 1);
       if (entry != null) documentCursor.put(chatId, entry.getItem());

       assert entry != null;
       System.out.println(entry.getItem().getItem().getTitle());

       new org.controller.ReturnCommand(entry.getPatron(), entry.getItem()).execute(
               SqlStorage.getInstance());
       keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId).getUser(),
                       "Fine paid successfully by " + entry.getPatron().getUser().getName() + " for "
               + entry.getItem().getItem().getTitle());

    }

    private List<CheckoutEntry> finesForUsers() {

        List<CheckoutEntry> checkoutEntries = SqlStorage.getInstance().find(Resource.Checkout,
                new QueryParameters());

        List<CheckoutEntry> overdue = new ArrayList<>();

        for(CheckoutEntry entry: checkoutEntries) {
            if (entry.getDueDate().isBefore(LocalDate.now())) {
                overdue.add(entry);
            }
        }
        return overdue;

    }

   private int calculateFine(CheckoutEntry entry) {
        LocalDate now = LocalDate.now();
        Period diff = Period.between(entry.getDueDate(), now);
        int fineAmount = diff.getDays() * 100;
        if(fineAmount > entry.getItem().getItem().getPrice()) return entry.getItem().getItem().getPrice();
        return fineAmount;
    }



    static List<CheckoutEntry> fineItems;
}
