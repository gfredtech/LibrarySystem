package org.user_interface.commands;

import org.items.Book;
import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.BookEntry;
import org.storage.resources.Resource;

import java.util.ArrayList;
import java.util.List;

public class SearchCommand extends Command {
    @Override
    protected String run(String info) {
        switch (info) {
            case "startnext":
                sendMessage("Welcome to the search page. Enter your query.");
                return "search_search";
            case "search":
               List<BookEntry> bookEntries = getSearchList();
                displaySearchResults(bookEntries);
                return "search_index";
            case "index":
                //TODO
                break;
        }

        return null;
    }

    private void displaySearchResults(List<BookEntry> entries) {
        StringBuilder builder = new StringBuilder();
        int i = 1;
        for (BookEntry e : entries) {
            builder.append(i).append(". ");
            builder.append(e.getItem().getTitle());
            builder.append("\n");
            i += 1;
        }

        if (builder.length() > 0) {
            sendMessage("This is the list of items matching your query: Enter the index of the item you want");
            sendMessage(builder.toString());
        }
    }

    private List<BookEntry> getSearchList() {
        String message = update.getMessage().getText();
        List<BookEntry> books = LibraryStorage.getInstance().find(Resource.Book, new QueryParameters());
        List<BookEntry> queries = new ArrayList<>();
        for(BookEntry e: books) {
            if(filterBasedOnDistance(message, e.getItem())) {
                queries.add(e);
            }
        }

        System.out.println(queries.toString());
        return queries;
    }


    private static int distance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        // i == 0
        int [] costs = new int [b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

    private boolean filterBasedOnDistance(String text, Book e) {
        String[] titles = e.getTitle().split("\\s");
        for(String t: titles) {
            System.out.println(distance(text, t));
            if (distance(t, text) <= 3)
                return true;
        }

        if(distance(e.getPublisher(), text) <= 3)
            return true;

        else {
            List<String> authors = e.getAuthors();
            for (String a : authors) {
                if (distance(a, text) <= 3) {
                    return true;
                }
            }
        }

        return false;
    }
}
