package org.storage.resources;

import org.items.User;
import org.storage.QueryParameters;
import org.storage.ItemSerializer;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserEntry extends DatabaseEntry {

    public UserEntry(ResultSet rs) throws SQLException {
        super(rs);
        String name = rs.getString("name");
        String type = rs.getString("type");
        String subType = rs.getString("subtype");
        String login = rs.getString("login");
        String phoneNumber = rs.getString("phone_number");
        String address = rs.getString("address");
        int passwordHash = rs.getInt("password_hash");
        user = new User(rs.getInt("user_id"), name, type, subType);
        user.setLogin(login);
        user.setAddress(address);
        user.setPhoneNumber(phoneNumber);
        user.setPasswordHash(passwordHash);
    }

    @Override
    public Resource getResourceType() {
        return Resource.User;
    }

    @Override
    public QueryParameters toQueryParameters() {
        return ItemSerializer.serialize(user);
    }

    public int getId() {
        return user.getCardNumber();
    }

    public User getUser() {
        return user;
    }

    User user;
}