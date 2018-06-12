package org.user_interface.commands;

import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.ItemEntry;
import org.storage.resources.Resource;
import org.storage.resources.UserEntry;

import java.util.HashMap;
import java.util.List;

public class EditCommand extends Command {
    @Override
    public String run(String info) {
        Long chatId;
        if (update.hasMessage()) chatId = update.getMessage().getChatId();
        else chatId = update.getCallbackQuery().getMessage().getChatId();

        switch (info) {
            case "startnext":
                UserEntry e = currentUser.get(chatId);
                String auth = authorizationChecker(e);
                if(auth!= null) return auth;
                keyboardUtils.showCRUDkeyboard("Edit");
                return "edit_main";

            case "main":
                String message = update.getMessage().getText();
                if (message != null && message.equals("Edit Document")) {
                    keyboardUtils.showDocumentKeyboard();
                    return "edit_documenttype";
                } else if (message != null && message.equals("Edit User")) {
                    showUsersInDatabase();
                    return "edit_users";
                }

            case "documenttype":
                message = update.getMessage().getText();
                switch (message) {
                    case "Book":
                        sendMessage("Here's a list of books. " +
                                "Select the one you'd like to edit");
                        listBooks();
                        return "edit_booklist";

                    case "AV Material":
                        sendMessage("Here's a list of AV Materials. Select the " +
                                "one you'd like to edit.");
                        listAvMaterials();
                        return "edit_avmateriallist";

                    case "Journal Issue":
                        sendMessage(
                                "Here's a list of Journal Issues. Select the " +
                                        "one you'd like to edit");
                        listJournalIssues();
                        return "edit_journalissuelist";

                    case "Journal Article":
                        sendMessage(
                                "Here's a list of Journal Articles. Select the " +
                                        "one you'd like to edit");
                        listJournalArticles();
                        return "edit_journalarticlelist";
                }

            case "booklist":
                selectDocToEdit(chatId, "book");
                return "edit_params";

            case "avmateriallist":
                selectDocToEdit(chatId, "avmaterial");
                return "edit_params";

            case "journalissuelist":
                selectDocToEdit(chatId, "journalissue");
                return "edit_params";

            case "journalarticlelist":
                selectDocToEdit(chatId, "journalarticlelist");
                return "edit_params";

            case "users":
                selectDocToEdit(chatId, "user");
                return "edit_params";

            case "params":
                parseParameters(chatId);
                return "menu_main";


        }
        return null;
    }

    private void showUsersInDatabase() {
        sendMessage("Here's a list of all users: Select the user you want to edit.");
        List<UserEntry> userEntryList = LibraryStorage.getInstance().find(Resource.User,
                new QueryParameters());
        if(userEntryList == null) {
            return;
        }

        StringBuilder builder = new StringBuilder();
        int i = 1;
        for(UserEntry e: userEntryList) {
            builder.append(i).append(". ");
            builder.append(e.getUser().getName());
            builder.append("\n");
            i += 1;
        }

        sendMessage(builder.toString());
    }

    private void parseParameters(Long chatId) {
        String input = update.getMessage().getText().trim();
        String type = userCursor.get(chatId) == null
                ? documentCursor.get(chatId).getResourceType().getTableName()
                : userCursor.get(chatId).getResourceType().getTableName();

        if (input.equals("delete")) {
            System.out.println("i" + type);
            switch (type) {
                case "user_card":

                    LibraryStorage.getInstance().removeAll(
                            Resource.User, new QueryParameters().add("user_id",
                            userCursor.get(chatId).getId()));
                    break;
                    default:

                        ItemEntry e = documentCursor.get(chatId);
                       LibraryStorage.getInstance().removeAll(
                                e.getResourceType(), new QueryParameters().add(
                                        e.getResourceType().getTableKey(), e.getId()));
                       break;

            }
            keyboardUtils.showMainMenuKeyboard(currentUser.get(chatId).getUser(),
                      "Entity removed successfully");
            return;
        }

        switch (type) {
            case "book":
                editParser.coreParser(Resource.Book, chatId, input);
                break;
            case "av_material":
                editParser.coreParser(Resource.AvMaterial, chatId, input);
                break;
            case "journal_issue":
                editParser.coreParser(Resource.JournalIssue, chatId, input);
                break;
            case "article":
                editParser.coreParser(Resource.JournalArticle, chatId, input);
                break;
            case "user_card":
                editParser.parseUserParameters(chatId, input);
                break;

        }

    }

    private void selectDocToEdit(Long chatId, String type) {
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


        if (user != null) { userCursor.put(chatId, user); sendMessage(user.getUser().toString());}
        else if(item != null) { documentCursor.put(chatId, item); sendMessage(item.getItem().toString());}

            String details = "This is the parameter list for editing; first type the key of the " +
                    "parameter you want to edit, followed by its new value, then separate each with a semicolon(;)\n" +
                    editParser.showEditFormat(type) + "\n*If you want to delete the item, type* `delete`\n" +
                                        "Example below:\n"
                    + editParser.showEditExample(type);
            sendMessage(details);
    }

    static HashMap<String, String> editParams = new HashMap<>();
    static EditParser editParser = new EditParser();

}
