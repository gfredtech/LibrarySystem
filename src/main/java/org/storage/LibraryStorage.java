package org.storage;

import org.storage.resources.CheckoutEntry;
import java.sql.SQLException;
import java.time.LocalDate;


public class LibraryStorage extends SqlStorage {

    /**
     * @param c contains data of the overdue check out
     * @return the fee, which is 100 rubles per day. Zero if not overdue
     */
    public int caluclateFee(CheckoutEntry c) {
        final int feePerDay = 100;
        if(LocalDate.now().isAfter(c.getDueDate()))
            return c.getDueDate().until(LocalDate.now()).getDays()*feePerDay;
        else
            return 0;
    }

    public static LibraryStorage getInstance() {
        if(instance == null)
            throw new RuntimeException("SQL Storage has not been initialized");
        return instance;
    }

    public static void connect(String databaseName, String userName, String userPassword) {
        try {
            SqlStorage.connect(databaseName, userName, userPassword);
            instance = new LibraryStorage(databaseName, userName, userPassword);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected LibraryStorage(String databaseName, String userName, String userPassword) throws  SQLException {
        super(databaseName, userName, userPassword);
    }

    private static LibraryStorage instance;
}
