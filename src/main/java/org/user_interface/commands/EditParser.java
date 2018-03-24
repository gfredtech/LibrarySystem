package org.user_interface.commands;

import org.resources.Book;
import org.resources.BookFactory;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import java.util.ArrayList;
import java.util.Arrays;
import static org.user_interface.commands.EditCommand.editParams;
import static org.user_interface.commands.EditCommand.editParser;

public class EditParser extends Command{

    void findGroup(String[] a, String group) {
        int index = -1;
        String authorsStart = null;
        for(int i = 0; i < a.length; i++) {
            if (a[i].startsWith(group)) {
                authorsStart = a[i].replace(group, "");
                authorsStart = authorsStart.replace("[", "");
                a[i] = " ";
                index = i;
                break;
            }
        }
        if(index == -1) return;
        for(int i = index + 1; i < a.length; i++) {
            authorsStart += " " + a[i];
            if(a[i].endsWith("]")) {
                a[i] = " ";
                authorsStart = authorsStart.replace("]", "");
                break;
            }
            a[i] = " ";
        }
        editParams.put(group, authorsStart.trim());
    }

    protected ArrayList<String> parseGroupItems(String a) {
        if(a == null) return null;
        String array[] = a.split("\\s");
        return new ArrayList<>(Arrays.asList(array));
    }


    protected void addEditParameter(String a) {
        editParams.put(a.substring(0, a.indexOf(" ")).trim(), a.substring(a.indexOf(" ")).trim());
    }

    protected Book applyEditParamsBook(Long chatId) {
        BookFactory factory = new BookFactory();
        System.out.println(editParams.toString());
        Book book = (Book) documentCursor.get(chatId);
        factory.setTitle(editParams.getOrDefault("title", book.getTitle()));
        factory.setPrice(Integer.parseInt(editParams.getOrDefault("price", String.valueOf(book.getPrice()))));
        factory.setPublisher(editParams.getOrDefault("publisher", book.getPublisher()));
        if(editParams.containsKey("isbestseller") && editParams.get("isbestseller").equals("true")) factory.setAsBestseller();
        else factory.setAsNonBestseller();

        if(editParams.containsKey("authors")) factory.setAuthors(editParser.parseGroupItems(editParams.get("authors")));
        else factory.setAuthors(book.getAuthors());

        return factory.build(book.getId());
        }

    void parseBookParameters(AbsSender sender, Update update, Long chatId, String input) {
        Book book = (Book) documentCursor.get(chatId);
        if (input.equals("delete")) {
//            SqlStorage.getInstance().removeBook(currentBook.getId());
            keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId),
                    book.getTitle() + " removed successfully");
            return;
        }

        String tokens[] = input.split("[,]+");
        for (int i = 0; i < tokens.length; i++) tokens[i] = tokens[i].trim();

        editParser.findGroup(tokens, "authors");
        editParser.findGroup(tokens, "keywords");

        for(String i: tokens) {
            if(!i.equals(" ")) editParser.addEditParameter(i.trim());
        }

        documentCursor.put(chatId, editParser.applyEditParamsBook(chatId));
        book = (Book) documentCursor.get(chatId);
        //TODO: update book here
        sendMessage(sender, update, book.toString());

    }

    void parseAvMaterialParameters(String input) {

    }

    void parseJournalIssueParameters(String input) {

    }

    void parseJournalArticleParameters(String input) {

    }

    @Override
    public String run(AbsSender sender, Update update, String info) {
        return null;
    }
}
