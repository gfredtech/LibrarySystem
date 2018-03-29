package org.storage.resources;

import org.storage.QueryParameters;
import org.storage.SqlStorage;
import org.storage.Storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class PendingRequestEntry extends DatabaseEntry {

    public PendingRequestEntry(ResultSet rs) throws SQLException {
        super(rs);
        Storage storage = SqlStorage.getInstance();

        int userId = rs.getInt("user_id");
        user = storage.get(Resource.User, userId).get();
        Resource<ItemEntry> itemType =
                Resource.fromString(rs.getString("item_type"));
        int itemId = rs.getInt("item_id");
        item = storage.get(itemType, itemId).get();

        requestDate = rs.getDate("request_date").toLocalDate();
    }

    @Override
    public Resource<PendingRequestEntry> getResourceType() {
        return Resource.PendingRequest;
    }

    @Override
    public QueryParameters toQueryParameters() {
        return new QueryParameters()
                .add("user_id", user.getId())
                .add("item_id", item.getId())
                .add("request_date", requestDate);
    }

    public ItemEntry getItem() {
        return item;
    }

    public UserEntry getUser() {
        return user;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    private UserEntry user;
    private ItemEntry item;
    private LocalDate requestDate;
}
