package org.storage.resources;

import org.items.Book;
import org.items.BookFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;


public class BookEntry extends ItemEntry<Book> {

    public BookEntry(ResultSet rs) throws SQLException {
        super(rs);
    }

    @Override
    public Resource<BookEntry> getResourceType() {
        return Resource.Book;
    }

    @Override
    protected BookFactory initFactory(ResultSet rs) throws SQLException {
        BookFactory factory = new BookFactory()
                .publisher(rs.getString("publisher"))
                .authors(Arrays.asList((String[])rs.getArray("authors").getArray()))
                .publicationDate(rs.getDate("publication_date").toLocalDate());

        if (rs.getBoolean("is_bestseller")) {
            factory.isBestseller();
        } else {
            factory.isNotBestseller();
        }
        return factory;
    }


}