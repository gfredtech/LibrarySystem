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
        //TODO:
    }


    private <T extends ItemEntry>
    String displaySearchResults(int i, List<T> entries) {
        StringBuilder builder = new StringBuilder();
        if(i == 0) i += 1;

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


        String bookResults = displaySearchResults(1, bookQueries);
        String avMaterialResults = displaySearchResults(bookQueries.size() == 0 ? 1 : bookQueries.size(), avMaterialQueries);
        String issueResults = displaySearchResults(avMaterialQueries.size() == 0 ? bookQueries.size()
                : avMaterialQueries.size(), issueQueries);

        if(bookResults.length() + avMaterialResults.length() + issueResults.length()<= 0) {
            sendMessage("Sorry, no items matched your query.");
        } else {

            if (bookResults.length() > 0)
                sendMessage("Here are books matching your query:\n" + bookResults);
            if (avMaterialResults.length() > 0)
                sendMessage("Here are Av materials matching your query:\n" + avMaterialResults);
            if (issueResults.length() > 0)
                sendMessage("Here are Journal Issues matching your query:\n" + issueResults);
        }

    }

    private void showSearchExample() {
        String message = "Welcome to the search page. Enter your query.\n" +
                "Remember that you can also build advanced queries. For example. if you want to query all " +
                " items documents created by Alan Turing before 1970, type `author: Alan Turing date: 1970`" +
                " The search parameters are shown below\n" +
                "";
        sendMessage(message);

    }


    static HashMap<String, List<? extends ItemEntry>> items = new HashMap<>();
}
