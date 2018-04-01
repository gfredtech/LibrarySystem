package org.user_interface.commands;

import org.controller.CheckOutCommand;
import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.AvMaterialEntry;
import org.storage.resources.BookEntry;
import org.storage.resources.JournalIssueEntry;
import org.storage.resources.Resource;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import java.util.ArrayList;

import static org.controller.Command.Result;

public class CheckoutItemCommand extends Command {

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
                return "checkout_final";

            case "selectavmaterial":
                selectAvMaterialForCheckout(sender, update, chatId);
                return "checkout_final";

            case "selectjournalissue":
                selectJournalIssueForCheckout(sender, update, chatId);
                return "checkout_final";

            case "final":
                checkOutItem(sender, update, chatId);
                return "menu_main";
        }
        return null;
    }

    private void showCheckoutType(AbsSender sender, Update update) {
        keyboardUtils.setInlineKeyBoard(sender, update, "Select the kind of document you want to checkout:", new
                ArrayList<String>() {{
                    add("Book");
                    add("AV Material");
                    add("Journal Article");
                }});
    }

    private void selectBookForCheckout(AbsSender sender, Update update, Long chatId) {
        BookEntry bookSelected;
        String msg = update.getMessage().getText();
        int position;
        try {
            position = Integer.parseInt(msg);
        } catch (NumberFormatException e) {
            sendMessage(sender, update, "Input is not a number.");
            return;
        }
        bookSelected = LibraryStorage.getInstance().find(Resource.Book, new QueryParameters()).get(position - 1);

        if (bookSelected != null) {
            showDocumentDetails(sender, update, bookSelected);
            documentCursor.put(chatId, bookSelected);
        }
    }

    private void selectAvMaterialForCheckout(AbsSender sender, Update update, Long chatId) {
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

        selected = LibraryStorage.getInstance().find(Resource.AvMaterial, new QueryParameters()).get(position1 - 1);

        if (selected != null) {
            showDocumentDetails(sender, update, selected);
            documentCursor.put(chatId, selected);
        }
    }

    private void selectJournalIssueForCheckout(AbsSender sender, Update update, Long chatId) {
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
        selectedJournal = LibraryStorage.getInstance().find(Resource.JournalIssue, new QueryParameters()).get(position-1);

        if(selectedJournal != null) {
            showDocumentDetails(sender, update, selectedJournal);
            documentCursor.put(chatId, selectedJournal);
        }
    }

    private String displayItemsForCheckout(AbsSender sender, Update update) {
        String type = update.getCallbackQuery().getData();

        switch (type) {
            case "Book":
                listBooks(sender, update);
                return "book";
            case "AV Material":
                listAvMaterials(sender, update);
                return "avmaterial";
            case "Journal Issue":
                listJournalIssues(sender, update);
                return "journalissue";
            default:
                //TODO
                throw new RuntimeException("Invalid type");
        }
    }

    private void checkOutItem(AbsSender sender, Update update, Long chatId){

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
            keyboardUtils.showMainMenuKeyboard(sender, update,
                    currentUser.get(chatId).getUser(), message);

        } else {
            keyboardUtils.showMainMenuKeyboard(sender, update,
                    currentUser.get(chatId).getUser(), "Checkout cancelled");
        }
    }
}
