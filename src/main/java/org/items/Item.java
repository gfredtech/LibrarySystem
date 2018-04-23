package org.items;

import java.util.List;


/**
 * Abstract class item. It abstractly represents the features that a generic document contains
 * @author Developed by Roberto Chavez
 * @author Reviewed and improved by Vladimir Scherba
 */

public abstract class Item {

    /**
     * Getter  for the item title
     * @return title of item
     */

    public String getTitle() {
        return title;
    }

    /**
     * Get method to obtain the number of copies of an item
     * @return number of copies of an item
     */
    public int getCopiesNum() {
        return copiesNum;
    }

    /**
     * Method to find if an item is of type reference
     * @return whether the item is of type reference or not
     */
    public boolean isReference() {
        return reference;
    }

    /**
     * Method to return the keywords of an item
     * @return the keywords list that can be used to search an item
     */
    public List<String> getKeywords() {
        return keywords;
    }

    /**
     * Method to get the price of an item
     * @return price of the item
     */
    public int getPrice() {
        return price;
    }

    /**
     * Method to return the title of an item with a given format
     * @return title of an item in the specific format
     */

    @Override
    public String toString() {
        return String.format("Item{title: %s}", title);
    }

    // Declaration of attributes for an item

    String title;
    int copiesNum; // number of copies that there exist for a given item
    boolean reference; // attribute to indicate whether or not an item is of reference type
    int price; //price of item
    List<String> keywords; // List of words with which the user ca search for documents
}