package org.resources;

import java.util.List;

/**
 * Builder Class for AvMaterial, a factory intended to create instances of the AvMaterial class
 * This class inhertis from superclass ItemFactory
 * @author Developed by Roberto Chavez
 * @author Reviewed and improved by Vladimir Scherba
 */

public class AvMaterialFactory extends ItemFactory<AvMaterial> {

    /**
     * Constructor of the class, using ''super'' to iherit features from the superclass ItemFactory
     */

    public AvMaterialFactory() {
        super(new AvMaterial());
    }

    /**
     * Method to the AvMaerial item with the id parameter
     * @param id with the id we identify the exact item that is referred to
     * @return the item AvMaterial
     */

    @Override
    public AvMaterial build(int id) {
        return (AvMaterial)super.build(id);
    }

    /**
     * Method to write the authors of the item AvMaterial
     * @param authors
     */

    void setAuthors(List<String> authors) {
        item.authors = authors;
    }
}