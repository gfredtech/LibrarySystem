package org.user_interface.commands;

import org.items.*;
import org.storage.QueryParameters;
import org.storage.SqlStorage;
import org.storage.resources.*;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.user_interface.ui.KeyboardUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public abstract class Command {

    public KeyboardUtils keyboardUtils = new KeyboardUtils();

    static HashMap<Long, UserEntry> currentUser = new HashMap<>();

    static HashMap<Long, ItemEntry> documentCursor = new HashMap<>();
    static HashMap<Long, UserEntry> userCursor = new HashMap<>();

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
        message.enableMarkdown(true);
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
            builder.append((i + 1) + ". " + name + "\n");
        }
        sendMessage(sender, update, builder.toString());
    }

    void listJournalIssues(AbsSender sender, Update update) {
        StringBuilder builder = new StringBuilder();
        List<JournalIssueEntry> journalIssues = SqlStorage.getInstance().find(Resource.JournalIssue, new QueryParameters());
        for(int i = 0; i < journalIssues.size(); i++) {
            String name = journalIssues.get(i).getItem().getTitle();
            builder.append((i+1) + ". " + name + "\n");
        }
        sendMessage(sender, update, builder.toString());
    }

    void listJournalArticles(AbsSender sender, Update update) {
        StringBuilder builder = new StringBuilder();
        List<JournalArticleEntry> articles = SqlStorage.getInstance().find(Resource.JournalArticle, new QueryParameters());
        for(int i = 0; i < articles.size(); i++) {
            String name = articles.get(i).getItem().getTitle();
            builder.append((i+1) + ". " + name + "\n");
        }
        sendMessage(sender, update, builder.toString());
    }

    void showDocumentDetails(AbsSender sender, Update update, ItemEntry item, String type) {
        String bookDetails = item.getItem().toString() +
                "\nCopies: " + item.getItem().getCopiesNum();

        keyboardUtils.setInlineKeyBoard(sender, update, bookDetails, new ArrayList<String>(){{
            add("Checkout " + type);
            add("Cancel Checkout");
        }});

    }


    LocalDate parseDate(String a) {
        DateFormat df = new SimpleDateFormat("MMddyyyy", Locale.ENGLISH);
        Date result = null;
        try {
            result = df.parse(a);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(result != null) return result.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return null;
    }
}
