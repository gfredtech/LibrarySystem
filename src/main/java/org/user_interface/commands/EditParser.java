package org.user_interface.commands;

import org.items.Book;
import org.storage.QueryParameters;
import org.storage.SqlStorage;
import org.storage.resources.BookEntry;
import org.storage.resources.Resource;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import java.util.ArrayList;
import java.util.Arrays;

import static org.user_interface.commands.EditCommand.editParams;
import static org.user_interface.commands.EditCommand.editParser;

public class EditParser extends Command {

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

    protected void addEditParameters(String a) {
        editParams.put(a.substring(0, a.indexOf(" ")).trim(), a.substring(a.indexOf(" ")).trim());
    }

    protected int applyEditParamsBook(Long chatId) {
        System.out.println(editParams.toString());
        if(editParams.containsKey("title")) SqlStorage.getInstance().updateAll(
                Resource.Book, new QueryParameters().add("book_id",
                        documentCursor.get(chatId).getId()),
                new QueryParameters().add("title", editParams.get("title")));

        if(editParams.containsKey("price")) SqlStorage.getInstance().updateAll(
                Resource.Book, new QueryParameters().add("book_id",
                        documentCursor.get(chatId).getId()),
                new QueryParameters().add("price", editParams.get("price")));

        if(editParams.containsKey("publisher")) SqlStorage.getInstance().updateAll(
                Resource.Book, new QueryParameters().add("book_id",
                        documentCursor.get(chatId).getId()),
                new QueryParameters().add("publisher", editParams.get("publisher")));

        if(editParams.containsKey("authors")) SqlStorage.getInstance().updateAll(
                Resource.Book, new QueryParameters().add("book_id",
                        documentCursor.get(chatId).getId()),
                new QueryParameters().add("authors",
                        parseGroupItems(editParams.get("authors"))));

       return documentCursor.get(chatId).getId();
    }

    void parseBookParameters(AbsSender sender, Update update, Long chatId, String input) {
        Book book = (Book) documentCursor.get(chatId).getItem();
        if (input.equals("delete")) {
//            SqlStorage.getInstance().removeBook(currentBook.getId());
            keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId).getUser(),
                    book.getTitle() + " removed successfully");
            return;
        }

        String tokens[] = input.split("[,]+");
        for (int i = 0; i < tokens.length; i++) tokens[i] = tokens[i].trim();

        editParser.findGroup(tokens, "authors");
        editParser.findGroup(tokens, "keywords");

        for(String i: tokens) {
            if(!i.equals(" ")) editParser.addEditParameters(i.trim());
        }

        int id = applyEditParamsBook(chatId);
        BookEntry bookEntry = SqlStorage.getInstance().find(Resource.Book,
                new QueryParameters().add("book_id", id)).get(0);
        documentCursor.put(chatId, bookEntry);

        book = (Book) documentCursor.get(chatId).getItem();
        sendMessage(sender, update, book.toString());
        keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId).getUser(),
                "Book updated successfully");

    }

    void parseAvMaterialParameters(String input) {
        //TODO:
    }

    void parseJournalIssueParameters(String input) {
        //TODO:
    }

    void parseJournalArticleParameters(String input) {
        //TODO:
    }

    @Override
    public String run(AbsSender sender, Update update, String info) {
        return null;
    }
}
