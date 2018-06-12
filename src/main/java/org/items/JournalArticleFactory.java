package org.items;

import java.util.List;

public class JournalArticleFactory extends ItemFactory<JournalArticleFactory, JournalArticle> {

    public JournalArticleFactory() {
        super(new JournalArticle());
    }

    @Override
    public JournalArticle build() {
        assert item.authors != null;
        assert item.journal != null;
        return item;
    }

    public void authors(List<String> authors) {
        item.authors = authors;
    }

    public void journal(JournalIssue journal) {
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