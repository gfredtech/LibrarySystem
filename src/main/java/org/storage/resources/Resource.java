package org.storage.resources;


import org.items.Item;


/**
 * A key class of the storage module. It is a map from the database tables to
 * Java objects, specifically Entry classes, and vice versa.
 * @see DatabaseEntry
 */
public class Resource<T extends DatabaseEntry> {

    private Resource(String tableName, String tableKey, Class<T> entryClass) {
        this.tableKey = tableKey;
        this.tableName = tableName;
        this.entryClass = entryClass;
    }

    public static final Resource<BookEntry> Book =
            new Resource<>("book", "book_id", BookEntry.class);
    public static final Resource<JournalIssueEntry> JournalIssue =
            new Resource<>("journal_issue", "journal_issue_id", JournalIssueEntry.class);
    public static final Resource<AvMaterialEntry> AvMaterial =
            new Resource<>("av_material", "av_material_id", AvMaterialEntry.class);
    public static final Resource<JournalArticleEntry> JournalArticle =
            new Resource<>("article", "article_id", JournalArticleEntry.class);
    public static final Resource<UserEntry> User =
            new Resource<>("user_card", "user_id", UserEntry.class);
    public static final Resource<CheckoutEntry> Checkout =
            new Resource<>("checkout", "checkout_item_id_user_id_pk", CheckoutEntry.class);
    public static final Resource<PendingRequestEntry> PendingRequest =
            new Resource<>("checkout_queue", null, PendingRequestEntry.class);
    public static final Resource<ActionLogEntry> ActionLog =
            new Resource<>("action_log", null, ActionLogEntry.class);

    public String getTableKey() {
        return tableKey;
    }

    public Class<T> entryClass() {
        return entryClass;
    }

    public static Resource fromString(String s) {
        switch (s) {
            case "book":
                return Book;
            case "journal_issue":
                return JournalIssue;
            case "av_material":
                return AvMaterial;
            case "article":
                return JournalArticle;
            case "user_card":
                return User;
            case "checkout":
                return Checkout;
            default:
                throw new IllegalArgumentException("Invalid data type: "+s);
        }
    }

    public static<T extends Item> Resource fromItem(Item item) {
        if (item instanceof org.items.Book) {
            return Book;
        } else if (item instanceof org.items.JournalArticle) {
            return JournalArticle;
        } else if (item instanceof org.items.JournalIssue) {
            return JournalIssue;
        } else if (item instanceof org.items.AvMaterial) {
            return AvMaterial;
        } else {
            throw new IllegalArgumentException("Invalid item type");
        }
    }

    public String getTableName() {
        return tableName;
    }

    private String tableName;
    private String tableKey;
    private Class<T> entryClass;
}