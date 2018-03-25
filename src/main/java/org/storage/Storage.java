package org.storage;

import org.items.*;
import org.storage.resources.Resource;
import org.storage.resources.DatabaseEntry;

import java.util.List;
import java.util.Optional;


/**
 * Represents an abstract storage of library materials and records (both user and checkout)
 */
public interface Storage {
    <T extends DatabaseEntry>
    List<T> find(Resource<T> type, QueryParameters searchParameters);

    <T extends DatabaseEntry>
    Optional<T> get(Resource<T> type, int id);

    int getNumOfEntries(Resource resource, QueryParameters params);

    void add(Resource type, QueryParameters data);

    void remove(Resource type, int id);

    void update(Resource type, int id, QueryParameters params);

    class QueryExecutionError extends RuntimeException {
        QueryExecutionError(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
