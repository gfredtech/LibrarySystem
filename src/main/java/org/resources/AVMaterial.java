package org.resources;

import java.util.List;

/**
 * Data structure representing a media material
 */
public class AVMaterial {

    public String getTitle() {
        return title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public int getPrice() {
        return price;
    }

    public int getCopiesNum() {
        return copiesNum;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    private String title;
    private List<String> authors;
    private int copiesNum;
    private int price;
    private List<String> keywords;
}
