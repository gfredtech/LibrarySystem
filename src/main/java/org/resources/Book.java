package org.resources;

import java.time.LocalDate;
import java.util.List;


/**
 * Data structure representing a book
 * The class inherits from class Item
 * @author Developed by Roberto Chavez
 * @author Reviewed by Vladimir Scherba
 */
public class Book extends Item {

    /**
     * Constructor by default of the class
     */

    Book() {}

    /**
     * Function to get the features of an item of type Book
     * @return title, authors and publisher
     */
    @Override
    public String toString() {
        return String.format("Book{title: %s, authors: %s, publisher: %s}", title, authors, publisher);
    }

    /**
     * This function returns the type of item of Book
     * @return type of item
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Feature returning function of the class
     * @return authors of the book item
     */

    public List<String> getAuthors() {
        return authors;
    }

    /**
     * Feature returning function of the class
     * @return publisher of the book item
     */

    public String getPublisher() {
        return publisher;
    }

    /**
     * Feature returning function of the class
     * @return publication date of the book item
     */
    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    /**
     * Feature returning function of the class
     * @return whether the book item is of reference type
     */
    public boolean isReference() {
        return reference;
    }

    /**
     * Feature returning function of the class
     * @return whether the book item is a best seller
     */
    public boolean isBestseller() {
        return bestseller;
    }

    /**
     * Declaration of the attributes of class Book
     */
    List<String> authors;
    String publisher;
    LocalDate publicationDate;
    boolean bestseller;
    static final String type = "book";
}