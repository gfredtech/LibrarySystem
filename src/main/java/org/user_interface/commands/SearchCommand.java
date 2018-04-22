package org.user_interface.commands;

import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchCommand extends Command {
    @Override
    protected String run(String info) {

        switch (info) {
            case "startnext":
                showSearchExample();
                return "search_main";
            case "main":
                return showSearchType();

            case "type":
                return getTypeAndPingForSearch();
            case "engine":
                getSearchList();
                if(!items.isEmpty()) return "search_search";
                else return "search_engine";
            case "search":
                getIndex();
                return "search_index";
            case "index":
                getIndex();
                break;
            case "user":
                userQueries = retrieveUsers();
                return "search_userindex";
            case "userindex":
                getUserIndex();
        }

        return null;
    }

    private void getUserIndex() {
        int index;
        try {
            index = Integer.parseInt(update.getMessage().getText());
        }catch (NumberFormatException e) {
            e.printStackTrace();
            sendMessage("Invalid input.");
            index = -1;
        }
        if(index != -1) {
            List<UserEntry> userEntries = userQueries;
            sendMessage(userEntries.get(index - 1).getUser().toString());
        }
    }

    private ArrayList<UserEntry> retrieveUsers() {
        String text = update.getMessage().getText();
        List<UserEntry> userEntries = LibraryStorage.getInstance().find(Resource.User, new QueryParameters());
        ArrayList<UserEntry> userQueries = new ArrayList<>();
        for(UserEntry e: userEntries) {
            if(SearchUtils.filterBasedOnDistance(text, e)){
                userQueries.add(e);
            }
        }

        if(userQueries.size() > 0) {
            StringBuilder builder = new StringBuilder();
            int i = 1;
            for(UserEntry e: userQueries) {
                builder.append(i).append(". ");
                builder.append(e.getUser().getName()).append("\n");
                i += 1;
            }

            sendMessage("These are results that matched your queries\n" + builder.toString());
            return userQueries;
        }

        return null;
    }

    private String getTypeAndPingForSearch() {
        type = update.getMessage().getText();
        ArrayList<String> types = new ArrayList<>();
        types.add("Book");
        types.add("AV Material");
        types.add("Journal Issue");
        types.add("Journal Article");

        if(types.contains(type)) {
            sendMessage("Enter your query");
            return "search_engine";

        } else {
            sendMessage("Command not recognized");
            keyboardUtils.showDocumentKeyboard();
            return "search_type";
        }
    }

    private String showSearchType() {
        String message = update.getMessage().getText();
        if(message.equals("Search Document")) {
            keyboardUtils.showDocumentKeyboard();
            return "search_type";
        } else if(message.equals("Search User")) {
            searchUser();
            return "search_user";
        } else {
            sendMessage("Invalid input.");
        }
        return "search_main";
    }

    private void searchUser() {
        sendMessage("Enter the name of the user you're searching for: ");

    }

    private void getIndex() {
        int index;
        try {
            index = Integer.parseInt(update.getMessage().getText());
        }catch (NumberFormatException e) {
            e.printStackTrace();
            index = -1;
            sendMessage("Invalid input");
        }

        List<? extends ItemEntry> searchQueries = items.get(type);

        sendMessage(searchQueries.get(index - 1).getItem().toString());

    }


    private <T extends ItemEntry>
    String displaySearchResults(List<T> entries) {
        StringBuilder builder = new StringBuilder();
        int i = 1;
        for (T e : entries) {
            builder.append(i).append(". ");
            builder.append(e.getItem().getTitle());
            builder.append("\n");
            i += 1;
        }

        return builder.toString();
    }


    private void getSearchList() {
        String message = update.getMessage().getText();

        switch (type) {
            case "Book":
                List<BookEntry> books = LibraryStorage.getInstance().find(Resource.Book, new QueryParameters());
                List<BookEntry> bookQueries = new ArrayList<>();
                for (BookEntry e : books) {
                    if (SearchUtils.filterBasedOnDistance(message, e.getItem())) {
                        bookQueries.add(e);
                    }
                }
                if (bookQueries.size() > 0) {
                    items.put(type, bookQueries);
                    sendMessage("Here are items that matched your query:\n"
                            + displaySearchResults(bookQueries));

                } else {
                    sendMessage("No search items matched your query");
                }
                break;
            case "AV Material":
                // Av Material search
                List<AvMaterialEntry> avMaterials = LibraryStorage.getInstance().find(Resource.AvMaterial,
                        new QueryParameters());
                List<AvMaterialEntry> avMaterialQueries = new ArrayList<>();
                for (AvMaterialEntry e : avMaterials) {
                    if (SearchUtils.filterBasedOnDistance(message, e.getItem()))
                        avMaterialQueries.add(e);

                }

                if (avMaterialQueries.size() > 0) {
                    items.put(type, avMaterialQueries);

                    sendMessage("Here are items that matched your query:\n"
                            + displaySearchResults(avMaterialQueries));

                } else {
                    sendMessage("No search items matched your query");
                }
                break;
            case "Journal Issue":

                // Journal Issue Search
                List<JournalIssueEntry> issues = LibraryStorage.getInstance().find(Resource.JournalIssue,
                        new QueryParameters());
                List<JournalIssueEntry> issueQueries = new ArrayList<>();
                for (JournalIssueEntry e : issues) {
                    if (SearchUtils.filterBasedOnDistance(message, e.getItem())) {
                        issueQueries.add(e);
                    }
                }

                if (issueQueries.size() > 0) {
                    items.put(type, issueQueries);
                    sendMessage("Here are items that matched your query:\n"
                            + displaySearchResults(issueQueries));

                } else {
                    sendMessage("No search items matched your query");
                }
                break;
        }

    }

    private void showSearchExample() {
        keyboardUtils.showCRUDkeyboard("Search");

    }


    private static HashMap<String, List<? extends ItemEntry>> items = new HashMap<>();
    static List<UserEntry> userQueries;
    private static String type = "";
}
