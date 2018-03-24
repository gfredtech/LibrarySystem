package org.user_interface.commands;

import org.controller.BookingController;
import org.resources.*;
import org.storage.QueryParameters;
import org.storage.SqlStorage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReturnCommand extends Command {


    @Override
    public String run(AbsSender sender, Update update, String info) {
        Long chatId;
        if(update.hasMessage()) chatId = update.getMessage().getChatId();
        else chatId = update.getCallbackQuery().getMessage().getChatId();

        switch (info) {
            case "startnext":
                showReturnType(sender, update);
            return  "return_document";

            case "document":
                return showCheckedOutDocuments(sender, update, chatId);

            case "indexnumber":
                returnDocument(sender, update, chatId);
                return "menu_main";
        }
        return null;
    }

    private void returnDocument(AbsSender sender, Update update, Long chatId) {
        Item document = null;
        String number = update.getMessage().getText();
        int index;
        try {
            index = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            sendMessage(sender, update, "Invalid");
            return;
        }

        if(type.get(chatId).equals("book")) {
            document = SqlStorage.getInstance().findBooks(new QueryParameters()).get(index - 1);
        } else if(type.get(chatId).equals("av_material")) {
            document = SqlStorage.getInstance().findAvMaterials(new QueryParameters()).get(index - 1);
        } else if(type.get(chatId).equals("journalissue")) {
            document = SqlStorage.getInstance().findAvMaterials(new QueryParameters()).get(index - 1);
        }

        if (document != null) {
            documentCursor.put(chatId, document);
        }
        try {
            /**  new ReturnController(SqlStorage.getInstance())
             .returnItem(currentUser.get(chatId).getCardNumber(),
             type.get(chatId), documentCursor.get(chatId).getId()); **/
            keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId),
                    documentCursor.get(chatId).getTitle() + " returned successfully.");
            documentCursor.remove(chatId);
        } catch (BookingController.CheckoutException e) {
            keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId),
                    "Sorry, there was an error returning this item");
        }
    }

    private String showCheckedOutDocuments(AbsSender sender, Update update, Long chatId) {
        String listOfItems;
        String t = update.getCallbackQuery().getData();
        t = t.substring(t.indexOf(" ")).
                toLowerCase().trim().replace(" ", "_");

        type.put(chatId, t);
        listOfItems = listCheckedOutDocs(currentUser.get(chatId), t);
        if (listOfItems.length() > 0) {
            sendMessage(sender, update,
                    "This is the list of current " + t + "s checked out by you:\n" + listOfItems);

            return "return_indexnumber";
        } else {
            sendMessage(sender, update, "You have no " + type.get(chatId) + " checked out");
            return "menu_main";
        }
    }
    private void showReturnType(AbsSender sender, Update update) {
        sendMessage(sender, update, "Select the type of document you want to return");
        keyboardUtils.setInlineKeyBoard(sender, update, "Types:",
                new ArrayList<String>() {{
                    add("Return Book");
                    add("Return Av Material");
                    add("Return Journal Issue");

                }});
    }

    String listCheckedOutDocs(User user, String type) {
        List<CheckoutRecord> checkedOut =
                SqlStorage.getInstance().getCheckoutRecordsFor(user.getCardNumber());
        StringBuilder docs = new StringBuilder();
        int i = 1;
        for (CheckoutRecord c : checkedOut) {
            if (c.item.getType().equals("book") && type.equals("book")) {
                Book b = SqlStorage.getInstance().getBook(c.item.getId()).get();
                docs.append(i);
                docs.append(". ");
                docs.append(b.getTitle());
                docs.append(" by ");
                docs.append(b.getAuthors());
                docs.append("\n");
                i += 1;
            } else if(c.item.getType().equals("av_material") && type.equals("av_material")) {
                AvMaterial a = SqlStorage.getInstance().getAvMaterial(c.item.getId()).get();
                docs.append(i);
                docs.append(". ");
                docs.append(a.getTitle());
                docs.append(" by ");
                docs.append(a.getAuthors());
                docs.append("\n");
                i += 1;
            } else if(c.item.getType().equals("journalissue") && type.equals("journalissue")) {
                JournalIssue j = SqlStorage.getInstance().getJournal(c.item.getId()).get();
                docs.append(i);
                docs.append(". ");
                docs.append(j.getTitle());
                docs.append("\n");
                i += 1;
            }
        }
        return docs.toString();
    }


    static HashMap<Long, String> type = new HashMap<>();
}
