package org.user_interface.commands;

import org.items.Item;
import org.storage.QueryParameters;
import org.storage.SqlStorage;
import org.storage.resources.AvMaterialEntry;
import org.storage.resources.BookEntry;
import org.storage.resources.Resource;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.user_interface.ui.KeyboardUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class Command {

    KeyboardUtils keyboardUtils = new KeyboardUtils();

    //executes an action, then returns an object that contains some
    //information back to the Bot class
    public abstract String run(AbsSender sender, Update update, String info);


    void sendMessage(AbsSender sender, Update update, String text) {
        Chat chat;
        if (update.hasMessage()) {
            chat = update.getMessage().getChat();
        } else {
            chat = update.getCallbackQuery().getMessage().getChat();
        }

        SendMessage message = new SendMessage();
        message.setText(text);
        message.setChatId(chat.getId());
        message.setReplyMarkup(new ReplyKeyboardRemove()); // hides keyboard in case it's showing already

        try {
            sender.execute(message);
        }catch (TelegramApiException e) {
            e.printStackTrace();
        }
        }

    void listBooks(AbsSender sender, Update update) {
        StringBuilder builder = new StringBuilder();
        List<BookEntry> books = SqlStorage.getInstance().find(Resource.Book, new QueryParameters());
        for (int i = 0; i < books.size(); i++) {
            String name = books.get(i).getItem().getTitle();
            System.out.println(name);
            builder.append((i + 1) + ". " + name + "\n\n");
        }

        sendMessage(sender, update, builder.toString());


    }

    void listAvMaterials(AbsSender sender, Update update) {
        StringBuilder builder = new StringBuilder();
        List<AvMaterialEntry> avMaterials = SqlStorage.getInstance().find(Resource.AvMaterial, new QueryParameters());
        for (int i = 0; i < avMaterials.size(); i++) {
            String name = avMaterials.get(i).getItem().getTitle();
            builder.append((i + 1) + ". " + name + "\n\n");
        }

        sendMessage(sender, update, builder.toString());

    }





    void showDocumentDetails(AbsSender sender, Update update, Item item, String type) {
        String bookDetails = item.toString() +
                "\nCopies: " + item.getCopiesNum();

        keyboardUtils.setInlineKeyBoard(sender, update, bookDetails, new ArrayList<String>(){{
            add("Checkout " + type);
            add("Cancel Checkout");
        }});

    }



}
