package org.items;

import java.time.LocalDate;
import java.util.List;


/**
 * A factory intended to create instances of the JournalIssue class
 */
public class JournalIssueFactory extends ItemFactory<JournalIssue> {
    public JournalIssueFactory() {
        super(new JournalIssue());
    }

    @Override
    public JournalIssue build() {
        assert item.editors != null;
        assert item.publisher != null;
        assert item.publicationDate != null;
        return item;
    }

    public JournalIssueFactory editors(List<String> editors) {
        item.editors = editors;
        return this;
    }

    public JournalIssueFactory publicationDate(LocalDate publicationDate) {
        item.publicationDate = publicationDate;
        return this;
    }

    public JournalIssueFactory publisher(String publisher) {
        item.publisher = publisher;
        return this;
    }
}