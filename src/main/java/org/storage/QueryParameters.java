package org.storage;

import javafx.util.Pair;

import java.sql.Types;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Developed by Vladimir Scherba
 */

public class QueryParameters {

    /**
     * Default constructor for Query Parameter
     * creates a HashMap object for handing parameters
     */
    public QueryParameters() {
        params = new LinkedHashMap<>();
    }

    /**
     * adds a String parameter to hashmap of query parameters
     * @param key
     * @param value
     * @return the current QueryParameters object
     */
    public QueryParameters add(String key, String value) {
        params.put(key, new Pair<>(Types.VARCHAR, value));
        return this;
    }

    /**
     * adds an Integer parameter to Hashmap of QueryParameters
     * @param key
     * @param value
     * @return
     */
    public QueryParameters add(String key, int value) {
        params.put(key, new Pair<>(Types.INTEGER, value));
        return this;
    }

    /**
     * adds a boolean parameter to Hashmap of QueryParameters
     * @param key
     * @param value
     * @return
     */
    public QueryParameters add(String key, boolean value) {
        params.put(key, new Pair<>(Types.BOOLEAN, value));
        return this;
    }

    /**
     * adds a list of objects of an arbitrary type to QueryParameters
     * @param key
     * @param value
     * @return QueryParameter object
     */
    public QueryParameters add(String key, List<?> value) {
        params.put(key, new Pair<>(Types.ARRAY, value));
        return this;
    }

    /**
     * adds a Date parameter to Query Parameters
     * @param key
     * @param value
     * @return Query Parameters
     */
    public QueryParameters add(String key, LocalDate value) {
        params.put(key, new Pair<>(Types.DATE, value));
        return this;
    }

    /**
     * adds a default string key to query parameters
     * @param key
     * @return
     */
    public QueryParameters add(String key) {
        params.put(key, new Pair<>(Types.OTHER, "DEFAULT"));
        return this;
    }

    /**
     * removes query parameter based on its key
     * @param key of query parameter to be removed
     */
    public void remove(String key) {
        params.remove(key);
    }

    /**
     * check if query parameters is empty
     * @return
     */
    public boolean isEmpty() {
        return params.isEmpty();
    }

    public String toInsertParameters() {
        StringBuilder sqlRow = new StringBuilder();

        sqlRow.append("(");

        for(Pair<Integer, ?> p: params.values()) {
            sqlRow.append(toSqlEntity(p.getKey(), p.getValue()));
            sqlRow.append(", ");
        }
        sqlRow.deleteCharAt(sqlRow.length()-2);
        sqlRow.append(")");
        return sqlRow.toString();
    }

    public String getKeys() {
        StringBuilder sqlRow = new StringBuilder();

        sqlRow.append("(");
        for(String p: params.keySet()) {
            sqlRow.append(p);
            sqlRow.append(", ");
        }
        sqlRow.deleteCharAt(sqlRow.length()-2);
        sqlRow.append(")");
        return sqlRow.toString();
    }

    /**
     * adds the SQL where condition to all
     * query parameters
     * @return string of SQL statement produced
     * from Query Parameters
     */
    public String toWhereCondition() {
        StringBuilder condition = new StringBuilder();
        for(Map.Entry<String, Pair<Integer, ?>>
                p: params.entrySet()) {
            condition.append(p.getKey());
            condition.append(" = ");
            Pair<Integer, ?> value = p.getValue();
            condition.append(toSqlEntity(value.getKey(), value.getValue()));
            condition.append(" AND ");
        }
        condition.append("TRUE");
        return condition.toString();
    }

    /**
     * converts query parameter to string to be
     * executed as an SQL statement
     * @param type
     * @param value
     * @return
     */
    private String toSqlEntity(Integer type, Object value) {
        StringBuilder s = new StringBuilder();
        switch (type) {
            case Types.VARCHAR:
                s.append("'").append(value).append("'");
                break;

            case Types.ARRAY:
                List array = (List)value;
                s.append("'{");
                for(Object e: array) {
                    s.append("\"").append(e).append("\",");
                }
                if(!array.isEmpty())
                    s.deleteCharAt(s.length()-1);
                s.append("}'");
                break;
            case Types.DATE:
                s.append("'").append(value).append("'");
                break;
            case Types.BOOLEAN:
                if((Boolean)value) {
                    s.append("TRUE");
                } else {
                    s.append("FALSE");
                }
                break;
            default:
                s.append(value);
        }
        return s.toString();
    }

    private LinkedHashMap<String, Pair<Integer, ?>> params;
}