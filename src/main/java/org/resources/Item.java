package org.resources;

import java.util.List;

public abstract class Item {

    public String getTitle() {
        return title;
    }
    public int getCopiesNum() {
        return copiesNum;
    }
    public boolean isReference() {
        return reference;
    }
    public List<String> getKeywords() {
        return keywords;
    }
    public int getPrice() {
        return price;
    }
    public abstract String getType();

    @Override
    public String toString() {
        return String.format("Item{title: %s}", title);
    }


    String title;
    int copiesNum;
    boolean reference;
    int price;
    List<String> keywords;

}
