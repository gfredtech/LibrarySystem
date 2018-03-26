package org.storage;

import org.items.*;

public class EntrySerializer {

    public static QueryParameters serialize(Item item) {
        if(item instanceof Book) {
            return serialize((Book)item);
        } else if (item instanceof AvMaterial) {
            return serialize((AvMaterial)item);
        } else if (item instanceof JournalIssue) {
            return serialize((JournalIssue)item);
        } else if (item instanceof JournalArticle) {
            return serialize((JournalArticle)item);
        } else {
            throw new IllegalArgumentException("Invalid item type");
        }
    }

    public static QueryParameters serialize(User user) {
        QueryParameters p = new QueryParameters();
        p.add("login", user.getLogin());
        p.add("password_hash", user.getPasswordHash());
        p.add("name", user.getName());
        p.add("address", user.getAddress());
        p.add("phone_number", user.getPhoneNumber());
        p.add("type", user.getType());
        p.add("subtype", user.getSubtype());
        p.add("user_id", user.getCardNumber());
        return p;
    }

    private static QueryParameters serialize(Book item) {
        return serializeItem(item)
                .add("authors", item.getAuthors())
                .add("publication_date", item.getPublicationDate())
                .add("publisher", item.getPublisher())
                .add("is_bestseller", item.isBestseller());
    }

    private static QueryParameters serialize(AvMaterial item) {
        return serializeItem(item)
                .add("authors", item.getAuthors());
    }

    private static QueryParameters serialize(JournalIssue item) {
        return serializeItem(item)
                .add("editors", item.getEditors())
                .add("publisher", item.getPublisher())
                .add("publication_date", item.getPublicationDate());
    }

    private static QueryParameters serialize(JournalArticle item) {
        return new QueryParameters()
                .add("title", item.getTitle())
                .add("authors", item.getAuthors())
                .add("keywords", item.getKeywords());
    }

    private static QueryParameters serializeItem(Item item) {
        return new QueryParameters()
                .add("price",item.getPrice())
                .add("title", item.getTitle())
                .add("keywords", item.getKeywords())
                .add("copy_num", item.getCopiesNum())
                .add("is_reference", item.isReference());
    }
}