package org.resources;

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
    public void setCopiesNum(int copiesNum) {
        ;
    }

    /**
     * @deprecated the journal reference property is used instead.
     */
    @Override
    @Deprecated
    public void setAsReference() {
        ;
    }

    /**
     * @deprecated the journal price is used instead.
     */
    @Override
    @Deprecated
    public void setPrice(int price) {
        ;
    }
}
