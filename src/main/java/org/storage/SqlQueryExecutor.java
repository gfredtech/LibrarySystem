package org.storage;

import java.sql.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Designed to handle low-level interactions with a database.
 */
class SqlQueryExecutor {

    /**
     * Establishes connection to database
     * @param databaseName Name of database
     * @param userName username of database
     * @param userPassword password of database system
     * @throws SQLException Connection/Authentication error
     */
    SqlQueryExecutor(String databaseName, String userName, String userPassword)
            throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/" + databaseName;
        connection = DriverManager.getConnection(url, userName, userPassword);
    }


    /**
     * The returned set will be closed upon a new call to the statement or closing it
     */
    Query select(String tableName, QueryParameters searchParameters) throws SQLException {
        return select(tableName, searchParameters, Collections.singletonList("*"), false);
    }


    Query select(String tableName, QueryParameters searchParameters, boolean withRegex) throws SQLException {
        return select(tableName, searchParameters, Collections.singletonList("*"), withRegex);
    }

    Query select(String tableName, QueryParameters searchParameters, List<String> columns, boolean withRegex) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        for(String col: columns) {
            query.append(col);
            query.append(",");
        }
        query.deleteCharAt(query.length()-1);
        query.append(" FROM ");
        query.append(tableName);
        if(!searchParameters.isEmpty()) {
            query.append(" WHERE ");
            query.append(searchParameters.toWhereCondition(withRegex));
        }
        query.append(";");

        System.out.println(query.toString());
        Statement s = connection.createStatement();
        try {
            s.execute(query.toString());
            return new Query(s);

        } catch (SQLException e) {
            s.close();
            throw e;
        }
    }


    /**
     * Updates an entry based on the query parameter
     * @param tableName name of table to be updated
     * @param updateParameters features to be updated
     * @param whatToUpdate distinctive features of items to be updated
     * @throws SQLException in case of an exception from JDBC
     */
    void update(String tableName, QueryParameters updateParameters, QueryParameters whatToUpdate) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("UPDATE ");
        query.append(tableName);
        query.append(" SET ");
        query.append(updateParameters.toUpdateParameters());
        query.append(" WHERE ");
        query.append(whatToUpdate.toWhereCondition(false));
        query.append(";");
        System.out.println(query.toString());
        try(Statement s = connection.createStatement()) {
            s.executeUpdate(query.toString());
        }
    }


    /**
     * inserts an a value into database based on parameters
     * @param tableName name of database
     * @param parameters parameters to insert data
     * @throws SQLException in case of an exception from JDBC
     */
    void insert(String tableName, QueryParameters parameters) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO ");
        query.append(tableName);
        query.append(parameters.getKeys());
        query.append(" VALUES ");
        query.append(parameters.toInsertParameters());
        query.append(";");
        System.out.println(query.toString());
        try(Statement s = connection.createStatement()) {
            s.executeUpdate(query.toString());

        }
    }

    /**
     * Deletes an entry in table based on query parameters
     * @param tableName name of table to deleted item from
     * @param parameters QueryParameters to delete item
     * @throws SQLException in case of an exception from JDBC
     */
    void deleteAll(String tableName, QueryParameters parameters) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("DELETE FROM ");
        query.append(tableName);
        query.append(" WHERE ");
        query.append(parameters.toWhereCondition(false));
        query.append(";");
        System.out.println(query.toString());
        try(Statement s = connection.createStatement()) {
            s.executeUpdate(query.toString());

        }
    }

    List<String> getPrimaryKeys(String tableName) throws SQLException {
        DatabaseMetaData dm = connection.getMetaData();
        ResultSet rs = dm.getPrimaryKeys(null, null, tableName);
        List<String> data = new LinkedList<>();
        while(rs.next()) {
            data.add(rs.getString("COLUMN_NAME"));
        }
        return Collections.emptyList();
    }

    List<String> getForeignKeys(String tableName) throws SQLException {
        DatabaseMetaData dm = connection.getMetaData();
        ResultSet rs = dm.getImportedKeys(null, null, tableName);
        while(rs.next()) {
            List<String> data = new LinkedList<>();
            for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                data.add(rs.getString(i));
            }
        }
        return Collections.emptyList();
    }

    /**
     * Closes connection to database
     */
    void closeConnection() throws SQLException {
        connection.close();
    }

    class Query implements AutoCloseable {
        private Query(Statement statement) {
            this.statement = statement;
        }

        ResultSet getResult() throws SQLException {
            return statement.getResultSet();
        }

        @Override
        public void close() throws SQLException {
            statement.close();
        }
        private Statement statement;
    }



    private Connection connection;
}