package org.resources;

import java.util.List;

/**
 * Data structure representing a media material
 * @author Developed by Roberto Chavez
 * @author Reviewed and improved by Vladimir Scherba
 */

public class AvMaterial extends Item {

    /**
     * This function returns the list of Authors of the AvMaterial
     * @return List<String>
     */
    public List<String> getAuthors() {
        return authors;
    }

    /**
     * The type of AvMaterial that was lent to the user
     * @return String
     */

    @Override
    public String getType() {
        return type;
    }

    /**
     * List of Authors of the AvMaterial
     * @variable List<String>
     */
    List<String> authors;

    /**
     * Declaration of the fixed variable type, indicating that the type of the document is of AvMaterial
     */
    static final String type = "av_material";

}