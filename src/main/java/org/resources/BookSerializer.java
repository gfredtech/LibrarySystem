package org.resources;

import org.storage.QueryParameters;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;



public class BookSerializer extends ItemSerializer<Book> {

    @Override
    public QueryParameters toQueryParameters(Book book) {
        return super.toQueryParameters(book)
                .add("book_id")
                .add("authors", book.getAuthors())
                .add("publication_date", book.getPublicationDate())
                .add("publisher", book.getPublisher())
                .add("is_bestseller", book.isBestseller());
    }

    @Override
    public Book fromResultSet(ResultSet rs) throws SQLException {
        BookFactory f = new BookFactory();
        f.setPublisher(rs.getString("publisher"));

        String[] authorsArray =
                (String[])rs.getArray("authors").getArray();
        List<String> authors = Arrays.asList(authorsArray);
        f.setAuthors(authors);

        LocalDate publicationDate =
                rs.getDate("publication_date").toLocalDate();
        f.setPublicationDate(publicationDate);

        if (rs.getBoolean("is_bestseller")) {
            f.setAsBestseller();
        }

        factory = f;

        return super.fromResultSet(rs);
    }
}
