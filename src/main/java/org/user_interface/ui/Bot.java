package org.user_interface.ui;

import org.DummyData;
import org.resources.*;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.user_interface.commands.Command;
import org.user_interface.commands.LoginCommand;
import org.user_interface.commands.SignUpCommand;
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
            switch (x) {
                case "/start":
                    command = new StartCommand(message.getFrom(), message.getChat());

                    SendMessage reply = command.run();
                    reply.setReplyMarkup(new ReplyKeyboardRemove()); // hides keyboard in case it's showing already
                    try {
                        execute(reply);
                        previous.put(chatId, "/start");

                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }

                    break;

                case "/login":
                    command = new LoginCommand(message.getFrom(), message.getChat());

                    SendMessage login = command.run();
                    login.setReplyMarkup(new ReplyKeyboardRemove()); // hides keyboard in case it's showing already

                    try {

                        execute(login);
                        previous.put(chatId, "/username");

                    } catch (TelegramApiException ex) {
                        ex.printStackTrace();
                    }
                    break;

                case "/signup":
                    command = new SignUpCommand(message.getFrom(), message.getChat());

                    SendMessage signup = command.run();
                    signup.setReplyMarkup(new ReplyKeyboardRemove()); // hides keyboard in case it's showing already
                    try {

                        execute(signup);
                        previous.put(chatId, "/signup_name");
                    } catch (TelegramApiException ex) {
                        ex.printStackTrace();
                    }
                    break;

                case "Checkout Document":
                    if (previous.get(chatId) != null && previous.get(chatId).equals("/menu")) {

                        setInlineKeyBoard(chatId,
                                "Select the kind of document you want to checkout:", new
                                        ArrayList<String>() {{
                                            add("Book");
                                            add("AV Material");
                                            add("Journal Article");
                                            add("Journal Issue");
                                        }});
                        previous.put(chatId, "/checkout");
                    } else {
                        sendMessage(chatId, "Please /login or /signup first before you can execute this command.");
                        previous.put(chatId, "/fail");
                    }

                    break;

                case "Return Document":
                    sendMessage(chatId, "Select the type of document you want to return");
                    setInlineKeyBoard(chatId, "Types:",
                            new ArrayList<String>() {{
                                add("Return Book");
                                add("Return Av Material");
                                add("Return Journal Issue");

                            }});
                    previous.put(chatId, "/return_document_list");
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
                    sendMessage(chatId, "Select the type of document you want to edit");
                    setInlineKeyBoard(chatId, "Types:",
                            new ArrayList<String>() {{
                                add("Edit Book");
                                add("Edit Av Material");
                                add("Edit Journal Issue");
                                add("Edit Journal Article");
                            }});
                    previous.put(chatId, "/modify_document");
                    break;




                default:
                    String input = previous.get(chatId);
                    if (input != null) {
                        switch (input) {
                            case "/signup_name":

                                signUpName = message.getText();
                                sendMessage(chatId, "Enter your e-mail address");

                                previous.put(chatId, "/signup_email");
                                break;

                            case "/signup_email":
                                signUpEmail = message.getText();
                                sendMessage(chatId, "Enter your phone number");
                                previous.put(chatId, "/signup_passcode");
                                break;

                            case "/signup_passcode":
                                signUpPhone = message.getText();
                                sendMessage(chatId, "Set a login code for your account");
                                previous.put(chatId, "/signup_type");
                                break;

                            case "/signup_type":
                                signUpPassword = message.getText();
                                setInlineKeyBoard(chatId, "Are you a Librarian, student or Faculty member?",
                                        new ArrayList<String>() {{
                                            add("Student");
                                            add("Faculty");
                                        }});
                                previous.put(chatId, "signup_confirm");


                            case "/signup_confirm":

                                System.out.println(signUpName + " " + signUpEmail + " " + signUpPhone + " " + signUpPassword);
                                String accountDetails = "Name: " + signUpName +
                                        "\n\nEmail: " + signUpEmail + "\n\nPhone Number: " + signUpPhone;
                                setInlineKeyBoard(chatId, accountDetails, new ArrayList<String>() {{
                                    add("Confirm");
                                    add("Cancel");
                                }});

                                break;


                            case "/username":
                                username = message.getText();
                                sendMessage(chatId, "Enter password");
                                previous.put(chatId, "/password");
                                break;

                            case "/password":
                                password = message.getText();
                                if (username.equals("admin") && password.equals("root")) {
                                    //TODO: get if user is librarian, then set isLibrarian parameter to correct value.
                                    showMainMenuKeyboard(chatId, true, "Success");
                                    previous.put(chatId, "/menu");

                                } else {
                                    sendMessage(chatId, "Unsuccessful. Please try again.");
                                    previous.put(chatId, "/start");

                                }

                                break;

                            case "/select_book":
                                Book bookSelected = null;
                                String msg = message.getText();
                                int position;
                                try {
                                    position = Integer.parseInt(msg);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                    sendMessage(chatId, "Input is not a number");
                                    return;
                                }

                                bookSelected = dummyBookList.get(position - 1);

                                if (bookSelected != null) {
                                    showBookDetails(chatId, bookSelected);
                                    bookCursor.put(chatId, bookSelected);
                                }
                                break;

                            case "/select_avmaterial":
                                AvMaterial selected = null;
                                String msg1 = x;
                                int position1;
                                try {
                                    position1 = Integer.parseInt(msg1);
                                }catch (NumberFormatException e) {
                                    e.printStackTrace();
                                    sendMessage(chatId, "Input is not a number.");
                                    return;
                                }
                                selected = dummyAvMaterialList.get(position1 - 1);

                                if(selected != null) {
                                    showAvMaterialDetails(chatId, selected);

                                    avMaterialCursor.put(chatId, selected);
                                }

                                break;

                            case "select_journalarticle":
                                //TODO: to be done
                                break;



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

                            case "/return_book_index_number":
                                Book returnSelected = null;

                                try {
                                    position = Integer.parseInt(x);
                                }catch (NumberFormatException e) {
                                    sendMessage(chatId, "Not a number");
                                    e.printStackTrace();
                                    return;
                                }

                                //TODO: get book position and point book cursor to book object
                                // returnSelected = bookAtIndexEntered();
                                //returnBook(returnSelected);
                                showMainMenuKeyboard(chatId, false, returnSelected.getTitle() + " returned successfully");
                                pre




                                }


                        }

                        break;
            }

        } else if(update.hasCallbackQuery()) {
            String call_data = update.getCallbackQuery().getData();
            Long chatId =  update.getCallbackQuery().getMessage().getChatId();

            switch (call_data) {
                case "Confirm":
                    createAccount(signUpName, signUpEmail, signUpPhone);
                    previous.put(chatId, "/Confirm");
                    sendMessage(chatId,
                            "Account created successfully! Use /login to login to your account");
                    break;


                case "Cancel":
                    if (previous != null && previous.get(chatId).equals("/Confirm")) {
                        sendMessage(chatId, "Already signed up");
                    } else {
                        sendMessage(chatId,
                                "Signup cancelled. Use /login, or /signup again if you want to create an account");
                    }
                    break;

                case "Student":
                    signUpType = "Patron";
                    signUpSubType = "Student";
                    break;


                case "FacultyMember":
                    signUpType = "Patron";
                    signUpSubType = "Faculty";
                    break;

                case "Book":
                    sendMessage(chatId, "Here's a " +
                            "list of all Books in the library. Enter the number of the book you want:");
                    listBooks(chatId);
                    previous.put(chatId, "/select_book");
                    break;

                case "AV Material":
                    sendMessage(chatId, "Here's a list of all AV Materials in the library. Enter" +
                            "the number of the AV material you want");
                    listAvMaterials(chatId);
                    previous.put(chatId, "/select_avmaterial");
                    break;

                case "Journal Article":
                    //TODO: idee fixe
                    StringBuilder builder2 = new StringBuilder();
                    // dummyJournalArticleList = new DummyData().

                    break;

                case "Journal Issue":
                    //TODO: idee fixe
                    break;


                case "Checkout Book":
                    //TODO: checkout book

                    showMainMenuKeyboard(chatId, true,
                            bookCursor.get(chatId).getTitle() + " Checked out successfully.");
                    break;

                case "Checkout Av Material":
                    showMainMenuKeyboard(chatId, true, avMaterialCursor.get(chatId).getTitle() + " checked out successfully!");
                    break;

                case "Cancel Checkout":
                    showMainMenuKeyboard(chatId, false,
                            "Operation Cancelled");
                    break;

                //TODO: Add document to its appropriate category
                case "Add Book":
                    if (previous.get(chatId).equals("/add_document_complete")) {
                        showMainMenuKeyboard(chatId, true, "Book added successfully!");
                        previous.put(chatId, "/menu");
                    }
                    break;

                case "Add Av Material":
                    if(previous.get(chatId).equals("/add_document_complete")) {
                        showMainMenuKeyboard(chatId, true, "AV Material added successfully!");
                        previous.put(chatId, "/menu");
                    }
                    break;

                case "Add Journal Article":
                    if(previous.get(chatId).equals("/add_document_complete")) {
                        showMainMenuKeyboard(chatId, true, "AV Material added successfully!");
                        previous.put(chatId, "/menu");
                    }
                    break;

                case "Add Journal Issue":
                    if(previous.get(chatId).equals("/add_document_complete")) {
                        showMainMenuKeyboard(chatId, true, "Journal Issue Added Successfully");
                        previous.put(chatId, "/menu");
                    }
                    break;

                case "Edit Book":
                    if(previous.get(chatId).equals("/modify_document")) {
                        sendMessage(chatId, "Select the Book that you want to edit:");
                        listBooks(chatId);
                        previous.put(chatId, "/edit_book");
                    }
                    break;

                case "Edit Av Material":
                    if(previous.get(chatId).equals("/modify_document")) {
                        sendMessage(chatId, "Select the Av Material you want to edit");
                        listAvMaterials(chatId);
                        previous.put(chatId, "/edit_avmaterial");
                    }
                    break;

                case "Return Book":
                    sendMessage(chatId, "This is the list of current books checked out by you");
                    /** TODO: return list of Books checked out by user
                     * But first check e.g.
                     * if the person has some books currently checked out, send as message
                     * else
                     * showMainMenuKeyboard(chatId, false, "You currently have no Books Checked out");
                     * previous.put(chatId, "/menu");
                     * else {
                    **/

                    previous.put(chatId, "/return_book_index_number");



                    break;

                case "Return Av Material":
                    sendMessage(chatId, "This is the list of current Av Materials checked out by you");
                    /** TODO: return list of Books checked out by user
                     * But first check e.g.
                     * if the person has some  Av Materials currently checked out, send as message
                     * else
                     * showMainMenuKeyboard(chatId, false, "You currently have no AV Materials Checked out");
                     * previous.put(chatId, "/menu");
                     * else {
                     **/

                    previous.put(chatId, "/return_avmaterial_index_number");

                    break;

                case "Return Journal Issue":
                    sendMessage(chatId, "This is the list of current Journal Issues checked out by you");
                    /** TODO: return list of Books checked out by user
                     * But first check e.g.
                     * if the person has some Journal Issues currently checked out, send as message
                     * else
                     * showMainMenuKeyboard(chatId, false, "You currently have no Journal issues Checked out");
                     * previous.put(chatId, "/menu");
                     * else {
                     **/

                    previous.put(chatId, "/return_journal_index_number");
                    break;

                    }
        }
    }

    @Override
    public String getBotUsername() {
        return "getfreecourses_bot";
        //return "konyvtar_bot";
    }

    @Override
    public String getBotToken() {
        return "453567691:AAG-05UGHvE4f_CDS1EFq2U0wj0w7x4Ho-o";
       // return "404457992:AAE0dHw07sHw8woSFiMJSebrQCK2aUyN8CM";
    }

    void showMainMenuKeyboard(Long chatId, boolean isLibrarian, String msg) {
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

        if(isLibrarian) {
            row = new KeyboardRow();
            row.add("Edit");
            keyboard.add(row);
        }

        /**
        row = new KeyboardRow();
        row.add("️Settings");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("Logout");
        keyboard.add(row);
         **/

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

    void createAccount(String signUpName, String signUpEmail, String signUpPhone) {

        User user = new User(1001, signUpName, signUpType, signUpSubType);
        user.setLogin(signUpPassword);
        user.setPhoneNumber(signUpPhone);
        user.setAddress(signUpEmail);


        dummyUserList.add(user);

        //TODO: add account to database
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
        String bookDetails = book.toString() +
                 "\nCopies: " + book.getCopiesNum();
        setInlineKeyBoard(chatId, bookDetails, new ArrayList<String>(){{
            add("Checkout Book");
            add("Cancel Checkout");
        }});
        previous.put(chatId, "/book_checkout");
    }

    void showAvMaterialDetails(Long chatId, AvMaterial selected) {
        String details = selected.toString() +
                "\nCopies: " + selected.getCopiesNum();
        setInlineKeyBoard(chatId, details, new ArrayList<String>() {{
            add("Checkout Av Material");
            add("Cancel Checkout");

        }});
        previous.put(chatId, "/avmaterial_checkout");

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
        dummyBookList = new DummyData().createBooks();
        for (int i = 0; i < dummyBookList.size(); i++) {
            String name = dummyBookList.get(i).getTitle();
            System.out.println(name);
            builder.append((i + 1) + ". " + name + "\n\n");
        }

        sendMessage(chatId, builder.toString());


    }
    void listAvMaterials(Long chatId) {
        dummyAvMaterialList = new DummyData().createAVMaterial();
        System.out.println(dummyAvMaterialList == null);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < dummyAvMaterialList.size(); i++) {
            String name = dummyAvMaterialList.get(i).getTitle();
            builder.append((i + 1) + ". " + name + "\n\n");
        }

        sendMessage(chatId, builder.toString());

    }


    HashMap<Long, String> previous = new HashMap<>();

    // tracks the current book that's about to be checked out by a user
    HashMap<Long, Book>  bookCursor = new HashMap<>();
    HashMap<Long, AvMaterial> avMaterialCursor = new HashMap<>();

    //TODO: will be removed. for testing purposes
    ArrayList<Book> dummyBookList;
    ArrayList<User> dummyUserList  = new DummyData().createUsers();
    ArrayList<AvMaterial> dummyAvMaterialList = new DummyData().createAVMaterial();
    ArrayList<JournalArticle> dummyJournalArticleList;
    ArrayList<JournalIssue> dummyJournalIssue;


    BookFactory factory = new BookFactory();

    String username, password;

    String signUpName, signUpEmail, signUpPhone, signUpPassword, signUpType, signUpSubType;
}

