package org.user_interface.commands;


import org.storage.QueryParameters;
import org.storage.SqlStorage;
import org.storage.resources.ItemEntry;
import org.storage.resources.Resource;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import java.util.ArrayList;
import java.util.Arrays;

import static org.user_interface.commands.EditCommand.editParams;
import static org.user_interface.commands.EditCommand.editParser;

public class EditParser extends Command {

    private ArrayList<String> parseGroupItems(String a) {
        if(a == null) return null;
        a = a.replaceAll("[\\[\\]]+", "");
        String array[] = a.split("[ ,]+");
        for(String i: array) System.out.println("this " + i);
        return new ArrayList<>(Arrays.asList(array));
    }

    private void addEditParameters(String a) {
        editParams.put(a.substring(0, a.indexOf(" ")).trim(), a.substring(a.indexOf(" ")).trim());
    }

    void coreParser(AbsSender sender, Update update, Resource type, Long chatId, String input) {
        String tokens[] = input.split("[;]+");

        for(String i: tokens) {
            if(!i.equals(" ")) editParser.addEditParameters(i.trim());
        }

        //update core parameters
        if(editParams.containsKey("title")) {
            SqlStorage.getInstance().updateAll(
                    type, new QueryParameters().add("book_id",
                            documentCursor.get(chatId).getId()),
                    new QueryParameters().add("title", editParams.get("title")));
            editParams.remove("title");
        }

        if(editParams.containsKey("copies")) {
            SqlStorage.getInstance().updateAll(type,
                    new QueryParameters().add(type.getTableKey(), documentCursor.get(chatId).getId())
                    ,new QueryParameters().add("copy_num", editParams.get("copies")));
            editParams.remove("copies");
        }

        if(editParams.containsKey("reference")) {
            SqlStorage.getInstance().updateAll(type,
                    new QueryParameters().add(type.getTableKey(), documentCursor.get(chatId).getId())
                    , new QueryParameters().add("is_reference", Boolean.valueOf(editParams.get("reference"))));
            editParams.remove("copies");
        }

        if(editParams.containsKey("keywords")) {
            SqlStorage.getInstance().updateAll(
                    type, new QueryParameters().add(type.getTableKey(),
                            documentCursor.get(chatId).getId()),
                    new QueryParameters().add("keywords",
                            parseGroupItems(editParams.get("keywords"))));
            editParams.remove("keywords");
        }
        if(editParams.containsKey("price")) {
            SqlStorage.getInstance().updateAll(
                    type, new QueryParameters().add(type.getTableKey(),
                            documentCursor.get(chatId).getId()),
                    new QueryParameters().add("price",
                            Integer.valueOf(editParams.get("price"))));
            editParams.remove("price");

        }

        if(!editParams.isEmpty()) {
            int itemId = -1;
            String itemType = type.getTableName();
            if (itemType.equals("book"))
                itemId = applyEditParamsBook(chatId);

            else if (itemType.equals("av_material"))
                itemId = applyEditParamsAvMaterial(chatId);

            else if (itemType.equals("journal_issue"))
                itemId = applyEditParamsJournalIssue(chatId);

            else if (itemType.equals("journal_article"))
                itemId = applyEditParamsArticle(chatId);

            ItemEntry entry = null;
            if (itemId != -1) {
                entry = (ItemEntry) SqlStorage.getInstance().find(type,
                        new QueryParameters().add(type.getTableKey(), itemId)).get(0);
            }

            if (entry != null) {
                documentCursor.put(chatId, entry);

                sendMessage(sender, update, entry.toString());
                keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId).getUser(),
                        "Book updated successfully");
            }
        }

    }

    private int applyEditParamsArticle(Long chatId) {
        //TODO: article_id
        return 0;
    }

    private int applyEditParamsJournalIssue(Long chatId) {
        if(editParams.containsKey("publisher")) SqlStorage.getInstance().updateAll(
                Resource.JournalIssue, new QueryParameters().add("journal_issue_id",
                        documentCursor.get(chatId).getId()),
                new QueryParameters().add("publisher", editParams.get("publisher")));

        if(editParams.containsKey("publishdate")) {
            SqlStorage.getInstance().updateAll(
                    Resource.JournalIssue, new QueryParameters().add("journal_issue_id",
                            documentCursor.get(chatId).getId()),
                    new QueryParameters().add("publication_date",
                            parseDate(editParams.get("publishdate"))));
        }

        if(editParams.containsKey("editors")) SqlStorage.getInstance().updateAll(
                Resource.AvMaterial, new QueryParameters().add("journal_issue_id",
                        documentCursor.get(chatId).getId()),
                new QueryParameters().add("editors",
                        parseGroupItems(editParams.get("editors"))));

        return documentCursor.get(chatId).getId();

    }

    private int applyEditParamsAvMaterial(Long chatId) {
        if(editParams.containsKey("authors")) SqlStorage.getInstance().updateAll(
                Resource.AvMaterial, new QueryParameters().add("av_material_id",
                        documentCursor.get(chatId).getId()),
                new QueryParameters().add("authors",
                        parseGroupItems(editParams.get("authors"))));
        return documentCursor.get(chatId).getId();
    }

    private int applyEditParamsBook(Long chatId) {
        System.out.println(editParams.toString());

        if(editParams.containsKey("publisher")) SqlStorage.getInstance().updateAll(
                Resource.Book, new QueryParameters().add("book_id",
                        documentCursor.get(chatId).getId()),
                new QueryParameters().add("publisher", editParams.get("publisher")));

        if(editParams.containsKey("bestseller")) {
            SqlStorage.getInstance().updateAll(
                    Resource.Book, new QueryParameters().add("book_id",
                            documentCursor.get(chatId).getId()),
                    new QueryParameters().add("is_bestseller",
                            Boolean.valueOf(editParams.get("bestseller"))));
        }

        if(editParams.containsKey("authors")) SqlStorage.getInstance().updateAll(
                Resource.Book, new QueryParameters().add("book_id",
                        documentCursor.get(chatId).getId()),
                new QueryParameters().add("authors",
                        parseGroupItems(editParams.get("authors"))));

        if(editParams.containsKey("publishdate")) {
            SqlStorage.getInstance().updateAll(
                    Resource.Book, new QueryParameters().add("book_id",
                            documentCursor.get(chatId).getId()),
                            new QueryParameters().add("publication_date",
                                    parseDate(editParams.get("publishdate"))));
        }

       return documentCursor.get(chatId).getId();
    }

    @Override
    public String run(AbsSender sender, Update update, String info) {
        return null;
    }

    String showEditFormat(String type) {
        if(type.equals("book")) return "title `Name`\ncopies `new number of copies` \n" +
        "authors `[author1...authorN]` \nbestseller `{true/false}` \nprice `price`\n" +
                "publisher `publisher` \nkeywords `[keyword1...keywordN]` \nreference `{true/false}`\n" +
                "publishdate `{DDMMYYYY}`";
        if(type.equals("avmaterial")) return "title `Name`\ncopies `new number of copies` \n" +
                "authors `[author1...authorN]`\nprice `price` \nkeywords `[keyword1...keywordN]` \n" +
                "reference `{true/false}`\n";
        if(type.equals("journalissue")) return "title `Name`\ncopies `new number of copies` \n" +
                "editors `[editor1...editorN]` \n publisher `publisher` \n" +
                "keywords `[keyword1...keywordN]` \nreference `{true/false}`\n" +
                "publishdate `{DDMMYYYY}`";
        if(type.equals("user")) return "name `Name`\n address `address` \n phone `number`\n" +
                "type `type` \nsubtype `subtype`\n login `login` \npassword `password`\n";
        return null;
    }

    String showEditExample(String type) {
        if(type.equals("book")) return "_title Hackers and Painters; copies 5; authors [Paul, Graham];" +
                " bestseller true; reference false; price 3000; publisher Harper-Collins; keywords [lisp, startup];" +
                " publishdate 05051998_";
        if (type.equals("avmaterial")) return "_title Kalkulus Video Training; copies 8; authors [Moe, Larry];" +
                " keywords [math, kalkoolos]; reference true_";
        if(type.equals("journalissue")) return "_title Hacker News magazine; copies 10; editors[Dang, Paul Graham, Peter Thiel;" +
                "publisher YCombinator; keywords [lisp, javascript]; publishdate 05211994; reference true_";
        if(type.equals("user")) return "_name John Smith; address Innopolis Street 7; phone +12345678912;" +
                " login johnsmith; password secret; type Faculty; subtype TA_";
        return null;
    }

    public void parseUserParameters(AbsSender sender, Update update, Long chatId, String input) {
        String tokens[] = input.split("[;]+");

        for(String i: tokens) {
            if(!i.equals(" ")) editParser.addEditParameters(i.trim());
        }

        //update parameters

        if(editParams.containsKey("name")) {
            SqlStorage.getInstance().updateAll(
                    Resource.User, new QueryParameters().add("user_id",
                            userCursor.get(chatId).getId()),
                    new QueryParameters().add("name", editParams.get("name")));
        }

        if(editParams.containsKey("address")) {
            SqlStorage.getInstance().updateAll(
                    Resource.User, new QueryParameters().add("user_id",
                            userCursor.get(chatId).getId()),
                    new QueryParameters().add("address", editParams.get("address")));
        }

        if(editParams.containsKey("phone")) {
            SqlStorage.getInstance().updateAll(
                    Resource.User, new QueryParameters().add("user_id",
                            userCursor.get(chatId).getId()),
                    new QueryParameters().add("phone_number", editParams.get("phone")));
        }

        if(editParams.containsKey("type")) {
            SqlStorage.getInstance().updateAll(
                    Resource.User, new QueryParameters().add("user_id",
                            userCursor.get(chatId).getId()),
                    new QueryParameters().add("type", editParams.get("type")));
        }

        if(editParams.containsKey("subtype")) {
            SqlStorage.getInstance().updateAll(
                    Resource.User, new QueryParameters().add("user_id",
                            userCursor.get(chatId).getId()),
                    new QueryParameters().add("subtype", editParams.get("subtype")));
        }

        if(editParams.containsKey("login")) {
            SqlStorage.getInstance().updateAll(
                    Resource.User, new QueryParameters().add("user_id",
                            userCursor.get(chatId).getId()),
                    new QueryParameters().add("login", editParams.get("login")));
        }

        if(editParams.containsKey("password")) {
            SqlStorage.getInstance().updateAll(
                    Resource.User, new QueryParameters().add("user_id",
                            userCursor.get(chatId).getId()),
                    new QueryParameters().add("password_hash", editParams.get("password").hashCode()));
        }

        keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId).getUser(),
                "User updated successfully!");

    }
}
