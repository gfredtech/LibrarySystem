package org.user_interface.ui;

import org.resources.*;
import org.storage.QueryParameters;
import org.storage.SqlStorage;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.user_interface.commands.Command;
import org.user_interface.commands.StartCommand;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.*;

public class Bot extends TelegramLongPollingBot {



    // entry point of bot
    @Override
    public void onUpdateReceived(Update update) {

        Message message = update.getMessage();


        if (update.hasMessage() && message.hasText()) {
            Long chatId = message.getChatId();
            String x = message.getText();
            Command command;
            System.out.println(executor);
            if(executor.containsKey(chatId)) {
                executor.get(chatId).processUpdate(update);
                if(executor.get(chatId).isDone()) {
                    executor.remove(chatId);
                }
            }
            switch (x) {
                case "/start":
                    command = new StartCommand(message.getFrom(), message.getChat());

                    SendMessage reply = command.run();
                    reply.setReplyMarkup(new ReplyKeyboardRemove()); // hides keyboard in case it's showing already
                    try {
                        execute(reply);

                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }

                    break;

                case "/login":
                    executor.put(chatId, new LoginExecutor(this));
                    executor.get(chatId).processUpdate(update);
                    System.out.println(executor);
                    break;

                case "/signup":
                    executor.put(chatId, new SignUpExecutor(this));
                    executor.get(chatId).processUpdate(update);
                    System.out.println(executor);
                    break;

                case "Checkout Document":
                    executor.put(chatId, new BookingExecutor(this));
                    executor.get(chatId).processUpdate(update);
                    System.out.println(executor);
                    break;

                case "Return Document":
                    executor.put(chatId, new ReturnExecutor(this));
                    executor.get(chatId).processUpdate(update);
                    System.out.println(executor);
                    break;

                case "Edit":
                    showCRUDkeyboard(chatId);
                    previous.put(chatId, "/edit");

                    break;

                case "Add Document":
                    sendMessage(chatId, "Enter the title of document to be added");
                    previous.put(chatId, "/add_document_title");
                    break;

                case "Modify Document":
                    executor.put(chatId, new DocumentModifyingExecutor(this));
                    executor.get(chatId).processUpdate(update);
                    System.out.println(executor);
                    break;

                default:
                    String input = previous.get(chatId);
                    if (input != null) {
                        switch (input) {
                            case "/add_document_title":
                                factory.setTitle(x);
                                sendMessage(chatId, "Enter the name of the authors, separate by comma(,)");
                                previous.put(chatId, "/add_document_authors");
                                break;

                            case "/add_document_authors":
                                String [] authors = x.split(",");
                                factory.setAuthors(Arrays.asList(authors));
                                sendMessage(chatId, "Enter the name of the publisher");
                                previous.put(chatId, "/add_document_publisher");
                                break;

                            case "/add_document_publisher":
                                factory.setPublisher(x);
                                sendMessage(chatId, "Enter the number of copies you want to add to the library");
                                previous.put(chatId, "/add_document_copies");
                                break;

                            case "/add_document_copies":
                                factory.setCopiesNum(Integer.parseInt(x));

                                setInlineKeyBoard(chatId, "What type of document is it?",
                                        new ArrayList<String>() {{
                                            add("Add Book");
                                            add("Add Av Material");
                                            add("Add Journal Article");
                                            add("Add Journal Issue");
                                        }});
                                previous.put(chatId, "/add_document_complete");
                                break;
                                }
                        }

                        break;
            }

        } else if(update.hasCallbackQuery()) {
            String call_data = update.getCallbackQuery().getData();
            Long chatId =  update.getCallbackQuery().getMessage().getChatId();

            switch (call_data) {
                //TODO: Add document to its appropriate category
                case "Add Book":
                    if (previous.get(chatId).equals("/add_document_complete")) {
                        showMainMenuKeyboard(chatId, "Book added successfully!");
                        previous.put(chatId, "/menu");
                    }
                    break;

                case "Add Av Material":
                    if(previous.get(chatId).equals("/add_document_complete")) {
                        showMainMenuKeyboard(chatId, "AV Material added successfully!");
                        previous.put(chatId, "/menu");
                    }
                    break;

                case "Add Journal Article":
                    if(previous.get(chatId).equals("/add_document_complete")) {
                        showMainMenuKeyboard(chatId, "AV Material added successfully!");
                        previous.put(chatId, "/menu");
                    }
                    break;

                case "Add Journal Issue":
                    if(previous.get(chatId).equals("/add_document_complete")) {
                        showMainMenuKeyboard(chatId, "Journal Issue Added Successfully");
                        previous.put(chatId, "/menu");
                    }
                    break;
            }
        }
    }

    @Override
    public String getBotUsername() {

        return "konyvtar_bot";
    }

