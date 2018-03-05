package org.storage;

import javafx.util.Pair;

import java.sql.Types;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class QueryParameters {

    public QueryParameters() {
        params = new LinkedHashMap<>();
    }

    public QueryParameters add(String key, String value) {
        params.put(key, new Pair<>(Types.VARCHAR, value));
        return this;
    }

    public QueryParameters add(String key, int value) {
        params.put(key, new Pair<>(Types.INTEGER, value));
        return this;
    }

    public QueryParameters add(String key, boolean value) {
        params.put(key, new Pair<>(Types.BOOLEAN, value));
        return this;
    }

    public QueryParameters add(String key, List<?> value) {
        params.put(key, new Pair<>(Types.ARRAY, value));
        return this;
    }

    public QueryParameters add(String key, LocalDate value) {
        params.put(key, new Pair<>(Types.DATE, value));
        return this;
    }

    public QueryParameters add(String key) {
        params.put(key, new Pair<>(Types.OTHER, "DEFAULT"));
        return this;
    }

    public void remove(String key) {
        params.remove(key);
    }

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
