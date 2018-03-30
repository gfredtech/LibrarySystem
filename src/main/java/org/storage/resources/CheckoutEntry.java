package org.storage.resources;

import org.storage.QueryParameters;
import org.storage.SqlStorage;
import org.storage.Storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.storage.resources.Resource.User;

public class CheckoutEntry extends DatabaseEntry {

    public CheckoutEntry(ResultSet rs) throws SQLException {
        super(rs);
        Storage storage = SqlStorage.getInstance();
        int userId = rs.getInt("user_id");
        int itemId = rs.getInt("item_id");
        String itemType = rs.getString("item_type");
        dueDate = rs.getDate("due_date").toLocalDate();

        patron = storage.get(User, userId).get();
        Resource<ItemEntry> resourceType =
                Resource.fromString(itemType);
        item = storage.get(resourceType, itemId).get();

        isRenewed = rs.getBoolean("is_renewed");
    }

    public QueryParameters toQueryParameters() {
        return new QueryParameters()
                .add("item_id", item.getId())
                .add("user_id", patron.getId())
                .add("due_date", dueDate)
                .add("is_renewed", isRenewed);
    }

    public Resource getResourceType() {
        return Resource.Checkout;
    }

    public UserEntry getPatron() {
        return patron;
    }

    public ItemEntry getItem() {
        return item;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public boolean isRenewed() {
        return isRenewed;
    }

    UserEntry patron;
    ItemEntry item;
    LocalDate dueDate;
    boolean isRenewed;
}