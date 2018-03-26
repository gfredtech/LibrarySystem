package org.user_interface.commands;

import org.items.AvMaterial;
import org.items.Book;
import org.items.JournalArticle;
import org.items.User;
import org.storage.QueryParameters;
import org.storage.SqlStorage;
import org.storage.resources.CheckoutEntry;
import org.storage.resources.ItemEntry;
import org.storage.resources.Resource;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReturnItemCommand extends Command {


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
        CheckoutEntry entry;
        String number = update.getMessage().getText();
        int index;
        try {
            index = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            sendMessage(sender, update, "Invalid");
            return;
        }

        entry = checkoutEntryMap.get(chatId).get(index - 1);
        if (entry != null) documentCursor.put(chatId, entry.getItem());

        System.out.println(entry.getItem().getItem().getTitle());

        //TODO: return item
        new org.controller.ReturnCommand(currentUser.get(chatId), entry.getItem()).execute(
                SqlStorage.getInstance());
        keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId).getUser(), 
                "Book returned successfully!!");

        }


    private String showCheckedOutDocuments(AbsSender sender, Update update, Long chatId) {
        List<CheckoutEntry> listOfItems;
        String t = update.getCallbackQuery().getData();
        t = t.substring(t.indexOf(" ")).
                toLowerCase().trim().replace(" ", "_");
        type.put(chatId, t);
        listOfItems = listCheckedOutDocs(sender, update, currentUser.get(chatId).getUser(), chatId, t);
        if(listOfItems == null) return "menu_main";

        checkoutEntryMap.put(chatId, listOfItems);
        return "return_indexnumber";
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

    List<CheckoutEntry> listCheckedOutDocs(AbsSender sender, Update update, User user, Long chatId, String type) {
        System.out.println(type);
        List<CheckoutEntry> entries = null;
        if(type.equals("book")) {
            entries = SqlStorage.getInstance().find(
                    Resource.Checkout, new QueryParameters().add("item_type", "book")
            .add("user_id", user.getCardNumber()));
        }else if(type.equals("av_material")) {
            entries = SqlStorage.getInstance().find(
                    Resource.Checkout, new QueryParameters().add("user_id", user.getCardNumber())
                    .add("item_type", "av_material"));
        } else if(type.equals("journal_article")) {
            entries = SqlStorage.getInstance().find(
                    Resource.Checkout, new QueryParameters().add("user_id", user.getCardNumber())
                            .add("item_type", "journal_issue"));
        }
        StringBuilder items = new StringBuilder();
        int i = 1;
        Book book; AvMaterial avMaterial; JournalArticle article;
        if (entries != null && entries.size() > 0) {
            sendMessage(sender, update,
                    "This is the list of current " + type + "s checked out by you:\n" );

        } else {
            keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId).getUser(), "You have no " + type + " checked out");
            return null;
        }

        for(CheckoutEntry c: entries) {
                ItemEntry item = c.getItem();
                items.append(i);
                items.append(". ");
                items.append(item.getItem().getTitle());
                items.append(" by ");
                if(item.getResourceType().equals(Resource.Book)) {
                    book = (Book) item.getItem();
                    items.append(book.getAuthors());
                } else if(item.getResourceType().equals(Resource.AvMaterial)) {
                    avMaterial = (AvMaterial) item.getItem();
                    items.append(avMaterial.getAuthors());
                } else if(item.getResourceType().equals(Resource.JournalIssue)) {
                    article = (JournalArticle) item.getItem();
                    items.append(article.getAuthors());
                }

                items.append("\n");
                i+=1;
            }

        sendMessage(sender, update, items.toString());

        return entries;
    }

    static HashMap<Long, String> type = new HashMap<>();
    static HashMap<Long, List<CheckoutEntry>> checkoutEntryMap = new HashMap<>();
}
