package org.storage.resources;

import org.items.JournalIssue;
import org.items.JournalIssueFactory;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;


public class JournalIssueEntry extends ItemEntry<JournalIssue> {

    public JournalIssueEntry(ResultSet rs) throws SQLException {
        super(rs);
    }


    @Override
    public Resource<JournalIssueEntry> getResourceType() {
        return Resource.JournalIssue;
    }

    @Override
    public JournalIssueFactory initFactory(ResultSet rs) throws SQLException {
        JournalIssueFactory f = new JournalIssueFactory();
        f.publisher(rs.getString("publisher"));

        String[] editorsArray =
                (String[])rs.getArray("editors").getArray();
        List<String> editors = Arrays.asList(editorsArray);
        f.editors(editors);

        LocalDate publicationDate =
                rs.getDate("publication_date").toLocalDate();
        f.publicationDate(publicationDate);

        return f;
    }
}
