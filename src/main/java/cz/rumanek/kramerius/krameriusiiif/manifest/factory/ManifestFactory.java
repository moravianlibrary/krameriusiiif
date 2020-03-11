package cz.rumanek.kramerius.krameriusiiif.manifest.factory;

import cz.rumanek.kramerius.krameriusiiif.manifest.CanvasBuilder;
import cz.rumanek.kramerius.krameriusiiif.manifest.ManifestBuilder;
import cz.rumanek.kramerius.krameriusiiif.model.DocumentEntity;
import de.digitalcollections.iiif.model.sharedcanvas.Manifest;
import de.digitalcollections.iiif.model.sharedcanvas.Sequence;

import java.util.stream.Stream;

public class ManifestFactory {

    CharSequence requestUrl;
    DocumentEntity parentDoc;
    Stream<DocumentEntity> pages;

    public ManifestFactory(CharSequence requestUrl, DocumentEntity parentDoc, Stream<DocumentEntity> pages) {
        this.parentDoc = parentDoc;
        this.requestUrl = requestUrl;
        this.pages = pages;
    }

    public Manifest empty() {
        return  ManifestBuilder.of(parentDoc)
                .setId(requestUrl)
                .label()
                .build();
    }

    public Manifest imageSequence(CharSequence baseUrl, String iiifEndpointURL) {
        return empty().addSequence(getImageSequence(baseUrl,iiifEndpointURL));
    }

    private Sequence getImageSequence(CharSequence baseUrl, String iiifEndpointURL) {

        Sequence sequence = new Sequence(null);
        pages.map(page -> CanvasBuilder.of(page)
                            .baseUrl(baseUrl)
                            .label()
                            .resolution()
                            .imageUrl(iiifEndpointURL)
                            .build()
                    )
                    .forEachOrdered(sequence::addCanvas);
        return sequence;
    }

}
