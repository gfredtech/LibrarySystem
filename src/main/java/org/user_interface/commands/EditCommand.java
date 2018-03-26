package org.user_interface.commands;

import org.storage.QueryParameters;
import org.storage.SqlStorage;
import org.storage.resources.ItemEntry;
import org.storage.resources.Resource;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import java.util.HashMap;

public class EditCommand extends Command {
    @Override
    public String run(AbsSender sender, Update update, String info) {
        Long chatId;
        if (update.hasMessage()) chatId = update.getMessage().getChatId();
        else chatId = update.getCallbackQuery().getMessage().getChatId();

        switch (info) {
            case "startnext":
                keyboardUtils.showCRUDkeyboard(sender, update, "Edit");
                return "edit_main";

            case "main":
                String message = update.getMessage().getText();
                if (message != null && message.equals("Edit Document")) {
                    keyboardUtils.showEditDocumentKeyboard(sender, update);
                    return "edit_documenttype";
                } else if (message != null && message.equals("Edit User")) {
                    //TODO: EDIT users
                    return "edit_users";
                }

            case "documenttype":
                message = update.getMessage().getText();
                switch (message) {
                    case "Book":
                        sendMessage(sender, update, "Here's a list of books. " +
                                "Select the one you'd like to edit");
                        listBooks(sender, update);
                        return "edit_booklist";

                    case "AV Material":
                        sendMessage(sender, update, "Here's a list of AV Materials. Select the " +
                                "one you'd like to edit.");
                        listAvMaterials(sender, update);
                        return "edit_avmateriallist";

                    case "Journal Issue":
                        sendMessage(sender, update,
                                "Here's a list of Journal Issues. Select the " +
                                        "one you'd like to edit");
                        listJournalIssues(sender, update);
                        return "edit_journalissuelist";

                    case "Journal Article":
                        sendMessage(sender, update,
                                "Here's a list of Journal Articles. Select the " +
                                        "one you'd like to edit");
                        listJournalArticles(sender, update);
                        return "edit_journalarticlelist";
                }

            case "booklist":
                selectDocToEdit(sender, update, chatId, "book");
                return "edit_params";

            case "avmateriallist":
                selectDocToEdit(sender, update, chatId, "avmaterial");
                return "edit_params";

            case "journalissuelist":
                selectDocToEdit(sender, update, chatId, "journalissue");
                return "edit_params";

            case "journalarticlelist":
                selectDocToEdit(sender, update, chatId, "journalarticlelist");
                return "edit_params";

            case "params":
                parseParameters(sender, update, chatId);
                return "menu_main";

        }
        return null;
    }

    private void parseParameters(AbsSender sender, Update update, Long chatId) {
        String input = update.getMessage().getText().trim();

        switch (documentCursor.get(chatId).getResourceType().getTableName()) {
            case "book":
                editParser.parseBookParameters(sender, update, chatId, input);
                break;
            case "avmaterial":
                editParser.parseAvMaterialParameters(input);
                break;
            case "journalissue":
                editParser.parseJournalIssueParameters(input);
                break;
            case "journalarticle":
                editParser.parseJournalArticleParameters(input);
                break;
        }

    }

    private void selectDocToEdit(AbsSender sender, Update update, Long chatId, String type) {
        ItemEntry item = null;
        String input = update.getMessage().getText();
        int index = Integer.parseInt(input);
        switch (type) {
            case "book":
                item = SqlStorage.getInstance().find(Resource.Book,
                        new QueryParameters()).get(index - 1);
                break;
            case "avmaterial":
                item = SqlStorage.getInstance().find(Resource.AvMaterial,
                        new QueryParameters()).get(index - 1);
                break;
            case "journalissue":
                item = SqlStorage.getInstance().find(Resource.JournalIssue,
                        new QueryParameters()).get(index - 1);
                break;
            case "journalarticle":
                item = SqlStorage.getInstance().find(Resource.JournalArticle,
                        new QueryParameters()).get(index - 1);
                break;
        }

        if (item != null) {
            documentCursor.put(chatId, item);
            sendMessage(sender, update, item.getItem().toString());

            String parameterlist = "title `Name` \ncopies `new number of copies` \n" +
                    "*If you want to delete the item, type delete*";
            sendMessage(sender, update, "This is the parameter list for editing a use book; first type the key of the " +
                    "parameter you want to edit, followed by its new value, then separate each with a comma(,)");
            sendMessage(sender, update, parameterlist);

        }
    }

    static HashMap<String, String> editParams = new HashMap<>();
    static EditParser editParser = new EditParser();

}
