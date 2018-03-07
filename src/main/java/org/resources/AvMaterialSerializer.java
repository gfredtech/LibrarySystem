package org.resources;

import org.storage.QueryParameters;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Serializer class of AvMaterial
 * The purpose of the class is to translate an AvMaterial item to its query parameters
 * @author Developed by Roberto Chavez
 * @author Reviewed and improved by Vladimir Scherba
 */

public class AvMaterialSerializer extends ItemSerializer<AvMaterial>  {

    /**
     * Function converting an object of the AvMaterial class to query parameters
     * @param material - This is the AvMaterial item from which the function will return the query parameters
     * @return the parameters of the item
     */
    @Override
    public QueryParameters toQueryParameters(AvMaterial material) {
        return new QueryParameters()
                .add("price", material.getPrice())
                .add("title", material.getTitle())
                .add("keywords", material.getKeywords())
                .add("copy_num", material.getCopiesNum())
                .add("DEFAULT")
                .add("authors", material.getAuthors())
                .add("is_reference", material.isReference());

    }

    /**
     * This function returns the List of authors who created the AvMaterial
     * @param rs - A table of data representing a database of AvMaterial and its authors
     * @return Result set of AvMaterial
     * @throws SQLException
     */
    @Override
    public AvMaterial fromResultSet(ResultSet rs) throws SQLException {
        AvMaterialFactory m = new AvMaterialFactory();

        String[] authorsArray =
                (String[])rs.getArray("authors").getArray();
        List<String> authors = Arrays.asList(authorsArray);

        m.setAuthors(authors);

        factory = m;

        return super.fromResultSet(rs);
    }
}