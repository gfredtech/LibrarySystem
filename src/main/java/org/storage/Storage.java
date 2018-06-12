package org.storage;

import org.storage.resources.DatabaseEntry;
import org.storage.resources.Resource;

import java.util.List;
import java.util.Optional;


/**
 * Represents an interface to an abstract data storage.
 * Allows searching, modification, craetion and removal of records.
 */
public interface Storage {
    <T extends DatabaseEntry>
    List<T> find(Resource<T> type, QueryParameters searchParameters);

    <T extends DatabaseEntry>
    Optional<T> get(Resource<T> type, int id);

    int getNumOfEntries(Resource resource, QueryParameters searchParameters);

    void add(Resource type, QueryParameters data);

    void removeAll(Resource type, QueryParameters searchParameters);

    void updateAll(Resource type, QueryParameters searchParameters, QueryParameters params);

    class QueryExecutionError extends RuntimeException {
        QueryExecutionError(String message, Throwable cause) {
            super(message, cause);
        }
    }
}