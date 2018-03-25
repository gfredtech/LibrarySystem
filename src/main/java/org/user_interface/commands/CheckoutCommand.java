package org.user_interface.commands;

import org.controller.CheckOutCommand;

import org.storage.QueryParameters;
import org.storage.SqlStorage;
import org.storage.resources.*;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import java.util.ArrayList;
import java.util.HashMap;

public class CheckoutCommand extends Command {
    HashMap<Long, UserEntry> currentUser = new HashMap<>();
    HashMap<Long, ItemEntry> documentCursor = new HashMap<>();


    @Override
    public String run(AbsSender sender, Update update, String info) {
        Long chatId;
        if(update.hasMessage()) {
            chatId = update.getMessage().getChatId();
        } else {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        }

        switch (info) {
            case "checkout_start":
                if (currentUser.containsKey(chatId)) {

                    keyboardUtils.setInlineKeyBoard(sender, update,
                            "Select the kind of document you want to checkout:", new
                                    ArrayList<String>() {{
                                        add("Book");
                                        add("AV Material");
                                        add("Journal Article");
                                        add("Journal Issue");
                                    }});
                    return "checkout_main";
                } else {
                    sendMessage(sender, update, "Please /login or /signup first before you can execute this command.");
                }

                break;

            case "Book":
                sendMessage(sender, update, "Here's a " +
                        "list of all Books in the library. Enter the number of the book you want:");
                listBooks(sender, update);
                return "checkout_selectbook";

            case "AV Material":
                sendMessage(sender, update, "Here's a list of all AV Materials in the library. Enter" +
                        "the number of the AV material you want");
                listAvMaterials(sender, update);
                return "checkout_selectavmaterial";

            case "checkout_selectbook":
                BookEntry bookSelected;
                String msg = update.getMessage().getText();
                System.out.println("Code reached: " + msg);
                int position;
                try {
                    position = Integer.parseInt(msg);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    sendMessage(sender, update, "Input is not a number.");
                    break;
                }

                bookSelected = SqlStorage.getInstance().find(Resource.Book, new QueryParameters()).get(position - 1);

                if (bookSelected != null) {
                    showDocumentDetails(sender, update, bookSelected.getItem(), "Book");
                    documentCursor.put(chatId, bookSelected);
                }
                return "checkout_book";
            case "checkout_selectavmaterial":
                AvMaterialEntry selected;
                String msg1 = update.getMessage().getText();
                int position1;
                try {
                    position1 = Integer.parseInt(msg1);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    sendMessage(sender, update, "Input is not a number.");
                    break;
                }
                selected = SqlStorage.getInstance().find(Resource.AvMaterial, new QueryParameters()).get(position1-1);

                if(selected != null) {
                    showDocumentDetails(sender, update, selected.getItem(), "AV Material");
                    documentCursor.put(chatId, selected);
                    return "checkout_avmaterial";
                }

            case "Checkout Book":
                try {
                    System.out.println(currentUser.get(chatId).getUser().getName());

                    new CheckOutCommand(SqlStorage.getInstance())
                            .checkOut();
                    keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId).getUser(),
                            documentCursor.get(chatId).getItem().getTitle() + " Checked out successfully.");
                    return "menu_";
                } catch (CheckOutCommand.CheckoutException e) {
                    keyboardUtils.showMainMenuKeyboard(sender, update,
                            currentUser.get(chatId).getUser(),
                            "Sorry, but you cannot check out the item now.");
                }
                return "logged_in";

                }
        return null;
    }


    public void setCurrentUser(Long chatId, UserEntry user) {
        this.currentUser.put(chatId, user);
    }
}
