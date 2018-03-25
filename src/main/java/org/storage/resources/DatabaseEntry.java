package org.storage.resources;


import org.storage.QueryParameters;

import java.sql.ResultSet;
import java.sql.SQLException;



public abstract class DatabaseEntry {

    DatabaseEntry(ResultSet rs) throws SQLException {
        id = rs.getInt(getResourceType().getTableKey());
    }

    public abstract QueryParameters toQueryParameters();

    public abstract Resource getResourceType();

    public int getId() {
        return id;
    }

    private int id;
}
