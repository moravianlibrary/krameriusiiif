package cz.rumanek.kramerius.krameriusiiif.controller;

import cz.rumanek.kramerius.krameriusiiif.manifest.CanvasBuilder;
import cz.rumanek.kramerius.krameriusiiif.manifest.CollectionBuilder;
import cz.rumanek.kramerius.krameriusiiif.manifest.ManifestBuilder;
import cz.rumanek.kramerius.krameriusiiif.model.DocumentEntity;
import cz.rumanek.kramerius.krameriusiiif.service.DocumentService;
import de.digitalcollections.iiif.model.PropertyValue;
import de.digitalcollections.iiif.model.sharedcanvas.Collection;
import de.digitalcollections.iiif.model.sharedcanvas.Manifest;
import de.digitalcollections.iiif.model.sharedcanvas.Sequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
public class ManifestController {

    /**
     * IIIF Image API endpoint url
     */
    private final String iiifEndpointURL;
    private final String baseUrl;

    @Autowired
    private DocumentService documentService;

    public ManifestController(@Value("#{getImageEndpoint}") String iiifEndpointURL,
                              @Value("#{getBaseUrl}") String baseUrl) {
        this.iiifEndpointURL = iiifEndpointURL;
        this.baseUrl = baseUrl;
    }

    @GetMapping(value = "/")
    public Collection home()  {
        Collection collection = new Collection(baseUrl);
        collection.setLabel(new PropertyValue("Vybrané kolekce"));

        Manifest manifest = new Manifest(baseUrl + "uuid:6203552b-922b-425b-845a-2a7e1ee04c6c/manifest");
        manifest.setLabel(new PropertyValue("test manifest"));
        collection.addManifest(manifest);

        Collection collection1 = new Collection(baseUrl + "uuid:5a2dd690-54b9-11de-8bcd-000d606f5dc6/collection");
        collection1.setLabel(new PropertyValue("Davidova houpačka"));
        collection.addCollection(collection1);

        return collection;
    }

    @GetMapping(value = "{pid}/collection")
    public Collection collection(HttpServletRequest request, @PathVariable String pid) {
        Optional<DocumentEntity> parentDoc = documentService.findByPid(pid);
        if (parentDoc.isPresent()) {
            Collection parentCollection =
                    CollectionBuilder.of(parentDoc.get())
//***baseUrl instead  .setId(request.getRequestURL())
                    .baseUrl(baseUrl)
                    .label()
                    .build();
            documentService.getCollectionDocumentsFor(pid).map(doc->
                    CollectionBuilder.of(doc)
                    .baseUrl(baseUrl)
                    .label()
                    .build()).forEachOrdered(parentCollection::addCollection);
            return parentCollection;
        } else {
            return null;
        }
    }

    @GetMapping(value = "{pid}/manifest")
    public Manifest manifest(HttpServletRequest request, @PathVariable String pid) {
        Optional<DocumentEntity> parentDoc = documentService.findByPid(pid);
        if (parentDoc.isPresent()) {
            Sequence sequence = new Sequence(null);
            documentService.getPagesFor(pid)
                    .map(page -> CanvasBuilder.of(page)
                            .baseUrl(baseUrl)
                            .label()
                            .resolution()
                            .imageUrl(iiifEndpointURL)
  //***second resolution    .canvasRatio()
                            .build()
                    )
                    .forEachOrdered(sequence::addCanvas);
            return ManifestBuilder.of(parentDoc.get())
                    .setId(request.getRequestURL())
                    .label()
                    .build()
                    .addSequence(sequence);
        }
        else {
            return null;
        }
    }

}
