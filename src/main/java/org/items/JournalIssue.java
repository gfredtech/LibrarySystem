package org.items;

import java.time.LocalDate;
import java.util.List;

public class JournalIssue extends Item {

    public String getPublisher() {
        return publisher;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public List<String> getEditors() {
        return editors;
    }

    List<String> editors;
    String publisher;
    LocalDate publicationDate;
}