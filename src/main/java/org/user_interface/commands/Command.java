package org.user_interface.commands;

import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.*;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.user_interface.ui.Bot;
import org.user_interface.ui.KeyboardUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public abstract class Command {

    public static KeyboardUtils keyboardUtils;
    static HashMap<Long, UserEntry> currentUser = new HashMap<>();
    private AbsSender sender = new Bot();
    public Update update;

    static HashMap<Long, ItemEntry> documentCursor = new HashMap<>();
    static HashMap<Long, UserEntry> userCursor = new HashMap<>();

    //executes an action, then returns an object that contains some
    //information back to the Bot class
    public String run(Update update, String info) {
        this.update = update;
        keyboardUtils = new KeyboardUtils(update);
        return run(info);
    }

    protected abstract String run(String info);


    void sendMessage(String text) {
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

    void listBooks() {
        StringBuilder builder = new StringBuilder();
        List<BookEntry> books = LibraryStorage.getInstance().find(Resource.Book, new QueryParameters());
        for (int i = 0; i < books.size(); i++) {
            String name = books.get(i).getItem().getTitle();
            System.out.println(name);
            builder.append(i + 1)
                   .append(". ")
                   .append(name)
                   .append("\n\n");
        }
        if(builder.length() > 0) {
            sendMessage("Here's a " +
                    "list of all the specified documents in the library. Enter the number of the book you want:");
            sendMessage(builder.toString());
        } else sendMessage("There are no books in the library.");
    }

    void listAvMaterials() {
        StringBuilder builder = new StringBuilder();
        List<AvMaterialEntry> avMaterials = LibraryStorage.getInstance().find(Resource.AvMaterial, new QueryParameters());
        for (int i = 0; i < avMaterials.size(); i++) {
            String name = avMaterials.get(i).getItem().getTitle();
            builder.append(i + 1)
                   .append(". ")
                   .append(name)
                   .append("\n");
        }

        if(builder.length() > 0) {
            sendMessage("Here's a " +
                    "list of all the specified documents in the library. Enter the number of the book you want:");
            sendMessage(builder.toString());
        } else sendMessage("There are no AV Materials in the library.");
    }

    void listJournalIssues() {
        StringBuilder builder = new StringBuilder();
        List<JournalIssueEntry> journalIssues = LibraryStorage.getInstance().find(Resource.JournalIssue, new QueryParameters());
        for(int i = 0; i < journalIssues.size(); i++) {
            String name = journalIssues.get(i).getItem().getTitle();
            builder.append(i + 1)
                   .append(". ")
                   .append(name)
                   .append("\n");
        }
        if(builder.length() > 0) {
            sendMessage("Here's a " +
                    "list of all the specified documents in the library. Enter the number of the book you want:");
            sendMessage(builder.toString());
        } else sendMessage("There are no Journal Issues in the library.");
    }

    void listJournalArticles() {
        StringBuilder builder = new StringBuilder();
        List<JournalArticleEntry> articles = LibraryStorage.getInstance().find(Resource.JournalArticle, new QueryParameters());
        for(int i = 0; i < articles.size(); i++) {
            String name = articles.get(i).getItem().getTitle();
            builder.append(i+1)
                   .append(". ")
                   .append(name)
                   .append("\n");
        }
        if(builder.length() > 0) {
            sendMessage("Here's a " +
                    "list of all the specified documents in the library. Enter the number of the book you want:");
            sendMessage(builder.toString());
        } else sendMessage("There are no Journal Articles in the library.");
    }

    void showDocumentDetails(ItemEntry item) {
        String bookDetails = item.getItem().toString() +
                "\nCopies: " + item.getItem().getCopiesNum();

        keyboardUtils.setInlineKeyBoard(bookDetails, new ArrayList<String>(){{
            add("Checkout");
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

    String authorizationChecker(UserEntry e) {
        if(!e.getUser().getType().equals("Librarian")) {
            keyboardUtils.showMainMenuKeyboard(e.getUser(),
                    "You're not allowed to perform this operation");
            return "menu_main";
        }
        else return null;
    }
}
