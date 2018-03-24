package org.resources;

import org.storage.QueryParameters;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;


public class JournalIssueSerializer extends ItemSerializer<JournalIssue>  {

    @Override
    public QueryParameters toQueryParameters(JournalIssue journal) {
        return new QueryParameters()
                .add("price", journal.getPrice())
                .add("title", journal.getTitle())
                .add("keywords", journal.getKeywords())
                .add("copy_num", journal.getCopiesNum())
                .add("editors", journal.getEditors())
                .add("publisher", journal.getPublisher())
                .add("publication_date", journal.getPublicationDate())
                .add("is_reference", journal.isReference());
    }

    @Override
    public JournalIssue fromResultSet(ResultSet rs) throws SQLException {
        JournalIssueFactory f = new JournalIssueFactory();
        f.setPublisher(rs.getString("publisher"));

        String[] editorsArray =
                (String[])rs.getArray("editors").getArray();
        List<String> editors = Arrays.asList(editorsArray);
        f.setEditors(editors);

        LocalDate publicationDate =
                rs.getDate("publication_date").toLocalDate();
        f.setPublicationDate(publicationDate);


        factory = f;

        return super.fromResultSet(rs);
    }
}
