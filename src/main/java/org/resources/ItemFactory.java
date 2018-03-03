package org.resources;

import java.util.LinkedList;
import java.util.List;



public abstract class ItemFactory<T extends Item> {

    protected ItemFactory(T i) {
        item = i;
    }

    public T build(int id) {
        assert item.keywords != null;
        assert item.title != null;
        item.id = id;
        return item;
    }

    @Deprecated
    public T getCarcass() {
        return item;
    }

    public void setTitle(String title) {
        item.title = title;
    }

    public void setCopiesNum(int copiesNum) {
        item.copiesNum = copiesNum;
    }

    public void setPrice(int price) {
        item.price = price;
    }

    public void setKeywords(List<String> keywords) {
        item.keywords = keywords;
    }

    public void setAsReference() {
        item.reference = true;
    }

    public void setAsNonReference() {
        item.reference = false;
    }

    protected T item;
}
