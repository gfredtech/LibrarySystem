package org.user_interface.commands;

import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.ItemEntry;
import org.storage.resources.Resource;
import org.storage.resources.UserEntry;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import java.util.HashMap;
import java.util.List;

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
                    showUsersInDatabase(sender, update, chatId);
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

            case "users":
                selectDocToEdit(sender, update, chatId, "user");
                return "edit_params";

            case "params":
                parseParameters(sender, update, chatId);
                return "menu_main";


        }
        return null;
    }

    private List<UserEntry> showUsersInDatabase(AbsSender sender, Update update, Long chatId) {
        sendMessage(sender, update, "Here's a list of all users: Select the user you want to edit.");
        List<UserEntry> userEntryList = LibraryStorage.getInstance().find(Resource.User,
                new QueryParameters());
        if(userEntryList == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        int i = 1;
        for(UserEntry e: userEntryList) {
            builder.append(i).append(". ");
            builder.append(e.getUser().getName());
            builder.append("\n");
            i += 1;
        }

        sendMessage(sender, update, builder.toString());
        return userEntryList;
    }

    private void parseParameters(AbsSender sender, Update update, Long chatId) {
        String input = update.getMessage().getText().trim();
        String type = userCursor.get(chatId) == null
                ? documentCursor.get(chatId).getResourceType().getTableName()
                : userCursor.get(chatId).getResourceType().getTableName();

        if (input.equals("delete")) {
            switch (type) {
                case "user_card":
                    //TODO: remove user in `userCursor`
                    default:
                        //TODO: delete document in `documentCursor`
            }
//           SqlStorage.getInstance().removeBook(currentBook.getId());
            keyboardUtils.showMainMenuKeyboard(sender, update, currentUser.get(chatId).getUser(),
                      "Entity removed successfully");
            return;
        }

        switch (type) {
            case "book":
                editParser.coreParser(sender, update, Resource.Book, chatId, input);
                break;
            case "av_material":
                editParser.coreParser(sender, update, Resource.AvMaterial, chatId, input);
                break;
            case "journal_issue":
                editParser.coreParser(sender, update, Resource.JournalIssue, chatId, input);
                break;
            case "article":
                editParser.coreParser(sender, update, Resource.JournalArticle, chatId, input);
                break;
            case "user_card":
                editParser.parseUserParameters(sender, update, chatId, input);

        }

    }

    private void selectDocToEdit(AbsSender sender, Update update, Long chatId, String type) {
        ItemEntry item = null;
        UserEntry user = null;
        String input = update.getMessage().getText();
        int index = Integer.parseInt(input);

            switch (type) {
                case "book":
                    item = LibraryStorage.getInstance().find(Resource.Book,
                            new QueryParameters()).get(index - 1);
                    break;
                case "avmaterial":
                    item = LibraryStorage.getInstance().find(Resource.AvMaterial,
                            new QueryParameters()).get(index - 1);
                    break;
                case "journalissue":
                    item = LibraryStorage.getInstance().find(Resource.JournalIssue,
                            new QueryParameters()).get(index - 1);
                    break;
                case "journalarticle":
                    item = LibraryStorage.getInstance().find(Resource.JournalArticle,
                            new QueryParameters()).get(index - 1);
                    break;
                case "user":
                    user = LibraryStorage.getInstance().find(Resource.User,
                            new QueryParameters()).get(index - 1);
                    break;
            }


        if (user != null) { userCursor.put(chatId, user); sendMessage(sender, update, user.getUser().toString());}
        else if(item != null) { documentCursor.put(chatId, item); sendMessage(sender, update, item.getItem().toString());}

            String details = "This is the parameter list for editing; first type the key of the " +
                    "parameter you want to edit, followed by its new value, then separate each with a semicolon(;)\n" +
                    editParser.showEditFormat(type) + "\n*If you want to delete the item, type* `delete`\n" +
                                        "Example below:\n"
                    + editParser.showEditExample(type);
            sendMessage(sender, update, details);
    }

    static HashMap<String, String> editParams = new HashMap<>();
    static EditParser editParser = new EditParser();

}
