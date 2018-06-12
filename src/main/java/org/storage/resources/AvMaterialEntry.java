package org.storage.resources;

import org.items.AvMaterial;
import org.items.AvMaterialFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Representation of an AV material in a database
 * The purpose of the class is to translate an AvMaterial object to SQL format and vice versa
 * @author Vladimir Shcherba
 */

public class AvMaterialEntry extends ItemEntry<AvMaterial>  {

    /**
     * Initialize the AvMaterial instance from a database row in the result set
     * @param rs the result set, pointing to a row with an AV material data
     * @throws SQLException
     */
    public AvMaterialEntry(ResultSet rs) throws SQLException {
        super(rs);
    }

    @Override
    public Resource<AvMaterialEntry> getResourceType() {
        return Resource.AvMaterial;
    }

    @Override
    protected AvMaterialFactory initFactory(ResultSet rs) throws SQLException {
        return new AvMaterialFactory()
                .authors(Arrays.asList(
                        (String[])rs.getArray("authors").getArray()));
    }
}