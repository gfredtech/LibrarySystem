package org.user_interface.commands;

import org.items.AvMaterial;
import org.items.Book;
import org.items.JournalIssue;

import org.storage.QueryParameters;
import org.storage.SqlStorage;
import org.storage.resources.AvMaterialEntry;
import org.storage.resources.BookEntry;
import org.storage.resources.JournalIssueEntry;
import org.storage.resources.Resource;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import java.util.ArrayList;

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
        BookEntry bookSelected;
        String msg = update.getMessage().getText();
        int position;
        try {
            position = Integer.parseInt(msg);
        } catch (NumberFormatException e) {
            sendMessage(sender, update, "Input is not a number.");
            return;
        }
        bookSelected = SqlStorage.getInstance().find(Resource.Book, new QueryParameters()).get(position - 1);

        if (bookSelected != null) {
            showDocumentDetails(sender, update, bookSelected, "Book");
            documentCursor.put(chatId, bookSelected);
        }
    }

    void selectAvMaterialForCheckout(AbsSender sender, Update update, Long chatId) {
        AvMaterialEntry selected;
        String msg1 = update.getMessage().getText();
        int position1;
        try {
            position1 = Integer.parseInt(msg1);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            sendMessage(sender, update, "Input is not a number");
            return;
        }

        selected = SqlStorage.getInstance().find(Resource.AvMaterial, new QueryParameters()).get(position1 - 1);

        if (selected != null) {
            showDocumentDetails(sender, update, selected, "AV Material");
            documentCursor.put(chatId, selected);
        }
    }

    void selectJournalIssueForCheckout(AbsSender sender, Update update, Long chatId) {
        JournalIssueEntry selectedJournal;
        String msg1 = update.getMessage().getText();
        int position;
        try {
            position = Integer.parseInt(msg1);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            sendMessage(sender, update, "Input is not a number.");
            return;

        }
        selectedJournal = SqlStorage.getInstance().find(Resource.JournalIssue, new QueryParameters()).get(position-1);

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
        //TODO: check out Book
        if(update.getCallbackQuery().getData().equals("Checkout Book")) {
            keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId).getUser(),
                    documentCursor.get(chatId).getItem().getTitle() + " Checked out successfully.");

        } else {
            keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId).getUser(),
                    "Checkout cancelled");
        }
    }

    void checkOutAvMaterial(AbsSender sender, Update update, Long chatId) {
        //TODO: check out AV material
        if(update.getCallbackQuery().getData().equals("Checkout AV Material")) {
            keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId).getUser(),
                    documentCursor.get(chatId).getItem().getTitle() + " Checked out successfully.");

        } else {
            keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId).getUser(),
                    "Checkout cancelled");
        }

    }

    private void checkOutJournalIssue(AbsSender sender, Update update, Long chatId) {
        //TODO: check out Journal Issue
        if(update.getCallbackQuery().getData().equals("Checkout Journal Issue")) {
            keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId).getUser(),
                    documentCursor.get(chatId).getItem().getTitle() + " checked out successfully");
        } else {
            keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId).getUser(),
                    "Checkout Cancelled");
        }
    }
}
