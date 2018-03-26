package org.storage.resources;

import org.items.JournalArticle;
import org.items.JournalArticleFactory;
import org.storage.QueryParameters;
import org.storage.EntrySerializer;
import org.storage.SqlStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;


public class JournalArticleEntry extends ItemEntry<JournalArticle> {

    public JournalArticleEntry(ResultSet rs) throws SQLException {
        super(rs);
    }

    @Override
    public Resource<JournalArticleEntry> getResourceType() {
        return Resource.JournalArticle;
    }

    @Override
    public QueryParameters toQueryParameters() {
        return EntrySerializer.serialize(item).add("journal_id", journal.getId());
    }

    @Override
    public JournalArticleFactory initFactory(ResultSet rs) throws SQLException {
        JournalArticleFactory j = new JournalArticleFactory();

        j.title(rs.getString("title"));

        String[] authorsArray =
                (String[])rs.getArray("authors").getArray();
        List<String> authors = Arrays.asList(authorsArray);
        j.authors(authors);

        String[] keywordsArray =
                (String[])rs.getArray("keywords").getArray();
        List<String> keywords = Arrays.asList(keywordsArray);
        j.keywords(keywords);

        journal = SqlStorage.getInstance().get(
                Resource.JournalIssue,  rs.getInt("journal_id")).get();
        j.journal(journal.item);

        return j;
    }

    public JournalIssueEntry getJournal() {
        return journal;
    }

    JournalIssueEntry journal;
}