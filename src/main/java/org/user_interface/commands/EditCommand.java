package org.user_interface.commands;

import org.resources.Book;
import org.resources.User;
import org.storage.QueryParameters;
import org.storage.SqlStorage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class EditCommand extends Command {
    @Override
    public String run(AbsSender sender, Update update, String info) {
        Long chatId;
        if (update.hasMessage()) chatId = update.getMessage().getChatId();
        else chatId = update.getCallbackQuery().getMessage().getChatId();
        switch (info) {
            case "edit_start":
                keyboardUtils.showCRUDkeyboard(sender, update);
                System.out.println("this was reached");
                return "edit_main";

            case "edit_main":
                String input = null;
                input = update.getMessage().getText();
                System.out.println("reached");

                if(input != null && input.startsWith("Add")) {
                    return input;

                } else if(input.equals("Edit Document")) {
                    System.out.println("this");
                    sendMessage(sender, update, "Select the type of document you want to edit");
                    keyboardUtils.setInlineKeyBoard(sender, update, "Types:",
                            new ArrayList<String>() {{
                                add("Edit Book");
                                add("Edit Av Material");
                                add("Edit Journal Issue");
                                add("Edit Journal Article");
                            }});
                    return "edit_documenttype";
                } else if(input.equals("Edit User")) {
                    sendMessage(sender, update, "This is a lit of all users in the" +
                            " library system. Select the user whose information you want to edit");
                    listBooks(sender, update);
                    return "edit_userlist";

                }

                break;
            case "edit_userlist":
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
                input = update.getMessage().getText();
                if (input.startsWith("delete")) {
                    input = input.substring(input.lastIndexOf("delete"), input.length());
                    SqlStorage.getInstance().removeUser(userCursor.getCardNumber());
                    keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId),
                            userCursor.getName() + " removed successfully");
                    return "menu";
                }
                List<String> items = Arrays.asList(input.split("\\s*,\\s*"));
                String temp;
                for(String i: items) {
                    if(i.startsWith("name")) {
                        temp = i.substring(i.lastIndexOf("title"), i.length());
                        User user = new User(userCursor.getCardNumber(), temp,
                                userCursor.getType(), userCursor.getSubtype());
                        user.setAddress(userCursor.getAddress());
                        user.setPasswordHash(userCursor.getPasswordHash());
                        user.setLogin(userCursor.getLogin());
                        user.setPhoneNumber(userCursor.getPhoneNumber());
                        SqlStorage.getInstance().updateUser(userCursor.getCardNumber(), user);


                    } else if(i.startsWith("address")) {
                        temp = i.substring(i.lastIndexOf("address"), i.length());
                        User user = new User(userCursor.getCardNumber(), userCursor.getName(),
                                userCursor.getType(), userCursor.getSubtype());
                        user.setAddress(temp);
                        user.setPasswordHash(userCursor.getPasswordHash());
                        user.setLogin(userCursor.getLogin());
                        user.setPhoneNumber(userCursor.getPhoneNumber());
                        SqlStorage.getInstance().updateUser(userCursor.getCardNumber(), user);


                    } else if(i.startsWith("type")) {
                        temp = i.substring(i.lastIndexOf("type"), i.length());
                        User user = null;
                        if(temp.equals("Student") || temp.equals("Faculty")) {
                            user = new User(userCursor.getCardNumber(), userCursor.getName(), "Patron", temp);
                        } else if(temp.equals("Librarian")) {
                            user = new User(userCursor.getCardNumber(), userCursor.getName(), temp, temp);
                        }

                        user.setAddress(temp);
                        user.setPasswordHash(userCursor.getPasswordHash());
                        user.setLogin(userCursor.getLogin());
                        user.setPhoneNumber(userCursor.getPhoneNumber());
                        SqlStorage.getInstance().updateUser(userCursor.getCardNumber(), user);
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

}
