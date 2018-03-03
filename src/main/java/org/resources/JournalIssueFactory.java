package org.resources;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;


/**
 * A factory intended to create instances of the JournalIssue class
 */
public class JournalIssueFactory extends ItemFactory<JournalIssue> {
    public JournalIssueFactory() {
        super(new JournalIssue());
    }

    public void setEditors(List<String> editors) {
        item.editors = editors;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        item.publicationDate = publicationDate;
    }

    public void setPublisher(String publisher) {
        item.publisher = publisher;
    }
}

