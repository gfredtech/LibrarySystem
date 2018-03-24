package org.user_interface.commands;

import org.resources.*;
import org.storage.SqlStorage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
                newDocumentName = update.getMessage().getText();
                message = "Enter the name of the authors " +
                        "separated by commas(,):";
                sendMessage(sender, update, message);
                return "add_documentauthors";

            case "add_documentauthors":
                newDocumentAuthors = Arrays.asList(input.split("\\s*,\\s*"));
                message = "Enter the name of the publisher";
                sendMessage(sender, update, message);
                return "add_documentpublisher";

            case "add_documentpublisher":
                newDocumentPublisher = input;
                message = "Enter the number of copies of the " +
                    "Document you want to add to the library";
                sendMessage(sender, update, message);
                return "add_copiesnum";

            case "add_copiesnum":
                newDocumentCopies = Integer.parseInt(input);
                sendMessage(sender, update, "What is the price of the document in Rubles?");
                return "add_price";

            case "add_price":
                newDocumentPrice = Integer.parseInt(input);
                sendMessage(sender, update, "Is it a bestSeller? (Y/N)");
                return "add_bestseller";


            case "add_bestseller":
                if(input.equalsIgnoreCase("Y")) {
                    newDocumentBestseller = true;
                    sendMessage(sender, update,"Enter the list of keywords for the book, separated by commas(,).");
                    return "add_keywords";
                } else if(input.equalsIgnoreCase("N")) {
                    sendMessage(sender, update, "Is it a reference? (Y/N)");
                    return "add_reference";
                }

            case "add_reference":
                if (input.equalsIgnoreCase("Y")) {
                    newDocumentReference = true;
                }
                sendMessage(sender, update,"Enter the list of keywords for the book, separated by commas(,).");
                return "add_keywords";

            case "add_keywords":
                newDocumentKeywords = Arrays.asList(input.split("\\s*,\\s*"));
                sendMessage(sender, update, "Enter the date of publication, like so: dd mm yyyy");
                return "add_publicationdate";

            case "add_publicationdate":
                String [] date = input.split("\\s");
                assert date.length == 3;
                int day = Integer.parseInt(date[0]);
                int month = Integer.parseInt(date[1]);
                int year = Integer.parseInt(date[2]);
                newDocumentPublicationDate = LocalDate.of(year, month, day);
                keyboardUtils.setInlineKeyBoard(sender, update, "What type of document is it?",
                        new ArrayList<String>() {{
                            add("Add Book");
                            add("Add Av Material");
                            add("Add Journal Article");
                            add("Add Journal Issue");
                        }});
                return "add_documenttype";

            case "Add Book":
                BookFactory bookFactory = new BookFactory();
                System.out.println("Authors: " + newDocumentAuthors.toString());
                System.out.println("Title:" + newDocumentName);
                System.out.println(newDocumentCopies);
                System.out.println(newDocumentPublisher);

                bookFactory.setAuthors(newDocumentAuthors);
                bookFactory.setTitle(newDocumentName);
                bookFactory.setCopiesNum(newDocumentCopies);
                bookFactory.setPublisher(newDocumentPublisher);
                bookFactory.setPrice(newDocumentPrice);
                if(newDocumentReference) {
                    bookFactory.setAsReference();
                }
                if(newDocumentBestseller) {
                    bookFactory.setAsBestseller();
                }
                bookFactory.setKeywords(newDocumentKeywords);
                bookFactory.setPublicationDate(newDocumentPublicationDate);
                //TODO: null pointer exception
                SqlStorage.getInstance().addBook(bookFactory);
                keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId),
                        newDocumentName + " added successfully!");
                return "menu";

            case "Add Av Material":
                AvMaterialFactory avMaterialFactory = new AvMaterialFactory();
                avMaterialFactory.setAsNonReference();
                avMaterialFactory.setTitle(newDocumentName);
                avMaterialFactory.setCopiesNum(newDocumentCopies);
                if(newDocumentReference) avMaterialFactory.setAsReference();
                avMaterialFactory.setKeywords(newDocumentKeywords);
                avMaterialFactory.setPrice(newDocumentPrice);
                SqlStorage.getInstance().addAvMaterial(avMaterialFactory);
                keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId),
                        newDocumentName + " added succesffully.");
                return "menu";
                }
        return null;
    }

    public void getCurrentUser(Update update, User user) {
        Long chatId = update.getMessage().getChatId();
        currentUser.put(chatId, user);
    }

    List<String> newDocumentAuthors;
    int newDocumentCopies, newDocumentPrice;
    String newDocumentName, newDocumentPublisher;
    Boolean newDocumentReference = false;
    Boolean newDocumentBestseller = false;
    List<String> newDocumentKeywords;
    LocalDate newDocumentPublicationDate;

}
