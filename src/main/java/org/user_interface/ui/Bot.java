package org.user_interface.ui;


import javafx.util.Pair;
import org.controller.BookingController;
import org.controller.ReturnController;
import org.resources.*;
import org.storage.QueryParameters;
import org.storage.SqlStorage;
import org.user_interface.commands.*;
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

    LoginCommand loginCommand = new LoginCommand();
    StartCommand startCommand = new StartCommand();
    SignUpCommand signUpCommand = new SignUpCommand();
    CheckoutCommand checkoutCommand = new CheckoutCommand();
    ReturnCommand returnCommand = new ReturnCommand();



    // entry point of bot
    @Override
    public void onUpdateReceived(Update update) {

        Message message = update.getMessage();
        String currentState;



        if (update.hasMessage() && message.hasText()) {

            Long chatId = message.getChatId();
            String x = message.getText();
            switch (x) {
                case "/start":
                    startCommand.run(this, update, null);
                    break;

                case "/login":
                    currentState = loginCommand.run(this, update, "login_username");
                    previous.put(chatId, currentState);

                    break;

                case "/signup":
                    currentState = signUpCommand.run(this, update, "signup_start");
                    previous.put(chatId, currentState);

                    break;

                case "Checkout Document":
                    // first,  pass currently logged in user to CheckoutCommand module
                    currentUser.put(chatId, loginCommand.returnNewlyLoggedInUser(update));
                    System.out.println("ayo");
                    checkoutCommand.getCurrentUser(update, currentUser.get(chatId));
                    currentState = checkoutCommand.run(this, update, "checkout_start");
                    previous.put(chatId, currentState);

                    break;

                case "Return Document":
                    // first, pass currently logged in user to CheckoutCommand module
                    currentUser.put(chatId, loginCommand.returnNewlyLoggedInUser(update));
                    System.out.println(currentUser.get(chatId).getName());
                    returnCommand.getCurrentUser(update, currentUser.get(chatId));
                    currentState = returnCommand.run(this, update, "return_start");
                    previous.put(chatId, currentState);

                    break;

  /**
                case "Edit":
                    showCRUDkeyboard(chatId);
                    previous.put(chatId, "/edit");

                    break;

                case "Add Document":
                    sendMessage(chatId, "Enter the title of document to be added");
                    previous.put(chatId, "/add_document_title");
                    break;
   **/

              /**  case "Modify Document":
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

               **/


                default:
                    String input = previous.get(chatId);

                    if (input != null) {
                        input = input.substring(0, input.indexOf("_"));
                        switch (input) {
                            case "login":
                                currentState = loginCommand.run(this, update, previous.get(chatId));
                                previous.put(chatId, currentState);
                                break;


                            case "signup":

                                currentState = signUpCommand.run(this, update, previous.get(chatId));
                                previous.put(chatId, currentState);

                                break;

                            case "checkout":
                                currentState = checkoutCommand.run(this, update, previous.get(chatId));
                                System.out.println("checkout is at" + currentState);
                                previous.put(chatId, currentState);
                                break;

                            case "return":
                                currentState = returnCommand.run(this, update, previous.get(chatId));
                                previous.put(chatId, currentState);
                                break;


              /**              case "select_journalarticle":
                                //TODO: to be done
                                break;


                            case "/add_document_title":
                                factory.setTitle(x);
                                sendMessage(chatId, "Enter the name of the authors, separate by comma(,)");
                                previous.put(chatId, "/add_document_authors");
                                break;

                            case "/add_document_authors":
                                String[] authors = x.split(",");
                                factory.setAuthors(Arrays.asList(authors));
                                sendMessage(chatId, "Enter the name of the publisher");
                                previous.put(chatId, "/add_document_publisher");
                                break;

                            case "/add_document_publisher":
                                factory.setPublisher(x);
                                sendMessage(chatId, "Enter the number of copies you want to add to the library");
                                previous.put(chatId, "/add_document_copies");
                                break; **/

                         /**   case "/add_document_copies":
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
                          **/


                        }


                    }
            }
        }else if(update.hasCallbackQuery()) {
            String call_data = update.getCallbackQuery().getData();
            Long chatId =  update.getCallbackQuery().getMessage().getChatId();

            String previousData = previous.get(chatId);
            previousData = previousData.substring(0, previousData.indexOf("_"));

            switch (previousData) {
                case "signup":
                    signUpCommand.run(this, update, call_data);
                    previous.put(chatId, "signup_confirm");
                    break;

                case "checkout":
                    currentState = checkoutCommand.run(this, update, call_data);
                    previous.put(chatId, currentState);
                    break;

                case "return":
                    currentState = returnCommand.run(this, update, call_data);
                    System.out.println("dafgs");
                    previous.put(chatId, currentState);
                    break;


            }

            ////////////////////////////////////////////////////////////////////////////////////////////////////
/**
            switch (call_data) {







                case "FacultyMember":
                    signUpType = "Patron";
                    signUpSubType = "Faculty";
                    previous.put(chatId, "signup_confirm");
                    break;

                case "Book":


                case "Journal Article":
                    //TODO: idee fixe
                    StringBuilder builder2 = new StringBuilder();

                    break;

                case "Journal Issue":
                    //TODO: idee fixe
                    break;


                //TODO: Add document to its appropriate category
                case "Add Book":
                    if (previous.get(chatId).equals("/add_document_complete")) {
                        SqlStorage.getInstance().addBook(factory);
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
                        showMainMenuKeyboard(chatId, "Journal Article added successfully!");
                        previous.put(chatId, "/menu");
                    }
                    break;

                case "Add Journal Issue":
                    if(previous.get(chatId).equals("/add_document_complete")) {
                        showMainMenuKeyboard(chatId, "Journal Issue Added Successfully");
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

                    break;

                case "Return Av Material":
                    List<CheckoutRecord> checkedOutAvMaterials =
                            SqlStorage.getInstance().getCheckoutRecordsFor(currentUser.get(chatId).getCardNumber());
                    StringBuilder avmaterials = new StringBuilder();
                    i = 1;
                    for (CheckoutRecord c: checkedOutAvMaterials) {
                        if (c.item.getType().equals("avmaterial")) {
                            AvMaterial a = SqlStorage.getInstance().getAvMaterial(c.item.getId()).get();
                            avmaterials.append(i);
                            avmaterials.append(". ");
                            avmaterials.append(a.getTitle());
                            avmaterials.append(" by ");
                            avmaterials.append(a.getAuthors());
                            avmaterials.append("\n");
                            i += 1;
                        }
                    }

                    if(avmaterials.length() > 0) {
                        sendMessage(chatId, "This is the list of current AV materials checked out by you:\n"
                        + avmaterials.toString());
                        previous.put(chatId, "/return_avmaterial_index_number");
                    } else {
                        showMainMenuKeyboard(chatId, "You have no AV materials checked out.");
                        previous.put(chatId, "/menu");
                    }


                    break;

                case "Return Journal Issue":
                   List<CheckoutRecord> checkedOutJournals =
                           SqlStorage.getInstance().getCheckoutRecordsFor(currentUser.get(chatId).getCardNumber());
                   StringBuilder journals = new StringBuilder();
                   i = 1;
                   for(CheckoutRecord c: checkedOutJournals) {
                       if(c.item.getType().equals("journal")) {
                           JournalIssue j = SqlStorage.getInstance().getJournal(c.item.getId()).get();
                           journals.append(i);
                           journals.append(". ");
                           journals.append(j.getTitle());
                           journals.append(" by ");
                           journals.append(j.getPublisher());
                           journals.append("\n");
                           i+= 1;
                       }
                   }
                   if (journals.length() > 0) {
                       sendMessage(chatId, "This is the list of current journals checked out by you:\n"
                       + journals.toString());
                       previous.put(chatId, "/return_journal_index_number");
                       } else {
                       showMainMenuKeyboard(chatId, "You currently have no AV materials checked out");
                       previous.put(chatId, "/menu");
                   }


                    break;

                    }  **/
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



    void sendMessage(Long chatId, String message) {
        try {
            execute(new SendMessage().
                    setChatId(chatId).setText(message).setReplyMarkup(new ReplyKeyboardRemove()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }






  /**  void showAvMaterialDetails(Long chatId, AvMaterial selected) {
        String details = selected.toString() +
                "\nCopies: " + selected.getCopiesNum();
        setInlineKeyBoard(chatId, details, new ArrayList<String>() {{
            add("Checkout Av Material");
            add("Cancel Checkout");

        }});
        previous.put(chatId, "/avmaterial_checkout");

    }
   **/


    HashMap<Long, String> previous = new HashMap<>();

    // A pair where the key is the info returned from the method, and
    // the value is the next state
    private Pair<String, String> messenger;

    // tracks the current book that's about to be checked out by a user
    Map<Long, Book>  bookCursor = new HashMap<>();
    Map<Long, AvMaterial> avMaterialCursor = new HashMap<>();
    Map<Long, User> currentUser = new HashMap<>();

    BookFactory factory = new BookFactory();
    AvMaterialFactory avMaterialFactory = new AvMaterialFactory();

    private String signUpName, signUpEmail, signUpPhone, signUpPassword, signUpType, signUpSubType;
}

