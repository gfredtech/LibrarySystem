package org.user_interface.commands;

import org.controller.ReturnCommand;
import org.items.AvMaterial;
import org.items.Book;
import org.items.JournalArticle;
import org.items.User;
import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.CheckoutEntry;
import org.storage.resources.ItemEntry;
import org.storage.resources.Resource;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.controller.Command.Result;

public class ReturnItemCommand extends Command {


    @Override
    public String run(String info) {
        Long chatId;
        if(update.hasMessage()) chatId = update.getMessage().getChatId();
        else chatId = update.getCallbackQuery().getMessage().getChatId();

        switch (info) {
            case "startnext":
                showReturnType();
            return  "return_document";

            case "document":
                String s = showCheckedOutDocuments(chatId);
                System.out.println("stuff " + s);
                return s;

            case "indexnumber":
                returnDocument(chatId);
                return "menu_main";
        }
        return null;
    }

    private void returnDocument(Long chatId) {
        CheckoutEntry entry;
        String number = update.getMessage().getText();
        int index;
        try {
            index = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            sendMessage("Invalid");
            return;
        }

        entry = checkoutEntryMap.get(chatId).get(index - 1);
        if (entry != null) documentCursor.put(chatId, entry.getItem());

        assert entry != null;
        System.out.println(entry.getItem().getItem().getTitle());


        ReturnCommand c = new ReturnCommand(currentUser.get(chatId), entry.getItem());
        Result result = c.execute(LibraryStorage.getInstance());
        String message = "";
        switch (result) {
            case Success:
                message = entry.getItem().getItem().getTitle() + " returned successfully!";
                break;
            case Warning:
                message = "Warning: " + result.getInfo();
                break;
            case Failure:
                message = "Failure: " + result.getInfo();
                break;
        }
        keyboardUtils.showMainMenuKeyboard(
                currentUser.get(chatId).getUser(),message);
    }


    private String showCheckedOutDocuments(Long chatId) {
        List<CheckoutEntry> listOfItems;
        String t = update.getCallbackQuery().getData();
        t = t.substring(t.indexOf(" ")).
                toLowerCase().trim().replace(" ", "_");
        type.put(chatId, t);
        listOfItems = listCheckedOutDocs(update, currentUser.get(chatId).getUser(), chatId, t);
        if(listOfItems == null) return "menu_main";

        checkoutEntryMap.put(chatId, listOfItems);
        return "return_indexnumber";
    }
    private void showReturnType() {
        keyboardUtils.setInlineKeyBoard("Select the type of document you want to return",
                new ArrayList<String>() {{
                    add("Return Book");
                    add("Return Av Material");
                    add("Return Journal Issue");

                }});
    }

    private List<CheckoutEntry> listCheckedOutDocs(Update update,
                                                   User user, Long chatId, String type) {
        System.out.println(type);
        List<CheckoutEntry> entries = null;
        switch (type) {
            case "book":
                entries = LibraryStorage.getInstance().find(
                        Resource.Checkout, new QueryParameters()
                                .add("item_type", "book")
                                .add("user_id", user.getCardNumber()));
                break;
            case "av_material":
                entries = LibraryStorage.getInstance().find(
                        Resource.Checkout, new QueryParameters()
                                .add("user_id", user.getCardNumber())
                                .add("item_type", "av_material"));
                break;
            case "journal_article":
                entries = LibraryStorage.getInstance().find(
                        Resource.Checkout, new QueryParameters()
                                .add("user_id", user.getCardNumber())
                                .add("item_type", "journal_issue"));
                break;
        }
        StringBuilder items = new StringBuilder();
        int i = 1;
        Book book; AvMaterial avMaterial; JournalArticle article;
        if (entries != null && entries.size() > 0) {
            sendMessage(
                    "This is the list of current items checked out by you:\n" );

        } else {
            keyboardUtils.showMainMenuKeyboard(currentUser.get(chatId).getUser(), "You have no items  checked out");
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

        sendMessage(items.toString());

        return entries;
    }

    private static HashMap<Long, String> type = new HashMap<>();
    private static HashMap<Long, List<CheckoutEntry>> checkoutEntryMap = new HashMap<>();
}
