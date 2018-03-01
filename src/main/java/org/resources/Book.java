package org.resources;

import java.time.LocalDate;
import java.util.List;


/**
 * Data structure representing a book
 */
public class Book extends Item {

    Book() {}

    @Override
    public String toString() {
        return String.format("Book{title: %s, authors: %s, publisher: %s}", title, authors, publisher);
    }

    @Override
    public String getType() {
        return type;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public boolean isReference() {
        return reference;
    }

    public boolean isBestseller() {
        return bestseller;
    }

    List<String> authors;
    String publisher;
    LocalDate publicationDate;
    boolean bestseller;
    static final String type = "book";
}
