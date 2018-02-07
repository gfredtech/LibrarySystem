package org.storage;

import org.resources.Book;
import org.resources.CheckoutInfo;
import org.resources.User;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;


/**
 * This class encapsulates the communication between the database and the system
 */
public class Storage {

    /**
     * Checks if JDBS driver is found and memorizes database and user data needed to open a connection
     * @param databaseName
     * @param userName
     * @param userPassword
     */
    public Storage(String databaseName, String userName, String userPassword) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.databaseName = databaseName;
        this.userName = userName;
        this.userPassword = userPassword;

        // temporary solution
        this.books = new LinkedList<>();
        this.users = new LinkedList<>();
        this.checkouts = new LinkedList<>();
    }


    /**
     * TODO This method is being developed and is not ready yet
     */
    public String searchForItem(String itemType, List<String> searchParameters) {
        assert false;
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

    // temporary solution!
    public void saveCheckouts() {
        try(FileWriter w = new FileWriter("checkouts")) {
            for(CheckoutInfo c: checkouts) {
                w.write(c.item.getTitle());
                w.write("\n");
                w.write(String.valueOf(c.patron.getCardNumber()));
                w.write("\n");
                w.write(c.overdue.toString());
                w.write("\n\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // temporary solution!
    public List<Book> books;
    public List<User> users;
    public List<CheckoutInfo> checkouts;

    private String databaseName, userName, userPassword;
}
