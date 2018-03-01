package org.resources;

import java.util.List;



public class JournalArticle {

    public String getTitle() {
        return title;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public JournalIssue getJournal() {
        return journal;
    }

    public List<String> getAuthors() {
        return authors;
    }

    String title;
    List<String> keywords;
    List<String> authors;
    JournalIssue journal;
}
