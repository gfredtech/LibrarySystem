package org.storage;

import javafx.util.Pair;

import java.sql.Types;
import java.util.LinkedList;
import java.util.List;



public class SearchParameters {

    public SearchParameters() {
        params = new LinkedList<>();
    }

    public SearchParameters addVarchar(String key, String value) {
        params.add(new Pair<>(Types.VARCHAR, value));
        return this;
    }

    public String toSQL() {
        StringBuilder builder = new StringBuilder();
        for(Pair<Integer, ?> entry: params) {
            switch(entry.getKey()) {
                case Types.VARCHAR:
                    builder.append("'");
                    builder.append(entry.getValue());
                    builder.append("'");
            }
        }
        return builder.toString();
    }

    private List<Pair<Integer, ?>> params;
}
