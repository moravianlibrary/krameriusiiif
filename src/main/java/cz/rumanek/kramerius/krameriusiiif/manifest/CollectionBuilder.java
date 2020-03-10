package cz.rumanek.kramerius.krameriusiiif.manifest;

import cz.rumanek.kramerius.krameriusiiif.model.DocumentEntity;
import de.digitalcollections.iiif.model.sharedcanvas.Collection;

public class CollectionBuilder extends AbstractBuilder<CollectionBuilder,Collection> {

    private Collection collection;

    public CollectionBuilder(DocumentEntity documentEntity) {
        super(documentEntity,"collection");
    }

    public static CollectionBuilder of(DocumentEntity document){
        return new CollectionBuilder(document);
    }

    public CollectionBuilder baseUrl(CharSequence baseUrl) {
        setUrl(baseUrl,"/collection");
        return this;
    }

    @Override
    protected CollectionBuilder getBuilder() {
        return this;
    }

    @Override
    protected Collection getResource() {
        return collection;
    }

    public Collection build() {
        collection = new Collection(id);
        addLabel();
        return collection;
    }

}
