package org.resources;

import org.storage.QueryParameters;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;



public class AvMaterialSerializer extends ItemSerializer<AvMaterial>  {

    @Override
    public QueryParameters toQueryParameters(AvMaterial material) {
        return new QueryParameters().
                add("av_material_id", material.getId())
                .add("price", material.getPrice())
                .add("title", material.getTitle())
                .add("keywords", material.getKeywords())
                .add("copy_num", material.getCopiesNum())
                .add("DEFAULT")
                .add("authors", material.getAuthors())
                .add("is_reference", material.isReference());

    }

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
