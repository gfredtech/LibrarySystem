package org.items;

import java.util.List;



public abstract class ItemFactory<F extends ItemFactory<F, T>, T extends Item> {

    ItemFactory(T i) {
        item = i;
    }

    public T build() {
        assert item.keywords != null;
        assert item.title != null;
        return item;
    }

    public F title(String title) {
        item.title = title;
        return (F)this;
    }

    public F copiesNum(int copiesNum) {
        item.copiesNum = copiesNum;
        return (F)this;
    }

    public F price(int price) {
        item.price = price;
        return (F)this;
    }

    public F keywords(List<String> keywords) {
        item.keywords = keywords;
        return (F)this;
    }

    public F isReference() {
        item.reference = true;
        return (F)this;
    }

    public F isNotReference() {
        item.reference = false;
        return (F)this;
    }

    protected T item;
}