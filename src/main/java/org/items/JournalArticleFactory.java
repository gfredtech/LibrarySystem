package org.items;

import java.util.List;

public class JournalArticleFactory extends ItemFactory<JournalArticle> {

    public JournalArticleFactory() {
        super(new JournalArticle());
    }

    public void setAuthors(List<String> authors) {
        item.authors = authors;
    }

    public void setJournalIssue(JournalIssue journal) {
        item.journal = journal;
    }

    /**
     * @deprecated the num of journal copies is used instead.
     */
    @Override
    @Deprecated
    public JournalArticleFactory copiesNum(int copiesNum) {
        return this;
    }

    /**
     * @deprecated the journal reference property is used instead.
     */
    @Override
    @Deprecated
    public JournalArticleFactory isReference() {
        return this;
    }

    /**
     * @deprecated the journal price is used instead.
     */
    @Override
    @Deprecated
    public JournalArticleFactory price(int price) {
        return this;
    }
}