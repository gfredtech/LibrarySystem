package org.user_interface.commands;

import org.controller.CheckOutCommand;
import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.AvMaterialEntry;
import org.storage.resources.BookEntry;
import org.storage.resources.JournalIssueEntry;
import org.storage.resources.Resource;

import java.util.ArrayList;

import static org.controller.Command.Result;

public class CheckoutItemCommand extends Command {

    @Override
    public String run(String info) {
        Long chatId;
        if(update.hasMessage()) chatId = update.getMessage().getChatId();
        else chatId = update.getCallbackQuery().getMessage().getChatId();

        switch (info) {
            case "startnext":
                showCheckoutType();
                return "checkout_main";

            case "main":
                String type = displayItemsForCheckout();
                return "checkout_select" + type;

            case "selectbook":
                return selectBookForCheckout(chatId);

            case "selectavmaterial":
                return selectAvMaterialForCheckout(chatId);

            case "selectjournalissue":
                selectJournalIssueForCheckout(chatId);
                return "checkout_final";

            case "final":
                checkOutItem(chatId);
                return "menu_main";
        }
        return null;
    }

    private void showCheckoutType() {
        keyboardUtils.setInlineKeyBoard("Select the kind of document you want to checkout:", new
                ArrayList<String>() {{
                    add("Book");
                    add("AV Material");
                    add("Journal Issue");
                }});
    }

    private String selectBookForCheckout(Long chatId) {
        BookEntry bookSelected;
        String msg = update.getMessage().getText();
        int position;
        try {
            position = Integer.parseInt(msg);
        } catch (NumberFormatException e) {
            sendMessage("Input is not a number.");
            return "checkout_selectbook";
        }
        bookSelected = LibraryStorage.getInstance().find(Resource.Book, new QueryParameters()).get(position - 1);

        if (bookSelected != null) {
            showDocumentDetails(bookSelected);
            documentCursor.put(chatId, bookSelected);
        }
        return "checkout_final";
    }

    private String selectAvMaterialForCheckout(Long chatId) {
        AvMaterialEntry selected;
        String msg1 = update.getMessage().getText();
        int position1;
        try {
            position1 = Integer.parseInt(msg1);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            sendMessage("Input is not a number");
            return "checkout_selectavmaterial";
        }

        selected = LibraryStorage.getInstance().find(Resource.AvMaterial, new QueryParameters()).get(position1 - 1);

        if (selected != null) {
            showDocumentDetails(selected);
            documentCursor.put(chatId, selected);
        }
        return "checkout_final";
    }

    private void selectJournalIssueForCheckout(Long chatId) {
        JournalIssueEntry selectedJournal;
        String msg1 = update.getMessage().getText();
        int position;
        try {
            position = Integer.parseInt(msg1);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            sendMessage("Input is not a number.");
            return;

        }
        selectedJournal = LibraryStorage.getInstance().find(Resource.JournalIssue, new QueryParameters()).get(position-1);

        if(selectedJournal != null) {
            showDocumentDetails(selectedJournal);
            documentCursor.put(chatId, selectedJournal);
        }
    }

    private String displayItemsForCheckout() {
        String type = update.getCallbackQuery().getData();

        switch (type) {
            case "Book":
                listBooks();
                return "book";
            case "AV Material":
                listAvMaterials();
                return "avmaterial";
            case "Journal Issue":
                listJournalIssues();
                return "journalissue";
            default:
                throw new RuntimeException("Invalid type");
        }
    }

    private void checkOutItem(Long chatId){

        if(update.getCallbackQuery().getData().equals("Checkout")) {
            CheckOutCommand command = new CheckOutCommand(currentUser.get(chatId),
                                                          documentCursor.get(chatId));

            Result result = command.execute(LibraryStorage.getInstance());
            String message = "";
            switch (result) {
                case Success:
                    message = documentCursor.get(chatId).getItem().getTitle()
                              + " is checked out successfully.";
                    break;
                case Warning:
                    message = "Warning: " + result.getInfo();
                    break;
                case Failure:
                    message = "Failure: " + result.getInfo();
                    break;
            }
            keyboardUtils.showMainMenuKeyboard(
                    currentUser.get(chatId).getUser(), message);

        } else {
            keyboardUtils.showMainMenuKeyboard(
                    currentUser.get(chatId).getUser(), "Checkout cancelled");
        }
    }
}
