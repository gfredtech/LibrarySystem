package org.items;

import java.util.List;

/**
 * Data structure representing a media material
 * @author Developed by Roberto Chavez
 * @author Reviewed and improved by Vladimir Scherba
 */

public class AvMaterial extends Item {

    /**
     * @return the list of authors of the material
     */
    public List<String> getAuthors() {
        return authors;
    }

    /**
     * List of Authors of the AvMaterial
     */
    List<String> authors;
}