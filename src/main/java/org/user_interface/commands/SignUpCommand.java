package org.user_interface.commands;

import org.controller.AddUserCommand;
import org.items.User;
import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.Resource;
import org.storage.resources.UserEntry;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SignUpCommand extends Command {
    private String credentials;

    @Override
    public String run(AbsSender sender, Update update, String info) {
        Long chatId;
        if(update.hasMessage()) chatId = update.getMessage().getChatId();
        else chatId = update.getCallbackQuery().getMessage().getChatId();
        String message;
        switch (info) {
            case "startnext":
                message = "Welcome to the signup section.\n" +
                        "Please Enter your signup info,\nseparated by commas.\n\n" +
                        "`[fullname], [addresss], [phone], [login], [password], [type], [subtype]`\n\n" +
                        "Examples:\n" +
                        "John Smith, Innopolis Street 7, +12345678912, johnsmith, secret, Faculty, Instructor\n" +
                        "Jane Doe, Washington Street, +98765432101, janedoe, secret, Student";
                sendMessage(sender, update, message);
                return "signup_validator";

            case "validator":
                credentials = update.getMessage().getText();
                System.out.println(Arrays.toString(credentials.split(",")));
                if (credentials.split(",").length != 7) {
                    sendMessage(sender, update, "Input mismatch. Enter details again.");
                    return "signup_validator";
                } else {
                    signUpConfirm(sender, update, chatId, credentials);
                    return "signup_confirm";
                }


            case "confirm":
                System.out.println("Callback " + update.getCallbackQuery().getData());
                if (update.getCallbackQuery().getData().equals("Confirm")) {

                    org.controller.Command.Result res =
                            addUserEntryMap.get(chatId).execute(LibraryStorage.getInstance());
                    switch (res) {
                        case Success:
                            keyboardUtils.showMainMenuKeyboard(sender, update,
                                    currentUser.get(chatId).getUser(),
                                    "Account created successfully!");

                            return "menu_main";
                        case Failure:
                            keyboardUtils.showMainMenuKeyboard(sender, update,
                                    currentUser.get(chatId).getUser(),
                                    res.getInfo());
                    }

                } else {
                    sendMessage(sender, update,
                            "Signup cancelled.");
                    return "start";
                }
        }

        return null;
    }

    private void signUpConfirm(AbsSender sender, Update update,Long chatId, String info) {
        String fullName = info.split("[,]+")[0].trim();
        String address = info.split("[,]+")[1].trim();
        String phoneNumber = info.split("[,]+")[2].trim();
        String userName = info.split("[,]+")[3].trim();
        String password = info.split("[,]+")[4].trim();
        String type = info.split("[,]+")[5].trim();

        String accountDetails = "Name: " + fullName +
                "\nAddress: " + address + "\nPhone Number: " + phoneNumber +
                "\nLogin: " + userName
                + "\nType:" + type;


        User user = new User(newUserCardNumber());
        user.setName(fullName);
        user.setAddress(address);
        user.setPhoneNumber(phoneNumber);
        user.setLogin(userName);

        if(User.getTypes().contains(type)) {
            user.setType(type);

            }

            if(!(type.equals("Librarian") || type.equals("Student"))) {

                user.setSubtype(info.split("[,]+")[6].trim());
            }

            user.setPassword(password);
         AddUserCommand command = new AddUserCommand(user, currentUser.get(chatId).getUser());

         addUserEntryMap.put(chatId, command);

        keyboardUtils.setInlineKeyBoard(sender, update, accountDetails, new ArrayList<String>() {{
            add("Confirm");
            add("Cancel");
        }});
    }

    private int newUserCardNumber() {
        List<UserEntry> entryList =
                LibraryStorage.getInstance().find(Resource.User, new QueryParameters());
        int result = 0;
        for(UserEntry e: entryList) {
            if(e.getUser().getCardNumber() > result)
                result = e.getUser().getCardNumber();
        }
        return result + 1;
    }

    private static HashMap<Long, AddUserCommand> addUserEntryMap = new
            HashMap<>();
}