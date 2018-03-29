package org.storage;

import javafx.util.Pair;

import java.sql.Types;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Vladimir Shcherba
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
     * @return this object
     */
    public QueryParameters add(String key, String value) {
        if(value == null)
            params.put(key, new Pair<>(Types.NULL, null));
        else
            params.put(key, new Pair<>(Types.VARCHAR, value));
        return this;
    }

    /**
     * adds an Integer parameter
     * @return this object
     */
    public QueryParameters add(String key, Integer value) {
        if(value == null)
            params.put(key, new Pair<>(Types.NULL, null));
        else
            params.put(key, new Pair<>(Types.INTEGER, value));
        return this;
    }

    /**
     * adds a boolean parameter
     * @return this object
     */
    public QueryParameters add(String key, Boolean value) {
        if(value == null)
            params.put(key, new Pair<>(Types.NULL, null));
        else
            params.put(key, new Pair<>(Types.BOOLEAN, value));
        return this;
    }

    /**
     * adds a list parameter
     * @return this object
     */
    public QueryParameters add(String key, List<?> value) {
        if(value == null)
            params.put(key, new Pair<>(Types.NULL, null));
        else
            params.put(key, new Pair<>(Types.ARRAY, value));
        return this;
    }

    /**
     * adds a Date parameter
     * @param key
     * @param value
     * @return this object
     */
    public QueryParameters add(String key, LocalDate value) {
        if(value == null)
            params.put(key, new Pair<>(Types.NULL, null));
        else
            params.put(key, new Pair<>(Types.DATE, value));
        return this;
    }

    /**
     * adds a parameter with default value
     * @param key
     * @return this object
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
     * @return if the list of query parameters is empty
     */
    public boolean isEmpty() {
        return params.isEmpty();
    }

    /**
     * @return a string representing the list of keys, surrounded by brackets and separated by commas
     */
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
     * @return parameters in the format required by WHERE SQL clause
     */
    public String toWhereCondition() {
        StringBuilder condition = new StringBuilder();
        for(Map.Entry<String, Pair<Integer, ?>>
                p: params.entrySet()) {
            condition.append("\"");
            condition.append(p.getKey());
            condition.append("\"");
            Pair<Integer, ?> value = p.getValue();
            int type = value.getKey();
            if(type == Types.NULL) {
                condition.append(" IS ");
            } else {
                condition.append(" = ");
            }
            condition.append(toSqlEntity(type, value.getValue()));
            condition.append(" AND ");
        }
        condition.append("TRUE");
        return condition.toString();
    }

    /**
     * @return parameters in the format required for INSERT parameters
     */
    public String toInsertParameters() {
        StringBuilder params = new StringBuilder();
        params.append("(");
        for(Map.Entry<String, Pair<Integer, ?>>
                p: this.params.entrySet()) {
            Pair<Integer, ?> value = p.getValue();
            params.append(toSqlEntity(value.getKey(), value.getValue()));
            params.append(",");
        }
        params.deleteCharAt(params.length()-1);
        params.append(")");
        return params.toString();
    }

    /**
     * @return parameters in the format required for INSERT parameters
     */
    public String toUpdateParameters() {
        StringBuilder params = new StringBuilder();
        for(Map.Entry<String, Pair<Integer, ?>>
                p: this.params.entrySet()) {
            params.append("\"");
            params.append(p.getKey());
            params.append("\"");
            params.append(" = ");
            Pair<Integer, ?> value = p.getValue();
            params.append(toSqlEntity(value.getKey(), value.getValue()));
            params.append(",");
        }
        params.deleteCharAt(params.length()-1);
        return params.toString();
    }

    @Override
    public String toString() {
        // lists all keys and values
        return toUpdateParameters();
    }

    /**
     * @return converts an object to a string, which is a valid SQL entity
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
            case Types.NULL:
                s.append("NULL");
                break;

            default:
                s.append(value);
        }
        return s.toString();
    }

    private LinkedHashMap<String, Pair<Integer, ?>> params;
}