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
                return "search_search";
            case "search":
               getSearchList();
                return "search_index";
            case "index":
                getIndex();
                break;
        }

        return null;
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

        if(index != -1) {
            int i = 0;
            if(items.containsKey("book"))
                i = items.get("book").size();

            if(index <= i) {
                sendMessage(items.get("book").get(index - 1).getItem().toString());
                return;
            }
            if(items.containsKey("avmaterial"))
            i += items.get("avmaterial").size();

            if(index <= i) {
                sendMessage(items.get("avmaterial").get(index - i).getItem().toString());
                return;
            }
            if(items.containsKey("issue"))
                i += items.get("issue").size();

            if(index <= i) {
                sendMessage(items.get("issue").get(index - i).getItem().toString());
            }

        }
    }


    private <T extends ItemEntry>
    String displaySearchResults(int i, List<T> entries) {
        StringBuilder builder = new StringBuilder();

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

        //Books
        List<BookEntry> books = LibraryStorage.getInstance().find(Resource.Book, new QueryParameters());
        List<BookEntry> bookQueries = new ArrayList<>();
        for (BookEntry e : books) {
            if (SearchUtils.filterBasedOnDistance(message, e.getItem())) {
                bookQueries.add(e);
            }
        }

        if(bookQueries.size() > 0) items.put("book", bookQueries);

        // Av Material search
        List<AvMaterialEntry> avMaterials = LibraryStorage.getInstance().find(Resource.AvMaterial,
                new QueryParameters());
        List<AvMaterialEntry> avMaterialQueries = new ArrayList<>();
        for (AvMaterialEntry e : avMaterials) {
            if (SearchUtils.filterBasedOnDistance(message, e.getItem())) {
                avMaterialQueries.add(e);
            }
        }

        if(avMaterialQueries.size() > 0) items.put("avmaterial", avMaterialQueries);

        // Journal Issue Search
        List<JournalIssueEntry> issues = LibraryStorage.getInstance().find(Resource.JournalIssue,
                new QueryParameters());
        List<JournalIssueEntry> issueQueries = new ArrayList<>();
        for(JournalIssueEntry e: issues) {
            if(SearchUtils.filterBasedOnDistance(message, e.getItem())) {
                issueQueries.add(e);
            }
        }

        if(issueQueries.size() > 0) items.put("issue", issueQueries);


        int i = 1;
        String bookResults = displaySearchResults(i, bookQueries);

        i += bookQueries.size();
        String avMaterialResults = displaySearchResults(i, avMaterialQueries);
        i += avMaterialQueries.size();
        String issueResults = displaySearchResults(i, issueQueries);

        if(bookResults.length() + avMaterialResults.length() + issueResults.length()<= 0) {
            sendMessage("Sorry, no items matched your query.");
        } else {

            if (bookResults.length() > 0 || avMaterialResults.length() > 0 || issueResults.length() > 0)
                sendMessage("Here are items matching your query:\n" + bookResults
                + avMaterialResults + issueResults);
        }

    }

    private void showSearchExample() {
        String message = "Welcome to the search page. Enter your query.\n";
        sendMessage(message);

    }


    private static HashMap<String, List<? extends ItemEntry>> items = new HashMap<>();
}
