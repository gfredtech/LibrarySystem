package org.storage.resources;


import org.storage.QueryParameters;

import java.sql.ResultSet;


/**
 * A representation of a database record, associated with some Resource instance.
 * May be initialized only from a result set,
 * meaning that it is a representation of an actual record in a database
 * @see Resource
 * @see ResultSet
 */
public abstract class DatabaseEntry {

    DatabaseEntry(ResultSet rs) {
    }

    public abstract QueryParameters toQueryParameters();

    public abstract Resource getResourceType();
}