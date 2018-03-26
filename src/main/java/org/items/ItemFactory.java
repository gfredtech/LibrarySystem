package org.items;

import java.util.List;



public abstract class ItemFactory<T extends Item> {

    protected ItemFactory(T i) {
        item = i;
    }

    public T build() {
        assert item.keywords != null;
        assert item.title != null;
        return item;
    }

    public ItemFactory<T> title(String title) {
        item.title = title;
        return this;
    }

    public ItemFactory<T> copiesNum(int copiesNum) {
        item.copiesNum = copiesNum;
        return this;
    }

    public ItemFactory<T> price(int price) {
        item.price = price;
        return this;
    }

    public ItemFactory<T> keywords(List<String> keywords) {
        item.keywords = keywords;
        return this;
    }

    public ItemFactory<T> isReference() {
        item.reference = true;
        return this;
    }

    public ItemFactory<T> isNotReference() {
        item.reference = false;
        return this;
    }

    protected T item;
}