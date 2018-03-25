package org.storage.resources;

import org.items.Item;
import org.items.ItemFactory;
import org.storage.QueryParameters;
import org.storage.EntrySerializer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public abstract class ItemEntry<T extends Item> extends DatabaseEntry {

    ItemEntry(ResultSet rs) throws SQLException {
        super(rs);
        ItemFactory<T> factory = initFactory(rs);
        factory.title(rs.getString("title"));
        factory.copiesNum(rs.getInt("copy_num"));

        String[] keywordsArray =
                (String[])rs.getArray("keywords").getArray();
        List<String> keywords = Arrays.asList(keywordsArray);
        factory.keywords(keywords);
        if (rs.getBoolean("is_reference")) {
            factory.isReference();
        }
        factory.price(rs.getInt("price"));

        item = factory.build();
        id = rs.getInt(getResourceType().getTableKey());
    }

    @Override
    public QueryParameters toQueryParameters() {
        return EntrySerializer.serialize(item);
    }

    public T getItem() {
        return item;
    }

    public int getId() {
        return id;
    }

    abstract protected ItemFactory<T> initFactory(ResultSet rs) throws SQLException;

    protected T item;
    private int id;
}
