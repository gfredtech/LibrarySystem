package org.items;

import java.time.LocalDate;
import java.util.List;


/**
 * A factory intended to create instances of the Book class
 * The factory implemented as a Builder class extends from class ItemFactory
 * @author Developed by Roberto Chavez
 * @author Reviewed and improved by Vladimir Scherba
 */
public class BookFactory extends ItemFactory<Book> {

    /**
     * Constructor of the class
     */
    public BookFactory() {
        super(new Book());
    }

    /**
     * Setting method to set the authors of book item
     * @param authors
     */
    public BookFactory authors(List<String> authors) {
        item.authors = authors;
        return this;
    }

    /**
     * Setting method to set the publication date of book item
     * @param publicationDate
     */
    public BookFactory publicationDate(LocalDate publicationDate) {
        item.publicationDate = publicationDate;
        return this;
    }

    /**
     * Setting method to set the publisher of book item
     * @param publisher
     */
    public BookFactory publisher(String publisher) {
        item.publisher = publisher;
        return this;
    }

    /**
     * Setting method to establish whether a book item is a best seller
     */
    public BookFactory isBestseller() {
        item.bestseller = true;
        return this;
    }

    /**
     * Setting method to establish whether a book item is not a best seller
     */
    public BookFactory isNotBestseller() {
        item.bestseller = false;
        return this;
    }
}