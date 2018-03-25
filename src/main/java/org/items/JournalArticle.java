package org.items;

import java.util.List;



public class JournalArticle extends Item {

    public JournalIssue getJournal() {
        return journal;
    }

    public List<String> getAuthors() {
        return authors;
    }

    @Override
    public int getCopiesNum() {
        return journal.getCopiesNum();
    }

    @Override
    public boolean isReference() {
        return journal.isReference();
    }

    @Override
    public int getPrice() {
        return journal.getPrice();
    }

    public void initializeJournal(JournalIssue issue) {
        if(journal == null) {
            journal = issue;
        } else {
            throw new RuntimeException("Journal is already initialized");
        }
    }

    List<String> authors;
    JournalIssue journal;
}
