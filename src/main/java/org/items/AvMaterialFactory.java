package org.items;

import java.util.List;



/**
 * Builder Class for AvMaterial, a factory intended to create instances of the AvMaterial class
 * This class inherits from superclass ItemFactory
 * @author Developed by Roberto Chavez
 * @author Reviewed and improved by Vladimir Scherba
 */

public class AvMaterialFactory extends ItemFactory<AvMaterialFactory, AvMaterial> {

    public AvMaterialFactory() {
        super(new AvMaterial());
    }

    @Override
    public AvMaterial build() {
        assert item.authors != null;
        return item;
    }

    /**
     * Method to set the list of authors of the item
     * @param authors
     */
    public AvMaterialFactory authors(List<String> authors) {
        item.authors = authors;
        return this;
    }
}