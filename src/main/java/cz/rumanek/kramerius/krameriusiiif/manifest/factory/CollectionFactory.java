package cz.rumanek.kramerius.krameriusiiif.manifest.factory;

import cz.rumanek.kramerius.krameriusiiif.manifest.CollectionBuilder;
import cz.rumanek.kramerius.krameriusiiif.model.DocumentEntity;
import de.digitalcollections.iiif.model.sharedcanvas.Collection;

import java.util.stream.Stream;

public class CollectionFactory {

    CharSequence url;
    DocumentEntity parentDoc;

    public CollectionFactory(CharSequence url, DocumentEntity parentDoc) {
        this.parentDoc = parentDoc;
        this.url = url;
    }

    public Collection parent() {
        return buildCollection(parentDoc);
    }

    private Collection buildCollection(DocumentEntity documentEntity) {
        return  CollectionBuilder.of(documentEntity)
                .baseUrl(url)
                .label()
                .build();
    }

    public Collection addChildCollections(Stream<DocumentEntity> documentEntityStream) {
        Collection parent = parent();
        getSubCollections(documentEntityStream).forEachOrdered(parent::addCollection);
        return parent;
    }

    private Stream<Collection> getSubCollections(Stream<DocumentEntity> documentEntityStream) {
        return documentEntityStream.map(this::buildCollection);
    }

}