    @Override
    public String getBotToken() {

       return "404457992:AAE0dHw07sHw8woSFiMJSebrQCK2aUyN8CM";
    }

    void showMainMenuKeyboard(Long chatId, String msg) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("Checkout Document");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("Return Document");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("Search");
        keyboard.add(row);

        if(currentUser.get(chatId).getType().equals("Librarian")) {
            row = new KeyboardRow();
            row.add("Edit");
            keyboard.add(row);
        }


        row = new KeyboardRow();
        row.add("️Settings");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("Logout");
        keyboard.add(row);
        

        keyboardMarkup.setKeyboard(keyboard);

        SendMessage message = new SendMessage().setChatId(chatId).setText(msg);
        message.setReplyMarkup(keyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }



    void sendMessage(Long chatId, String message) {
        try {
            execute(new SendMessage().
                    setChatId(chatId).setText(message).setReplyMarkup(new ReplyKeyboardRemove()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    void createAccount(User user) {
        SqlStorage.getInstance().addUser(user);
    }

    void setInlineKeyBoard(Long ChatId, String message, List<String> commands) {
        SendMessage msg = new SendMessage();

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        for(String i: commands) {
            row.add(new InlineKeyboardButton().
                    setText(i).setCallbackData(i.trim()));
            rows.add(row);
            row = new ArrayList<>();
        }
        msg.setChatId(ChatId);
        msg.setText(message);

        markup.setKeyboard(rows);
        msg.setReplyMarkup(markup);

        try {
            execute(msg);
        }catch (TelegramApiException e) {
            e.printStackTrace();
        }


    }

    void showBookDetails(Long chatId, Book book) {
        String details = book.toString() +
                 "\nCopies: " + book.getCopiesNum();
        sendMessage(chatId, details+"\n"+
                "Checkout book\n"+
                "Cancel");
    }

    void showAvMaterialDetails(Long chatId, AvMaterial selected) {
        String details = selected.toString() +
                "\nCopies: " + selected.getCopiesNum();
        sendMessage(chatId, details+"\n"+
                    "Checkout AV material\n"+
                    "Cancel");
    }

    void showArticleDetails(Long chatId, JournalArticle selected) {
        String details = selected.toString() +
                "\nCopies: " + selected.getCopiesNum();
        sendMessage(chatId, details+"\n"+
                "Checkout article\n"+
                "Cancel");
    }

    void showCRUDkeyboard(Long ChatId) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("Add Document");
        row.add("Modify Document");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("Add User");
        row.add("Modify User");
       keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);

        SendMessage message = new SendMessage().setChatId(ChatId).setText("Choose an option");
        message.setReplyMarkup(keyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    void listBooks(Long chatId) {
        StringBuilder builder = new StringBuilder();
        List<Book> books = SqlStorage.getInstance().findBooks(new QueryParameters());
        for (int i = 0; i < books.size(); i++) {
            String name = books.get(i).getTitle();
            System.out.println(name);
            builder.append(books.get(i).getId() + ". " + name + "\n\n");
        }

        sendMessage(chatId, builder.toString());
    }

    void listArticles(Long chatId) {
        StringBuilder builder = new StringBuilder();
        List<JournalArticle> articles = SqlStorage.getInstance().findArticles(new QueryParameters());
        for (int i = 0; i < articles.size(); i++) {
            String name = articles.get(i).getTitle();
            System.out.println(name);
            builder.append(articles.get(i).getId() + ". " + name + "\n");
        }
        sendMessage(chatId, builder.toString());
    }

    void listJournalIssues(Long chatId) {
        StringBuilder builder = new StringBuilder();
        List<JournalIssue> issues = SqlStorage.getInstance().findJournals(new QueryParameters());
        for (int i = 0; i < issues.size(); i++) {
            String name = issues.get(i).getTitle();
            System.out.println(name);
            builder.append(issues.get(i).getId() + ". " + name + "\n");
        }
        sendMessage(chatId, builder.toString());
    }

    void listAvMaterials(Long chatId) {
        StringBuilder builder = new StringBuilder();
        List<AvMaterial> avMaterials = SqlStorage.getInstance().findAvMaterials(new QueryParameters());
        for (int i = 0; i < avMaterials.size(); i++) {
            String name = avMaterials.get(i).getTitle();
            builder.append(avMaterials.get(i).getId() + ". " + name + "\n");
        }

        sendMessage(chatId, builder.toString());

    }

    Map<Long, Executor> executor = new HashMap<>();

    Map<Long, String> previous = new HashMap<>();

    // tracks the current book that's about to be checked out by a user
    Map<Long, Item>  itemCursor = new HashMap<>();
    Map<Long, User> currentUser = new HashMap<>();

    BookFactory factory = new BookFactory();
}

