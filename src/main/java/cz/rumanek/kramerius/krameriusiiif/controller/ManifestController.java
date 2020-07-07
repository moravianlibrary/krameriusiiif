package cz.rumanek.kramerius.krameriusiiif.controller;

import cz.rumanek.kramerius.krameriusiiif.manifest.factory.CollectionFactory;
import cz.rumanek.kramerius.krameriusiiif.manifest.factory.ManifestFactory;
import cz.rumanek.kramerius.krameriusiiif.model.DocumentEntity;
import cz.rumanek.kramerius.krameriusiiif.service.DocumentService;
import de.digitalcollections.iiif.model.PropertyValue;
import de.digitalcollections.iiif.model.sharedcanvas.Collection;
import de.digitalcollections.iiif.model.sharedcanvas.Manifest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Stream;

import static cz.rumanek.kramerius.krameriusiiif.config.Constants.IIIF_ENDPOINT;
import static cz.rumanek.kramerius.krameriusiiif.config.Constants.SERVER_BASEURL;

@RestController
public class ManifestController {

    /**
     * IIIF Image API endpoint url
     */
    private final String iiifEndpointURL;
    private final String baseUrl;

    private final DocumentService documentService;

    public ManifestController(@Qualifier(IIIF_ENDPOINT) String iiifEndpointURL,
                              @Qualifier(SERVER_BASEURL) String baseUrl,
                              @Autowired DocumentService documentService) {
        this.iiifEndpointURL = iiifEndpointURL;
        this.baseUrl = baseUrl;
        this.documentService = documentService;
    }

    @GetMapping(value = "/")
    public Collection home() {
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
        return new CollectionFactory(baseUrl, getDocument(pid))
                .addChildCollections(getCollections(pid));
    }

    @GetMapping(value = "{pid}/manifest")
    public Manifest manifest(HttpServletRequest request, @PathVariable String pid) {
        return new ManifestFactory(baseUrl, getDocument(pid))
                .imageSequence(getPages(pid), iiifEndpointURL);
    }

    private DocumentEntity getDocument(String pid) {
        return documentService.findByPid(pid).orElseThrow(ResourceNotFoundException::new);
    }

    private Stream<DocumentEntity> getPages(String pid) {
        return documentService.getPagesFor(pid);
    }

    private Stream<DocumentEntity> getCollections(String pid) {
        return documentService.getCollectionDocumentsFor(pid);
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public class ResourceNotFoundException extends RuntimeException {
    }
}