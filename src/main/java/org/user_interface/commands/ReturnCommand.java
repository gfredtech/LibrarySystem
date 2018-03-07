package org.user_interface.commands;

import org.controller.BookingController;
import org.controller.ReturnController;
import org.resources.*;
import org.storage.QueryParameters;
import org.storage.SqlStorage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.user_interface.ui.KeyboardUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReturnCommand extends Command {
    HashMap<Long, User> currentUser = new HashMap<>();
    KeyboardUtils keyboardUtils = new KeyboardUtils();
    HashMap<Long, Item> documentCursor = new HashMap<>();

    @Override
    public String run(AbsSender sender, Update update, String info) {
        Long chatId;
        if(update.hasMessage()) {
            chatId = update.getMessage().getChatId();
        } else {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        }

        String listOfItems;
        switch (info) {
            case "return_start":
            sendMessage(sender, update, "Select the type of document you want to return");
            keyboardUtils.setInlineKeyBoard(sender, update, "Types:",
                    new ArrayList<String>() {{
                        add("Return Book");
                        add("Return Av Material");
                        add("Return Journal Issue");

                    }});
            return  "return_document";

            case "Return Book":
                listOfItems = listCheckedOutBooks(sender, update, currentUser.get(chatId));
                if(listOfItems.length() > 0) {
                    sendMessage(sender, update,
                            "This is the list of current books checked out by you:\n"+ listOfItems);
                    return "return_bookindexnumber";
                } else {
                    sendMessage(sender, update, "You have no books checked out");
                    return "menu";
                    }

            case "return_bookindexnumber":
                System.out.println("reacged");
                Book book;
                String number = update.getMessage().getText();
                int index;
                try {
                    index = Integer.parseInt(number);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    sendMessage(sender, update, "Invalid");
                    break;
                }

                book = SqlStorage.getInstance().findBooks(new QueryParameters()).get(index - 1);

                if (book != null) {
                    documentCursor.put(chatId, book);
                }

                try {
                    new ReturnController(SqlStorage.getInstance())
                            .returnItem(currentUser.get(chatId).getCardNumber(),
                                    "book", documentCursor.get(chatId).getId());
                    keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId),
                            documentCursor.get(chatId).getTitle() + " returned successfully.");
                } catch (BookingController.CheckoutException e) {
                   keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId),
                            "Sorry, there was an error returning this item");
                }
                return "menu";

            case "return_avmaterialindexnumber":
                AvMaterial avMaterial = null;
                String avMaterialIndex = update.getMessage().getText();
                int indexNumber;
                try {
                    indexNumber = Integer.parseInt(avMaterialIndex);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    sendMessage(sender, update, "Invalid index for Av Material");
                    break;
                }

                avMaterial = SqlStorage.getInstance().findAvMaterials(new QueryParameters()).get(indexNumber - 1);

                if (avMaterial != null) {
                    documentCursor.put(chatId, avMaterial);
                }


                new ReturnController(SqlStorage.getInstance())
                        .returnItem(currentUser.get(chatId)
                                .getCardNumber(), "av_material", documentCursor.get(chatId).getId());
                keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId),
                        documentCursor.get(chatId).getTitle() + " returned successfully.");

                return "menu";
        }

        return null;
    }

    public void getCurrentUser(Update update, User user) {
        Long chatId = update.getMessage().getChatId();
        currentUser.put(chatId, user);
    }

    String listCheckedOutBooks(AbsSender sender, Update update, User user) {
        List<CheckoutRecord> checkedOut =
                SqlStorage.getInstance().getCheckoutRecordsFor(user.getCardNumber());
        System.out.println(user.getCardNumber());
        StringBuilder books = new StringBuilder();
        int i = 1;
        for (CheckoutRecord c : checkedOut) {
            if (c.item.getType().equals("book")) {
                Book b = SqlStorage.getInstance().getBook(c.item.getId()).get();
                books.append(i);
                books.append(". ");
                books.append(b.getTitle());
                books.append(" by ");
                books.append(b.getAuthors());
                books.append("\n");
                i += 1;
            }
        }

        return books.toString();
    }

}
