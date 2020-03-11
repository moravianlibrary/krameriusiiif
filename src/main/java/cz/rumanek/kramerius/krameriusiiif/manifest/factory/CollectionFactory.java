package cz.rumanek.kramerius.krameriusiiif.manifest.factory;

import cz.rumanek.kramerius.krameriusiiif.manifest.CollectionBuilder;
import cz.rumanek.kramerius.krameriusiiif.model.DocumentEntity;
import de.digitalcollections.iiif.model.sharedcanvas.Collection;

import java.util.stream.Stream;

public class CollectionFactory {

    CharSequence requestUrl;
    DocumentEntity parentDoc;
    Stream<DocumentEntity> collectionItems;

    public CollectionFactory(CharSequence requestUrl, DocumentEntity parentDoc, Stream<DocumentEntity> collectionItems) {
        this.parentDoc = parentDoc;
        this.requestUrl = requestUrl;
        this.collectionItems = collectionItems;
    }

    public Collection parent() {
        return  CollectionBuilder.of(parentDoc)
                .setId(requestUrl)
                .label()
                .build();
    }

    public Collection addChildCollections(CharSequence baseUrl) {
        Collection parent = parent();
        getSubCollections(baseUrl).forEachOrdered(parent::addCollection);
        return parent;
    }

    private Stream<Collection> getSubCollections(CharSequence baseUrl) {
        return collectionItems.map(doc ->
                CollectionBuilder.of(doc)
                        .baseUrl(baseUrl)
                        .label()
                        .build());
    }

}
