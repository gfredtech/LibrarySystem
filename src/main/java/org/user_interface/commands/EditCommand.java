package org.user_interface.commands;

import org.resources.Book;
import org.resources.BookFactory;
import org.resources.Item;
import org.resources.User;
import org.storage.QueryParameters;
import org.storage.SqlStorage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class EditCommand extends Command {
    @Override
    public String run(AbsSender sender, Update update, String info) {
        BookFactory bookFactory = new BookFactory();
        Long chatId;
        if (update.hasMessage()) chatId = update.getMessage().getChatId();
        else chatId = update.getCallbackQuery().getMessage().getChatId();
        switch (info) {
            case "edit_start":
                keyboardUtils.showCRUDkeyboard(sender, update);
                System.out.println("keyboard shown");
                return "edit_main";

            case "Edit Document":
                System.out.println("next code after keyboard shown");
                String input = null;
                input = update.getMessage().getText();

                if(input != null && input.equals("Edit Document")) {
                    sendMessage(sender, update, "Select the type of document you want to edit");
                    keyboardUtils.setInlineKeyBoard(sender, update, "Types:",
                            new ArrayList<String>() {{
                                add("Edit Book");
                                add("Edit Av Material");
                                add("Edit Journal Issue");
                                add("Edit Journal Article");
                            }});
                    return "edit_documenttype";
                }

            case "Edit Book":
                sendMessage(sender, update, "Here's a list of books. Select the one you'd like to edit");
                listBooks(sender, update);
                return "edit_booklist";

            case "edit_booklist":
                System.out.println("kwasiabeema");
                Book book = null;
                input = update.getMessage().getText();
                int index = Integer.parseInt(input);
                book = SqlStorage.getInstance().findBooks(new QueryParameters()).get(index - 1);

                if (book != null) {
                    bookCursor.put(chatId, book);
                    String accountDetails = "Name: " + book.getTitle() +
                            "\nNo. of copies: " + book.getCopiesNum() + "\nAuthors:" + book.getAuthors() +
                            "\nPublicationDate: " + book.getPublicationDate();

                    sendMessage(sender, update, accountDetails);
                    String parameterlist = "title `Name` \ncopies `new number of copies` \n" +
                            "*If you want to delete the book, type delete*";
                    sendMessage(sender, update, "This is the parameter list for editing a use book; first type the key of the " +
                            "parameter you want to edit, followed by its new value, then separate each with a comma(,)");
                    sendMessage(sender, update, parameterlist);
                    return "edit_bookparams";
                }
                break;

            case "edit_bookparams":
                Book currentBook = bookCursor.get(chatId);
                editNumberOfCopies = currentBook.getCopiesNum();
                editTitle = currentBook.getTitle();
                System.out.println("*** " + currentBook.getTitle());

                input = update.getMessage().getText();
                if (input.equals("delete")) {
                    SqlStorage.getInstance().removeBook(currentBook.getId());
                    keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId),
                            currentBook.getTitle() + " removed successfully");
                    return "menu";
                }

                List<String> items = Arrays.asList(input.split("\\s*,\\s*"));
                String temp;
                for(String i: items) {
                    if(i.startsWith("title")) {
                        temp = i.replace("title ", "");
                        SqlStorage.getInstance().updateBook(
                                currentBook.getId(),
                                new QueryParameters().add("title", temp));

                    } else if(i.startsWith("copies")) {
                        temp = i.replace("copies ", "");
                        SqlStorage.getInstance().updateBook(
                                currentBook.getId(),
                                new QueryParameters().add("copy_num", Integer.parseInt(temp)));
                    }
                }
                keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId),
                        "Edit done successfully");

                break;


            case "edit_userlist":
                String msg = update.getMessage().getText();

                int position;
                try {
                    position = Integer.parseInt(msg);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    sendMessage(sender, update, "Input is not a number.");
                    break;
                }

                userCursor = SqlStorage.getInstance().findUsers(new QueryParameters()).get(position - 1);

                if (userCursor != null) {
                    String accountDetails = "Name: " + userCursor.getName() +
                            "\nAddress: " + userCursor.getAddress() + "\nPhone Number: " + userCursor.getPhoneNumber()
                            + "\nType:" + userCursor.getSubtype() + "\nCard number: " + userCursor.getCardNumber();
                    sendMessage(sender, update, accountDetails);
                    String parameterlist = "name `Name`\n address `Address`\n phone `Phone Number`\n type `Type`\n\n" +
                            "*If you want to delete a user, type `delete` followed by user's card number";
                    sendMessage(sender, update, "This is the parameter list for editing a user; first type the key of the " +
                            "parameter you want to edit, followed by its new value, then separate each with a comma(,)");
                    sendMessage(sender, update, parameterlist);
                    return "edit_userparams";
                }

                break;

            case "edit_userparams":
                editNumberOfCopies = bookCursor.get(chatId).getCopiesNum();
                input = update.getMessage().getText();
                if (input.startsWith("delete")) {
                    input.replace("delete", "");
                    SqlStorage.getInstance().removeUser(userCursor.getCardNumber());
                    keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId),
                            userCursor.getName() + " removed successfully");
                    return "menu";
                }
                items = Arrays.asList(input.split("\\s*,\\s*"));
                for(String i: items) {
                    if(i.startsWith("name")) {
                        temp = i.substring(i.lastIndexOf("name"), i.length());
                        QueryParameters params = new QueryParameters().add("name", temp);
                        SqlStorage.getInstance().updateUser(userCursor.getCardNumber(), params);

                    } else if(i.startsWith("address")) {
                        temp = i.substring(i.lastIndexOf("address"), i.length());
                        QueryParameters params = new QueryParameters().add("address", temp);
                        SqlStorage.getInstance().updateUser(userCursor.getCardNumber(), params);


                    } else if(i.startsWith("type")) {
                        temp = i.substring(i.lastIndexOf("type"), i.length());
                        QueryParameters params = new QueryParameters();
                        if(temp.equals("Student") || temp.equals("Faculty")) {
                            params.add("type", "Patron").add("subtype", temp);
                        } else if(temp.equals("Librarian")) {
                            params.add("type", temp);
                        }
                        SqlStorage.getInstance().updateUser(userCursor.getCardNumber(), params);
                    }
                }

                keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId),
                        "Edit done successfully");
        }
        return null;
    }

    public void getCurrentUser(Update update, User user) {
        Long chatId = update.getMessage().getChatId();
        currentUser.put(chatId, user);
    }

    HashMap<Long, User> currentUser = new HashMap<>();
    User userCursor;

    HashMap<Long, Book> bookCursor = new HashMap<>();

    String editTitle;
    int editNumberOfCopies;
    LocalDate publicationDate;

}
