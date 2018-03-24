package org.user_interface.commands;

import org.controller.BookingController;
import org.resources.*;

import org.storage.QueryParameters;
import org.storage.SqlStorage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.user_interface.ui.KeyboardUtils;
import java.util.ArrayList;
import java.util.HashMap;

public class CheckoutCommand extends Command {

    @Override
    public String run(AbsSender sender, Update update, String info) {
        Long chatId;
        if(update.hasMessage()) chatId = update.getMessage().getChatId();
        else chatId = update.getCallbackQuery().getMessage().getChatId();

        switch (info) {
            case "startnext":
                   showCheckoutType(sender, update);
                   return "checkout_main";

            case "main":
                String type = displayItemsForCheckout(sender, update);
                return "checkout_select" + type;

            case "selectbook":
                selectBookForCheckout(sender, update, chatId);
                return "checkout_book";

            case "selectavmaterial":
               selectAvMaterialForCheckout(sender, update, chatId);
                return "checkout_avmaterial";

            case "selectjournalissue":
                selectJournalIssueForCheckout(sender, update, chatId);
                return "checkout_journalissue";

            case "book":
                checkOutBook(sender, update, chatId);
                return "menu_main";

            case "avmaterial":
                checkOutAvMaterial(sender, update, chatId);
                return "menu_main";

            case "journalissue":
                checkOutJournalIssue(sender, update, chatId);
                return "menu_main";
                }
        return null;
    }

    void showCheckoutType(AbsSender sender, Update update) {
        keyboardUtils.setInlineKeyBoard(sender, update, "Select the kind of document you want to checkout:", new
                ArrayList<String>() {{
                    add("Book");
                    add("AV Material");
                    add("Journal Article");
                }});
    }

    void selectBookForCheckout(AbsSender sender, Update update, Long chatId) {
        Book bookSelected;
        String msg = update.getMessage().getText();
        int position;
        try {
            position = Integer.parseInt(msg);
        } catch (NumberFormatException e) {
            sendMessage(sender, update, "Input is not a number.");
            return;
        }
        bookSelected = SqlStorage.getInstance().findBooks(new QueryParameters()).get(position - 1);

        if (bookSelected != null) {
            showDocumentDetails(sender, update, bookSelected, "Book");
            documentCursor.put(chatId, bookSelected);
        }
    }

    void selectAvMaterialForCheckout(AbsSender sender, Update update, Long chatId) {
        AvMaterial selected;
        String msg1 = update.getMessage().getText();
        int position1;
        try {
            position1 = Integer.parseInt(msg1);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            sendMessage(sender, update, "Input is not a number");
            return;
        }
        selected = SqlStorage.getInstance().findAvMaterials(new QueryParameters()).get(position1 - 1);

        if (selected != null) {
            showDocumentDetails(sender, update, selected, "AV Material");
            documentCursor.put(chatId, selected);
        }
    }

    void selectJournalIssueForCheckout(AbsSender sender, Update update, Long chatId) {
        JournalIssue selectedJournal;
       String msg1 = update.getMessage().getText();
        int position;
        try {
            position = Integer.parseInt(msg1);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            sendMessage(sender, update, "Input is not a number.");
            return;

        }
        selectedJournal = SqlStorage.getInstance().findJournals(new QueryParameters()).get(position-1);

        if(selectedJournal != null) {
            showDocumentDetails(sender, update, selectedJournal, "Journal Issue");
            documentCursor.put(chatId, selectedJournal);
        }
    }

    String displayItemsForCheckout(AbsSender sender, Update update) {
        String type = update.getCallbackQuery().getData();
        sendMessage(sender, update, "Here's a " +
                "list of all the specified documents in the library. Enter the number of the book you want:");
        if(type.equals("Book")) {
            listBooks(sender, update);
            return "book";
        } else if(type.equals("AV Material")) {
            listAvMaterials(sender, update);
            return "avmaterial";
        } else {
            listJournalIssues(sender, update);
            return "journalissue";
        }
    }

    void checkOutBook(AbsSender sender, Update update, Long chatId) {
        try {
            System.out.println(currentUser.get(chatId).getName());

            /** new BookingController(SqlStorage.getInstance())
             .checkOut(currentUser.get(chatId).getCardNumber(),
             "book", documentCursor.get(chatId).getId()); **/
            keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId),
                    documentCursor.get(chatId).getTitle() + " Checked out successfully.");

        } catch (BookingController.CheckoutException e) {
            keyboardUtils.showMainMenuKeyboard(sender, update,
                    currentUser.get(chatId),
                    "Sorry, but you cannot check out the item now.");
        }
    }

    void checkOutAvMaterial(AbsSender sender, Update update, Long chatId) {
        if(update.getCallbackQuery().getData().equals("Checkout AV Material")) {
            keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId),
                    documentCursor.get(chatId).getTitle() + " Checked out successfully.");

        } else {
            keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId),
                    "Checkout cancelled");
        }

    }

    private void checkOutJournalIssue(AbsSender sender, Update update, Long chatId) {
        if(update.getCallbackQuery().getData().equals("Checkout Journal Issue")) {
            keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId),
                    documentCursor.get(chatId).getTitle() + " checked out successfully");
        } else {
            keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId),
            "Checkout Cancelled");
        }
    }
}
