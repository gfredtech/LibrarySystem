package org.storage;

import org.resources.Book;
import org.resources.CheckoutInfo;
import org.resources.User;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;



public class Storage {

    public Storage(String databaseName, String userName, String userPassword) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.databaseName = databaseName;
        this.userName = userName;
        this.userPassword = userPassword;

        this.books = new LinkedList<>();
        this.users = new LinkedList<>();
        this.checkouts = new LinkedList<>();
    }



    public String searchForItem(String itemType, List<String> searchParameters) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM ");
        query.append(itemType);
        query.append(";");

        StringBuilder result = new StringBuilder();
        try(Connection connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/"+databaseName,
                        userName, userPassword)) {
            Statement stmt = connection.createStatement();
            ResultSet s = stmt.executeQuery(query.toString());
            for(int i = 1; i <= s.getMetaData().getColumnCount(); i++) {
                result.append(s.getString(1));
                result.append("\n");
            }
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public List<Book> books;
    public List<User> users;
    public List<CheckoutInfo> checkouts;

    private String databaseName, userName, userPassword;
}
