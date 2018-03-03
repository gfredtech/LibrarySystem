package org.resources;

import org.storage.QueryParameters;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;


public class JournalArticleSerializer extends ItemSerializer<JournalArticle> {


    public QueryParameters toQueryParameters(JournalArticle article) {
        return new QueryParameters()
                .add("title", article.getTitle())
                .add("authors", article.getAuthors())
                .add("keywords", article.getKeywords());
    }



    public JournalArticle fromResultSet(ResultSet rs) throws SQLException {
        JournalArticleFactory j = new JournalArticleFactory();

        j.setTitle(rs.getString("title"));

        String[] authorsArray =
                (String[])rs.getArray("authors").getArray();
        List<String> authors = Arrays.asList(authorsArray);
        j.setAuthors(authors);

        String[] keywordsArray =
                (String[])rs.getArray("keywords").getArray();
        List<String> keywords = Arrays.asList(keywordsArray);
        j.setKeywords(keywords);

        factory = j;
        return j.build(rs.getInt("article_id"));
    }
}