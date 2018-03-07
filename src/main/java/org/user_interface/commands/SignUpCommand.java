package org.user_interface.commands;


import org.resources.User;
import org.storage.SqlStorage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import java.util.ArrayList;


public class SignUpCommand extends Command{
    private String fullName, address, phoneNumber, username, password, signupSubType;

    @Override
    public String run(AbsSender sender, Update update, String info) {
        String message;
        switch (info) {
            case "signup_start":
                message = "Welcome to the signup section." +
                        " Please Enter your full name";
                sendMessage(sender, update, message);
                return "signup_address";
            case "signup_address":
                fullName = update.getMessage().getText();
                message = "Enter your address";
                sendMessage(sender, update, message);
                return "signup_phone";
            case "signup_phone":
                address = update.getMessage().getText();
                message = "Enter your phone number";
                sendMessage(sender, update, message);
                return "signup_username";

            case "signup_username":
                phoneNumber = update.getMessage().getText();
                message = "Enter your username/login";
                sendMessage(sender, update, message);
                return "signup_password";

            case "signup_password":
                username = update.getMessage().getText();
                message = "Enter your password";
                sendMessage(sender, update, message);
                return "signup_validator";

            case "signup_validator":
                password = update.getMessage().getText();
                keyboardUtils.setInlineKeyBoard(sender, update, "Are you a Student or Faculty Member?",
                        new ArrayList<String>() {{
                            add("Student");
                            add("Faculty");
                        }});

                return "signup_type";

                //Handle if student or faculty

            case "Student":
                signUpConfirm(sender, update, "Student");
                break;

            case "Faculty":
                signUpConfirm(sender, update, "Faculty");

            case "Confirm":
                createAccount();
                sendMessage(sender, update,
                        "Account created successfully! Use /login to login to your account");
                return "signup_done";

            case "Cancel":
                sendMessage(sender, update,
                        "Signup cancelled. Use /login, or /signup again if you want to create an account");
                return "start";
        }

        return null;
    }

    void signUpConfirm(AbsSender sender, Update update, String type) {
        signupSubType = type;
        String accountDetails = "Name: " + fullName +
                "\nEmail: " + address + "\nPhone Number: " + phoneNumber
                + "\nType:" + type;
        keyboardUtils.setInlineKeyBoard(sender, update, accountDetails, new ArrayList<String>() {{
            add("Confirm");
            add("Cancel");
        }});
    }

    void createAccount() {

        User user = new User(1001, fullName, "Patron", signupSubType);
        user.setLogin(username);
        user.setPassword(password);
        user.setPhoneNumber(phoneNumber);
        user.setAddress(address);
        System.out.println(user.toString());

        SqlStorage.getInstance().addUser(user);
    }
}