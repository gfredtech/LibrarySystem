package org.user_interface.commands;

import org.items.AvMaterial;
import org.items.Book;
import org.items.JournalArticle;
import org.items.JournalIssue;
import org.storage.resources.UserEntry;

import java.util.List;

class SearchUtils {

    private static int distance(String a, String b) {
        a = a.toLowerCase().replaceAll("\\s+","");
        b = b.toLowerCase().replaceAll("\\s+","");
        // i == 0
        int[] costs = new int[b.length() + 1];
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

    static boolean filterBasedOnDistance(String text, Book e) {
        String[] titles = e.getTitle().split("\\s");
        for (String t : titles) {
            if (distance(t, text) <= 3)
                return true;
        }
        if(distance(text,e.getTitle()) <= 3) return true;

        if (distance(e.getPublisher(), text) <= 3)
            return true;

        else {
            List<String> authors = e.getAuthors();
            for (String a : authors) {
                if (distance(a, text) <= 3) {
                    return true;
                }
            }

            List<String> keywords = e.getKeywords();
            for (String k : keywords) {
                if (distance(k, text) <= 3)
                    return true;
            }
        }

        return false;
    }

    static boolean filterBasedOnDistance(String text, AvMaterial e) {
        String[] titles = e.getTitle().split("\\s");
        for (String t : titles) {
            if (distance(t, text) <= 3)
                return true;
        }
        if(distance(text,e.getTitle()) <= 3) return true;

        List<String> authors = e.getAuthors();
        for (String a : authors) {
            if (distance(a, text) <= 3)
                return true;

        }

        List<String> keywords = e.getKeywords();
        for (String k : keywords) {
            if (distance(k, text) <= 3)
                return true;
        }

        return false;
    }

    static boolean filterBasedOnDistance(String text, JournalIssue e) {
        String[] titles = e.getTitle().split("\\s");
        for (String t : titles) {
            if (distance(t, text) <= 3)
                return true;
        }
        if(distance(text,e.getTitle()) <= 3) return true;

        List<String> editors = e.getEditors();
        for (String a : editors) {
            if (distance(a, text) <= 3)
                return true;

        }

        List<String> keywords = e.getKeywords();
        for (String k : keywords) {
            if (distance(k, text) <= 3)
                return true;
        }

        return false;
    }

    static boolean filterBasedOnDistance(String text, UserEntry e) {
        String name = e.getUser().getName();
        if (distance(name, text) <= 3) {
            return true;
        } else if (distance(text, e.getUser().getAddress()) <= 3) {
            return true;
        } else return distance(text, e.getUser().getType()) <= 3;
    }
}

