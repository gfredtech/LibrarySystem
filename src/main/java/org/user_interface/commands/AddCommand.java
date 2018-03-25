package org.user_interface.commands;

import org.items.User;
import org.storage.QueryParameters;
import org.storage.SqlStorage;
import org.storage.resources.Resource;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class AddCommand extends Command{
    @Override
    public String run(AbsSender sender, Update update, String info) {
        String input;
        if (update.hasMessage()) input = update.getMessage().getText();
        else input = update.getCallbackQuery().getMessage().getText();

        Long chatId;
        if (update.hasMessage()) chatId = update.getMessage().getChatId();
        else chatId = update.getCallbackQuery().getMessage().getChatId();
        switch (info) {
            case "Add Document":
                String message = "Enter the name of the Document " +
                        "you want to add";
                sendMessage(sender, update, message);
                return "add_documentname";

            case "add_documentname":
                newDocumentParams.add("title", update.getMessage().getText());
                message = "Enter the name of the authors " +
                        "separated by commas(,):";
                sendMessage(sender, update, message);
                return "add_documentauthors";

            case "add_documentauthors":
                newDocumentParams.add("authors", Arrays.asList(input.split("\\s*,\\s*")));
                message = "Enter the name of the publisher";
                sendMessage(sender, update, message);
                return "add_documentpublisher";

            case "add_documentpublisher":
                newDocumentParams.add("publisher", input);
                message = "Enter the number of copies of the " +
                    "Document you want to add to the library";
                sendMessage(sender, update, message);
                return "add_copiesnum";

            case "add_copiesnum":
                newDocumentParams.add("copy_num", Integer.parseInt(input));
                sendMessage(sender, update, "What is the price of the document in Rubles?");
                return "add_price";

            case "add_price":
                newDocumentParams.add("price", Integer.parseInt(input));
                sendMessage(sender, update, "Is it a bestSeller? (Y/N)");
                return "add_bestseller";


            case "add_bestseller":
                if(input.equalsIgnoreCase("Y")) {
                    newDocumentParams.add("is_bestseller", true);
                    sendMessage(sender, update,"Enter the list of keywords for the book, separated by commas(,).");
                    return "add_keywords";
                } else if(input.equalsIgnoreCase("N")) {
                    sendMessage(sender, update, "Is it a reference? (Y/N)");
                    return "add_reference";
                }

            case "add_reference":
                if (input.equalsIgnoreCase("Y")) {
                    newDocumentParams.add("is_reference", true);
                }
                sendMessage(sender, update,"Enter the list of keywords for the book, separated by commas(,).");
                return "add_keywords";

            case "add_keywords":
                newDocumentParams.add("keywords", Arrays.asList(input.split("\\s*,\\s*")));
                sendMessage(sender, update, "Enter the date of publication, like so: dd mm yyyy");
                return "add_publicationdate";

            case "add_publicationdate":
                String [] date = input.split("\\s");
                assert date.length == 3;
                int day = Integer.parseInt(date[0]);
                int month = Integer.parseInt(date[1]);
                int year = Integer.parseInt(date[2]);
                newDocumentParams.add("publication_date", LocalDate.of(year, month, day));
                keyboardUtils.setInlineKeyBoard(sender, update, "What type of document is it?",
                        new ArrayList<String>() {{
                            add("Add Book");
                            add("Add Av Material");
                            add("Add Journal Article");
                            add("Add Journal Issue");
                        }});
                return "add_documenttype";

            case "Add Book":
                SqlStorage.getInstance().add(Resource.Book, newDocumentParams);
                keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId), "Added successfully!");
                return "menu";

            case "Add Av Material":
                SqlStorage.getInstance().add(Resource.AvMaterial, newDocumentParams);
                keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId),
                        "Added succesffully.");
                return "menu";
                }
        return null;
    }

    public void setCurrentUser(Long chatId, User user) {
        currentUser.put(chatId, user);
    }

    QueryParameters newDocumentParams = new QueryParameters();

    HashMap<Long, User> currentUser = new HashMap<>();
}
