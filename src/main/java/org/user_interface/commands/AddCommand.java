package org.user_interface.commands;

import org.controller.AddItemCommand;
import org.items.Item;
import org.storage.LibraryStorage;
import org.user_interface.ui.Interface;

import java.util.ArrayList;
import java.util.HashMap;

public class AddCommand extends Command{
    @Override
    public String run(String info) {

        Long chatId;
        if (update.hasMessage()) chatId = update.getMessage().getChatId();
        else chatId = update.getCallbackQuery().getMessage().getChatId();
        switch (info) {
            case "startnext":
                System.out.println(chatId);
                String auth = authorizationChecker(currentUser.get(chatId));
                if(auth!= null) return auth;
                keyboardUtils.showCRUDkeyboard("Add");
                return "add_main";

            case "main":
                String message = update.getMessage().getText();
                if (message != null && message.equals("Add Document")) {
                    keyboardUtils.showDocumentKeyboard();
                    return "add_documenttype";
                } else if (message != null && message.equals("Add User")) {
                    System.out.println("user add executed");
                    return new Interface().handleMessageUpdate(sender, update, "signup_startnext");
                }

            case "documenttype":
                showInstructions(chatId);
                return "add_params";

            case "params":
                return parseParameters(chatId);

            case "confirm":
                message = update.getCallbackQuery().getData();
                if(message.equals("Confirm")) {

                    org.controller.Command.Result res =
                            addEntryMap.get(chatId).execute(LibraryStorage.getInstance());

                    switch (res) {
                        case Success:
                            keyboardUtils.showMainMenuKeyboard(
                                    currentUser.get(chatId).getUser(),
                                    "Item has been added successfully.");
                            break;

                        case Failure:
                            keyboardUtils.showMainMenuKeyboard(
                                    currentUser.get(chatId).getUser(),
                                    res.getInfo());
                            break;
                    }
                } else if(message.equals("Cancel")) {
                    keyboardUtils.showMainMenuKeyboard(
                            currentUser.get(chatId).getUser(),
                            "Operation Cancelled");
                }
                return "menu_main";
        }
        return null;
    }

    private String parseParameters(Long chatId) {
        Item item = null;
        switch (type.get(chatId)) {
            case "Book":
               item =  addParser.parseBookParameters();
                break;
            case "AV Material":
               item =  addParser.parseAvMaterialParameters();
                break;
            case "Journal Issue":
               item = addParser.parseJournalIssueParameters();
                break;
        }

        if(item == null) {
            keyboardUtils.showMainMenuKeyboard(currentUser.get(chatId).getUser()
            ,"There was a problem parsing your input. Please try again");
            return "menu_main";
        }

        keyboardUtils.setInlineKeyBoard(
                item.toString(), new ArrayList<String>() {{
                    add("Confirm");
                    add("Cancel");
                }});

        addEntryMap.put(chatId, new AddItemCommand(item, currentUser.get(chatId).getUser()));
        return "add_confirm";
    }


    private void showInstructions(Long chatId) {

        String message = update.getMessage().getText();
        String builder = ("To add a document, type the following information in the " +
                "specified order as shown, separated by semicolons:\n\n") +
                showAddFormat(message) +
                "\n\nExample below:\n" +
                showAddExample(message);

        sendMessage(builder);
        type.put(chatId, message);
    }

    private String showAddFormat(String type) {
        if(type.equals("Book")) return "`title; copies; isReference{true | false}; " +
                "[keyword1,...keywordN]; price; [author1,...authorN]" +
                "; publication date(DDMMYYYY); " +
                "publisher; isBestseller{true | false}`";

        if(type.equals("AV Material")) return "`title; copies,isReference{true | false}; " +
                "[keyword1,...keywordN]; [author1,...authorN]`";

       if(type.equals("Journal Issue")) return "`title; copies; isReference{true | false}; " +
                "[keyword1, keyword2...keywordN]; price; [editor1,...editorN]" +
                "; publication date(DDMMYYYY); publisher`";
       return null;
    }

    private String showAddExample(String type) {
        if(type.equals("Book")) return "_Hackers and Painters; 5; " +
                "false; [hacker, lisp, painter, startup]; 2500; [Paul, Graham]; 09092005; " +
                "Harper Collins; true_";
        if(type.equals("AV Material"))
            return "_Calculus; 5; " +
                    "false; [math, calculus, differential equations]; 2500; [Paul, Graham]_";
        if (type.equals("Journal Issue")) return "_The Maker Magazine; 5; " +
                "true; [arduino, raspberry pi, hardware]; 2500; [Paul, Graham]; 09092005;" +
                "Harper Collins_";
        return null;
        }
    
    private static HashMap<Long, String> type = new HashMap<>();
   private static AddParser addParser = new AddParser();
   private static HashMap<Long, AddItemCommand> addEntryMap = new HashMap<>();

}
