package org.resources;

import java.time.LocalDate;
import java.util.LinkedList;
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
    public void setAuthors(List<String> authors) {
        item.authors = authors;
    }

    /**
     * Setting method to set the publication date of book item
     * @param publicationDate
     */
    public void setPublicationDate(LocalDate publicationDate) {
        item.publicationDate = publicationDate;
    }

    /**
     * Setting method to set the publisher of book item
     * @param publisher
     */
    public void setPublisher(String publisher) {
        item.publisher = publisher;
    }

    /**
     * Setting method to establish whether a book item is a best seller
     */
    public void setAsBestseller() {
        item.bestseller = true;
    }

    /**
     * Setting method to establish whether a book item is not a best seller
     */
    public void setAsNonBestseller() {
        item.bestseller = false;
    }
}
