package org.resources;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;


/**
 * A factory intended to create instances of the Book class
 */
public class BookFactory extends ItemFactory<Book> {

    public BookFactory() {
        super(new Book());
    }

    public void setAuthors(List<String> authors) {
        item.authors = authors;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        item.publicationDate = publicationDate;
    }

    public void setPublisher(String publisher) {
        item.publisher = publisher;
    }

    public void setAsBestseller() {
        item.bestseller = true;
    }

    public void setAsNonBestseller() {
        item.bestseller = false;
    }
}

