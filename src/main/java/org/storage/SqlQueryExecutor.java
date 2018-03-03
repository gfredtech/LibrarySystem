package org.storage;

import java.sql.*;
import java.util.Arrays;
import java.util.List;


class SqlQueryExecutor {

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
        }
        query.append(searchParameters.toWhereCondition());
        query.append(";");

        return statement.executeQuery(query.toString());
    }



    void insert(String tableName, QueryParameters parameters) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO ");
        query.append(tableName);
        query.append(parameters.getKeys());
        query.append(" VALUES ");
        query.append(parameters.toInsertParameters());
        query.append(";");
        statement.executeUpdate(query.toString());
    }

    void deleteOne(String tableName, QueryParameters parameters) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("DELETE FROM ");
        query.append(tableName);
        query.append(" WHERE ctid IN (SELECT ctid\n" +
                     "                FROM checkout\n" +
                     "                WHERE ");
        query.append(parameters.toWhereCondition());
        query.append(" LIMIT 1)");
        query.append(";");
        statement.executeUpdate(query.toString());
    }

    /**
     * @TODO: Explore what happens if to neglect calling this
     */
    void closeConnection() throws SQLException {
        statement.close();
        connection.close();
    }

    private Connection connection;
    private Statement statement;
}
