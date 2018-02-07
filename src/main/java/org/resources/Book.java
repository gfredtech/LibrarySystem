package org.resources;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;



public class Book {

    Book() {}

    public List<String> getAuthors() {
        return authors;
    }

    public String getTitle() {
        return title;
    }

    public String getPublisher() {
        return publisher;
    }

    public LocalDate getPublicationYear() {
        return publicationYear;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public int getCopiesNum() {
        return copiesNum;
    }

    public int getPrice() {
        return price;
    }

    public boolean isReference() {
        return reference;
    }

    public boolean isBestseller() {
        return bestseller;
    }

    String title;
    List<String> authors;
    String publisher;
    LocalDate publicationYear;
    int copiesNum;
    boolean reference;
    boolean bestseller;
    int price;
    List<String> keywords;
}
