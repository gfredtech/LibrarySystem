package org.user_interface.commands;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import java.util.ArrayList;

public class SignUpCommand extends Command {
    private String credentials;

    @Override
    public String run(AbsSender sender, Update update, String info) {
        String message;
        switch (info) {
            case "startnext":
                message = "Welcome to the signup section.\n" +
                        "Please Enter your signup info,\nseparated by commas.\n\n" +
                        "[fullname], [addresss], [phone], [login], [password], [type]\n\n" +
                        "Examples:\n" +
                        "John Smith, Innopolis Street 7, +12345678912, johnsmith, secret, Faculty\n" +
                        "Jane Doe, Washington Street, +98765432101, janedoe, secret, Student";
                sendMessage(sender, update, message);
                return "signup_validator";

            case "validator":
                credentials = update.getMessage().getText();
                System.out.println(credentials.split(",").toString());
                if (credentials.split(",").length != 6) {
                    sendMessage(sender, update, "Input mismatch. Enter details again.");
                    return "signup_validator";
                } else {
                    signUpConfirm(sender, update, credentials);
                    return "signup_confirm";
                }

                //Handle if student or faculty

            case "confirm":
                System.out.println("Callback " + update.getCallbackQuery().getData());
                if (update.getCallbackQuery().getData().equals("Confirm")) {
                    //TODO: create account here
                    sendMessage(sender, update, "Account created successfully! Use /login to login to your account");
                    return "start_startnext";
                } else {
                    sendMessage(sender, update,
                            "Signup cancelled. Use /login, or /signup again if you want to create an account");
                    return "start";
                }
        }

        return null;
    }

    void signUpConfirm(AbsSender sender, Update update, String info) {
        String fullName = info.split(",")[0].trim();
        String address = info.split(",")[1].trim();
        String phoneNumber = info.split(",")[2].trim();
        String userName = info.split(",")[3].trim();
        String type = info.split(",")[5].trim();

        String accountDetails = "Name: " + fullName +
                "\nAddress: " + address + "\nPhone Number: " + phoneNumber +
                "\nLogin: " + userName
                + "\nType:" + type;
        keyboardUtils.setInlineKeyBoard(sender, update, accountDetails, new ArrayList<String>() {{
            add("Confirm");
            add("Cancel");
        }});
    }

    void createAccount() {

       // User user = new User(1001, fullName, "Patron", signupSubType);
        //user.setLogin(username);
        //user.setPassword(password);
        //user.setPhoneNumber(phoneNumber);
        //user.setAddress(address);
        //System.out.println(user.toString());

        // LibraryStorage.getInstance().addUser(user);
    }
}