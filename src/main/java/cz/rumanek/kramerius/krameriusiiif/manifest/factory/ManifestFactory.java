package cz.rumanek.kramerius.krameriusiiif.manifest.factory;

import cz.rumanek.kramerius.krameriusiiif.manifest.CanvasBuilder;
import cz.rumanek.kramerius.krameriusiiif.manifest.ManifestBuilder;
import cz.rumanek.kramerius.krameriusiiif.model.DocumentEntity;
import de.digitalcollections.iiif.model.sharedcanvas.Canvas;
import de.digitalcollections.iiif.model.sharedcanvas.Manifest;
import de.digitalcollections.iiif.model.sharedcanvas.Sequence;

import java.util.stream.Stream;

public class ManifestFactory {

    CharSequence url;
    DocumentEntity parentDoc;

    public ManifestFactory(CharSequence url, DocumentEntity parentDoc) {
        this.parentDoc = parentDoc;
        this.url = url;
    }

    public Manifest parent() {
        return  ManifestBuilder.of(parentDoc)
                .baseUrl(url)
                .label()
                .build();
    }

    public Manifest imageSequence(Stream<DocumentEntity> pages, String imageBaseUrl) {
        return parent().addSequence(getImageSequence(pages, imageBaseUrl));
    }

    private Sequence getImageSequence(Stream<DocumentEntity> pages, String imageBaseUrl) {
        Sequence sequence = new Sequence(null);
        pages.map(page -> buildImageCanvas(page, imageBaseUrl)).forEachOrdered(sequence::addCanvas);
        return sequence;
    }

    private Canvas buildImageCanvas(DocumentEntity page, String imageBaseUrl) {
        return CanvasBuilder.of(page)
                .baseUrl(url)
                .label()
                .resolution()
                .imageBaseUrl(imageBaseUrl)
                .build();
    }

}
