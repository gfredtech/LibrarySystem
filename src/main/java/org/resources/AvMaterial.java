package org.resources;

import java.util.List;

/**
 * Data structure representing a media material
 */
public class AvMaterial extends Item {

    AvMaterial() {}

    public List<String> getAuthors() {
        return authors;
    }

    @Override
    public String getType() {
        return type;
    }

    List<String> authors;

    static final String type = "journal_issue";

}
