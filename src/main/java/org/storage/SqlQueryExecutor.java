package org.storage;

import javafx.util.Pair;

import javax.print.DocFlavor;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;



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
    ResultSet select(String tableName, Map<String, String> searchParameters) throws SQLException {
        return select(tableName, searchParameters, Arrays.asList("*"));
    }

    ResultSet select(String tableName, Map<String, String> searchParameters, List<String> columns) throws SQLException {
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
        for(Map.Entry p: searchParameters.entrySet()) {
            query.append(p.getKey());
            query.append(" = '");
            query.append(p.getValue());
            query.append("' AND ");
        }
        query.append("TRUE");
        query.append(";");

        System.out.println(query.toString());
        return statement.executeQuery(query.toString());
    }



    void insert(String tableName, List<Pair<Integer, ?>> parameters) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO ");
        query.append(tableName);
        query.append(" VALUES (");
        for(Pair<Integer, ?> p: parameters) {
            switch (p.getKey()) {
                case Types.VARCHAR:
                    query.append("'");
                    query.append(p.getValue());
                    query.append("'");
                    break;

                case Types.ARRAY:
                    List<String> array = (List<String>)p.getValue();
                    query.append("'{");
                    for(String e: array) {
                        query.append("\"");
                        query.append(e);
                        query.append("\",");
                    }
                    if(!array.isEmpty())
                        query.deleteCharAt(query.length()-1);
                    query.append("}'");
                    break;
                case Types.DATE:
                    query.append("'");
                    query.append(p.getValue());
                    query.append("'");
                    break;
                case Types.BOOLEAN:
                    if((Boolean)p.getValue()) {
                        query.append("TRUE");
                    } else {
                        query.append("FALSE");
                    }
                    break;
                default:
                    query.append(p.getValue());
            }
            query.append(", ");
        }
        query.deleteCharAt(query.length()-2);
        query.append(");");
        statement.executeUpdate(query.toString());
    }

    void deleteOne(String tableName, Map<String, ?> parameters) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("DELETE FROM ");
        query.append(tableName);
        query.append(" WHERE ctid IN (SELECT ctid\n" +
                     "                FROM checkout\n" +
                     "                WHERE ");
        for(Map.Entry p: parameters.entrySet()) {
            query.append(p.getKey());
            query.append(" = '");
            query.append(p.getValue());
            query.append("' AND ");
        }
        query.append("TRUE LIMIT 1)");
        query.append(";");
        System.out.println(query.toString());
        statement.executeUpdate(query.toString());
    }

    /**
     * @TODO: Explore what happenes if to neglect calling this
     */
    void closeConnection() throws SQLException {
        statement.close();
        connection.close();
    }

    private Connection connection;
    private Statement statement;
}
