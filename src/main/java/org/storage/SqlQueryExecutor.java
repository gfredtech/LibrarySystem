package org.storage;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author Developed by Vladimir Scherba
 */
class SqlQueryExecutor {

    /**
     * Establishes connection to database
     * @param databaseName Name of database
     * @param userName username of database
     * @param userPassword password of database system
     * @throws SQLException Connection/Authentication error
     * @throws ClassNotFoundException Dependencies not added
     */
    SqlQueryExecutor(String databaseName, String userName, String userPassword)
            throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");

        String url = "jdbc:postgresql://localhost:5432/" + databaseName;
        connection = DriverManager.getConnection(url, userName, userPassword);
        statement = connection.createStatement();
    }


    /**
     * The returned set will be closed upon a new call to the statement or closing it
     */
    ResultSet select(String tableName, QueryParameters searchParameters) throws SQLException {
        return select(tableName, searchParameters, Arrays.asList("*"));
    }

    ResultSet select(String tableName, QueryParameters searchParameters, List<String> columns) throws SQLException {
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
            query.append(searchParameters.toWhereCondition());
        }
        query.append(";");

        System.out.println(query.toString());
        return statement.executeQuery(query.toString());
    }


    /**
     * Updates an entry based on the query parameter
     * @param tableName name of table to be updated
     * @param parameters parameters to be updated
     * @throws SQLException
     */
    void update(String tableName, QueryParameters parameters) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("UPDATE ");
        query.append(tableName);
        query.append(" SET ");
        query.append(parameters.toInsertParameters());
        query.append(";");
        System.out.println(query.toString());
        statement.executeUpdate(query.toString());
    }


    /**
     * inserts an a value into database based on parameters
     * @param tableName name of database
     * @param parameters parameters to insert data
     * @throws SQLException
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
        statement.executeUpdate(query.toString());
    }

    /**
     * Deletes an entry in table based on query parameters
     * @param tableName name of table to deleted item from
     * @param parameters QueryParameters to delete item
     * @throws SQLException
     */
    void deleteAll(String tableName, QueryParameters parameters) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("DELETE FROM ");
        query.append(tableName);
        query.append(" WHERE ");
        query.append(parameters.toWhereCondition());
        query.append(";");
        System.out.println(query.toString());
        statement.executeUpdate(query.toString());
    }

    /**
     * Closes connection to database
     */
    void closeConnection() throws SQLException {
        statement.close();
        connection.close();
    }

    private Connection connection;
    private Statement statement;
}