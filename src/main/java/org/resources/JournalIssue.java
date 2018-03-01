package org.resources;

import java.time.LocalDate;
import java.util.List;

public class JournalIssue extends Item {

    public String getPublisher() {
        return publisher;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    @Override
    public String getType() {
        return type;
    }

    public List<String> getEditors() {
        return editors;
    }

    List<String> editors;
    String publisher;
    LocalDate publicationDate;
    static final String type = "journal_issue";
}
