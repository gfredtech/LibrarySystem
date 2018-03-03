package org.resources;

import org.storage.QueryParameters;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public abstract class ItemSerializer<T extends Item> {

    public QueryParameters toQueryParameters(T item) {
        return new QueryParameters()
                .add("price",item.getPrice())
                .add("title", item.getTitle())
                .add("keywords", item.getKeywords())
                .add("copy_num", item.getCopiesNum())
                .add("is_reference", item.isReference());
    }

    public T fromResultSet(ResultSet rs) throws SQLException {
        factory.setTitle(rs.getString("title"));
        factory.setCopiesNum(rs.getInt("copy_num"));

        String[] keywordsArray =
                (String[])rs.getArray("keywords").getArray();
        List<String> keywords = Arrays.asList(keywordsArray);
        factory.setKeywords(keywords);
        if (rs.getBoolean("is_reference")) {
            factory.setAsReference();
        }
        factory.setPrice(rs.getInt("price"));
        return (T)factory.build(rs.getInt(
                rs.getMetaData().getTableName(1)+"_id"));

    }

    protected ItemFactory factory;
}
