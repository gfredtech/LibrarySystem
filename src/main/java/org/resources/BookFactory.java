package org.resources;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;


/**
 * A factory intended to create instances of the Book class
 */
public class BookFactory {

    public Book build() {
        Book book = new Book();
        book.authors = authors;
        book.copiesNum = copiesNum;
        book.keywords = keywords;
        book.price = price;
        book.publicationYear = publicationYear;
        book.reference = reference;
        book.bestseller = bestseller;
        book.publisher = publisher;
        book.title = title;
        return book;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public void setCopiesNum(int copiesNum) {
        this.copiesNum = copiesNum;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public void setPublicationYear(LocalDate publicationYear) {
        this.publicationYear = publicationYear;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setAsReference() {
        this.reference = true;
    }

    public void setAsNonReference() {
        this.reference = false;
    }

    public void setAsBestseller() {
        this.bestseller = true;
    }

    public void setAsNonBestseller() {
        this.bestseller = false;
    }

    private String title;
    private List<String> authors;
    private String publisher;
    private LocalDate publicationYear;
    private int copiesNum = 1;
    private boolean reference = false;
    private boolean bestseller = false;
    private int price;
    private List<String> keywords = new LinkedList<>();
}

