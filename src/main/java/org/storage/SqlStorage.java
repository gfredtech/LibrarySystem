package org.storage;


import org.storage.resources.DatabaseEntry;
import org.storage.resources.Resource;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


/**
 * Interacts with the database described in the Resource enumeration.
 * Able to search for, add, remove, and update records
 * and also perform some simple operations like counting records that fit some parameters.
 */
public class SqlStorage extends SqlQueryExecutor implements Storage {

    public static SqlStorage getInstance() {
        if(instance == null)
            throw new RuntimeException("SQL Storage has not been initialized");
        return instance;
    }

    public static void connect(String databaseName, String userName, String userPassword) {
        try {
            instance = new SqlStorage(databaseName, userName, userPassword);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Calls the constructor of the superclass
     * @see SqlQueryExecutor#SqlQueryExecutor(String, String, String)
     */
    SqlStorage(String databaseName, String userName, String userPassword)
            throws SQLException {
        super(databaseName, userName, userPassword);
    }



    @Override
    public <T extends DatabaseEntry>
    List<T> find(Resource<T> type, QueryParameters searchParameters) {
        List<T> result = new LinkedList<>();
        try (Query q = select(type.getTableName(), searchParameters)){
            ResultSet rs = q.getResult();
            while(rs.next()) {
                T t = initEntry(type.entryClass(), rs);
                result.add(t);
            }
        } catch (SQLException e) {
            final String errorMessage = "Error performing search in the table '"+type.getTableName()
                    + "' with parameters: "+ searchParameters;
            throw new QueryExecutionError(errorMessage, e);
        }

        return result;
    }

    @Override
    public <T extends DatabaseEntry>
    Optional<T> get(Resource<T> type, int id) {
        QueryParameters params = new QueryParameters()
                .add(type.getTableKey(), id);
        List<T> users = find(type, params);
        if(!users.isEmpty()) {
            return Optional.of(users.get(0));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public int getNumOfEntries(Resource resource, QueryParameters params) {
        try (Query q = select(resource.getTableName(), params,
                Collections.singletonList("count(*)"))){
            ResultSet rs = q.getResult();
            rs.next();
            return rs.getInt(1);

        } catch (SQLException e) {
            final String errorMessage = "Error during retrieving the number of entries in "+resource.getTableName();
            throw new QueryExecutionError(errorMessage, e);
        }

    }



    @Override
    public void add(Resource type, QueryParameters parameters) {
        try {

            insert(type.getTableName(), parameters);

        } catch (SQLException e) {
            final String errorMessage = "Error during insertion to the table '"+type.getTableName()
                    + "' with parameters: "+ parameters;
            throw new QueryExecutionError(errorMessage, e);
        }
    }



    @Override
    public void removeAll(Resource type, QueryParameters parameters ) {
        try {
            deleteAll(type.getTableName(), parameters);
        } catch (SQLException e) {
            final String errorMessage = "Error during removal from the table '"+type.getTableName()
                    + "' with parameters: " + parameters;
            throw new QueryExecutionError(errorMessage, e);
        }
    }


    @Override
    public void updateAll(Resource type, QueryParameters searchParams, QueryParameters params) {
        try {
            update(type.getTableName(),
                    params,
                    searchParams);
        } catch (SQLException e) {
            final String errorMessage = "Error during update in the table '"+type.getTableName()
                    + "' with parameters: " + params;
            throw new QueryExecutionError(errorMessage, e);
        }
    }


    private <T extends DatabaseEntry>
    T initEntry(Class<T> type, ResultSet rs) {
        try {
            return type.getConstructor(ResultSet.class).newInstance(rs);
        } catch (NoSuchMethodException|IllegalAccessException
                |InvocationTargetException|InstantiationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void closeConnection() throws SQLException {
        super.closeConnection();
    }

    private static SqlStorage instance;
}