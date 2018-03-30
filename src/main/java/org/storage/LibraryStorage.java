package org.storage;

import java.sql.SQLException;

public class LibraryStorage extends SqlStorage {


    protected LibraryStorage(String databaseName, String userName, String userPassword) throws  SQLException {
        super(databaseName, userName, userPassword);
    }

}
