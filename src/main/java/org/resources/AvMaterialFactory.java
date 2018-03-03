package org.resources;

import java.util.List;

public class AvMaterialFactory extends ItemFactory<AvMaterial> {

    public AvMaterialFactory() {
        super(new AvMaterial());
    }

    @Override
    public AvMaterial build(int id) {
        return (AvMaterial)super.build(id);
    }

    void setAuthors(List<String> authors) {
        item.authors = authors;
    }
}
