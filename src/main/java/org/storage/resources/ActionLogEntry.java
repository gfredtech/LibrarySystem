package org.storage.resources;

import org.storage.QueryParameters;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class ActionLogEntry extends DatabaseEntry {

    public ActionLogEntry(ResultSet rs) throws SQLException {
        super(rs);
        userId = rs.getInt("user_id");
        actionType = rs.getString("action_type");
        actionParameters = (String[])rs.getArray("action_parameters").getArray();
    }

    @Override
    public Resource<ActionLogEntry> getResourceType() {
        return Resource.ActionLog;
    }

    @Override
    public QueryParameters toQueryParameters() {
        return new QueryParameters()
                .add("user_id", userId)
                .add("action_type", actionType)
                .add("action_parameters", Arrays.asList(actionParameters));
    }

    private int userId;

    public int getUserId() {
        return userId;
    }

    public String getActionType() {
        return actionType;
    }

    public String[] getActionParameters() {
        return actionParameters;
    }

    private String actionType;
    private String[] actionParameters;
}
