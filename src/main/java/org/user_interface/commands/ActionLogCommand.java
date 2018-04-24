package org.user_interface.commands;

import org.items.AvMaterial;
import org.items.Book;
import org.items.JournalIssue;
import org.items.User;
import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.ActionLogEntry;
import org.storage.resources.ItemEntry;
import org.storage.resources.Resource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ActionLogCommand extends Command {
    @Override
    protected String run(String info) {
        switch (info){
            // Displays the action log
            case "start":
                List<ActionLogEntry> entries = LibraryStorage.
                        getInstance().find(Resource.ActionLog, new QueryParameters());
                StringBuilder builder = new StringBuilder();
                int i = 1;
                for(ActionLogEntry e: entries) {
                    builder.append(i).append(". ");
                    builder.append(getUserNameFromId(e.getUserId())).append(" ");
                    builder.append(parseActionParameters(e.getActionType(),
                            e.getActionParameters()));
                    builder.append("\n");
                    i += 1;
                }

                sendMessage(builder.toString());
        }

        return null;
    }

    // Return Patron's name from User Id
    String getUserNameFromId(int userId) {
        User user = LibraryStorage.getInstance().
                find(Resource.User, new QueryParameters().
                        add("user_id", userId)).get(0).getUser();
        return user.getName();
    }

    /**
     * Parses the parameters for an action log entry
     * @param actionType action log type
     * @param actionParameters the parameters of the action log
     * @return string description of the action
     */
   private String parseActionParameters(String actionType, String[] actionParameters) {
        switch (actionType){
            case "CheckOut":
                return checkOutParams(actionParameters);
            case "OutstandingRequest":
                return outstandingRequestParams(actionParameters);
            case "AddUser":
                return addUserParams(actionParameters);
            case "AddItem":
                return addItemParams(actionParameters);
            case "Renew":
                return renewParams(actionParameters);
        }
        return null;
    }

    // checkout parsing
    private String checkOutParams(String[] actionParameters) {
        System.out.println("does this execute");
        HashMap<String, String> types = new HashMap<>();
        types.put("book", "a Book");
        types.put("av_material", "an AV Material");
        types.put("journal_issue", "a Journal Issue");
        String [] params = actionParameters[0].split("\\s");
        String type = params[0];
        System.out.println(type);
        int itemId = Integer.valueOf(params[1].substring(1, params[1].length() - 1));
        String stringify = "checked out " + types.get(type) + " titled ";
        switch (type) {
            case "book":
                stringify += LibraryStorage.
                        getInstance().find(Resource.Book, new QueryParameters()
                        .add("book_id", itemId)).get(0).getItem().getTitle();

                break;
            case "av_material":
                AvMaterial a = LibraryStorage
                        .getInstance().find(Resource.AvMaterial, new QueryParameters()
                        .add("av_material_id", itemId)).get(0).getItem();
                stringify += a.getTitle();
                break;
            case "journal_issue":
                JournalIssue j = LibraryStorage
                        .getInstance().find(Resource.JournalIssue, new QueryParameters()
                        .add("journal_issue_id", itemId)).get(0).getItem();
                stringify += j.getTitle();
                break;
        }
        stringify += " on " + actionParameters[1];

        return stringify;
    }

    //outstanding request parameters
    private String outstandingRequestParams(String [] actionParameters) {
        String [] params = actionParameters[0].split("\\s");
        int itemId = Integer.valueOf(params[1].substring(1, params[1].length() - 1));
        String stringify = "placed an outstanding request for ";
        switch (params[0]) {
            case "book":
                Book b = LibraryStorage.getInstance()
                        .find(Resource.Book, new QueryParameters()
                                .add("book_id", itemId)).get(0).getItem();
                stringify += b.getTitle();
                break;
            case "av_material":
                AvMaterial a = LibraryStorage.getInstance()
                        .find(Resource.AvMaterial, new QueryParameters()
                        .add("av_material_id", itemId)).get(0).getItem();
                stringify += a.getTitle();
                break;
            case "journal_issue":
                JournalIssue j = LibraryStorage.getInstance()
                        .find(Resource.JournalIssue, new QueryParameters()
                        .add("journal_issue_id", itemId)).get(0).getItem();
                stringify += j.getTitle();
                break;
        }

        return stringify;
    }

    // add user parameter parser
    private String addUserParams(String[] actionParameters) {
        String[] params = actionParameters[0].split("\\s");
        int userId = Integer.valueOf(params[1].substring(1, params[1].length() - 1));
        User u = LibraryStorage.getInstance().find(Resource.User, new QueryParameters()
         .add("user_id", userId)).get(0).getUser();
        return "added the user " + u.getName();
    }

    // add item parameter parser
    private String addItemParams(String[] actionParameters) {
        HashMap<String, String> types = new HashMap<>();
        types.put("book", "the Book");
        types.put("av_material", "the AV Material");
        types.put("journal_issue", "the Journal Issue");
        types.put("article", "the Journal Article");
        String params = actionParameters[0];
        String type = params.split("\\s")[0];
        params = params.trim().replace(type + " ", "");
        params = params.substring(1, params.length() - 1);

        return "added the " + types.get(type) + " " + params;
    }

    // renew parameters parser
    private String renewParams(String[] actionParameters) {
        String item = actionParameters[0];
        String checkOutDate = actionParameters[1];
        String dueDate = actionParameters[3];
        String stringify = "renewed the item ";

        HashMap<String, String> types = new HashMap<>();

        String type = item.split("\\s")[0];
        String id = item.split("\\s")[1];
        int itemId = Integer.valueOf(id.substring(1, id.length() - 1));

        switch (type) {
            case "book":
                Book b = LibraryStorage.getInstance()
                        .find(Resource.Book, new QueryParameters()
                                .add("book_id", itemId)).get(0).getItem();
                stringify += b.getTitle();

                break;
            case "av_material":
                 stringify += LibraryStorage.getInstance()
                        .find(Resource.AvMaterial, new QueryParameters()
                                .add("av_material_id", itemId)).get(0).getItem().getTitle();
                break;
            case "journal_issue":
                JournalIssue j = LibraryStorage.getInstance()
                        .find(Resource.JournalIssue, new QueryParameters()
                                .add("journal_issue_id", itemId)).get(0).getItem();
                stringify += j.getTitle();
                break;
        }
        stringify += " on " + checkOutDate + ". The due date is " + dueDate;
        return stringify;
    }
}
