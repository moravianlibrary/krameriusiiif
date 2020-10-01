package cz.rumanek.kramerius.krameriusiiif.manifest.factory;

import cz.rumanek.kramerius.krameriusiiif.manifest.CanvasBuilder;
import cz.rumanek.kramerius.krameriusiiif.manifest.ManifestBuilder;
import cz.rumanek.kramerius.krameriusiiif.model.DocumentEntity;
import de.digitalcollections.iiif.model.sharedcanvas.Canvas;
import de.digitalcollections.iiif.model.sharedcanvas.Manifest;
import de.digitalcollections.iiif.model.sharedcanvas.Sequence;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ManifestFactory {

    CharSequence url;
    DocumentEntity parentDoc;
    ForkJoinPool forkJoinPool = new ForkJoinPool(50);

    public ManifestFactory(CharSequence url, DocumentEntity parentDoc) {
        this.parentDoc = parentDoc;
        this.url = url;
    }

    public Manifest parent() {
        return ManifestBuilder.of(parentDoc)
                .baseUrl(url)
                .label()
                .build();
    }

    public Manifest imageSequence(Stream<DocumentEntity> pages, String imageBaseUrl) {
        return parent().addSequence(getImageSequence(pages, imageBaseUrl));
    }

    private Sequence getImageSequence(Stream<DocumentEntity> pages, String imageBaseUrl) {
        final Sequence sequence = new Sequence(null);
        List<Canvas> canvases = new ArrayList<>(50);
        try {
            CompletableFuture.runAsync((() ->
            canvases.addAll(pages.map(page -> buildImageCanvas(page, imageBaseUrl)).collect(Collectors.toList()))), forkJoinPool).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        sequence.setCanvases(canvases);
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
