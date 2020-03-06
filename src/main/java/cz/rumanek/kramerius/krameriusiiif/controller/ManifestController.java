package cz.rumanek.kramerius.krameriusiiif.controller;

import cz.rumanek.kramerius.krameriusiiif.model.DocumentDTO;
import cz.rumanek.kramerius.krameriusiiif.service.DocumentService;
import de.digitalcollections.iiif.model.PropertyValue;
import de.digitalcollections.iiif.model.image.ImageApiProfile;
import de.digitalcollections.iiif.model.sharedcanvas.Canvas;
import de.digitalcollections.iiif.model.sharedcanvas.Collection;
import de.digitalcollections.iiif.model.sharedcanvas.Manifest;
import de.digitalcollections.iiif.model.sharedcanvas.Sequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
    public Collection collection(@PathVariable String pid) {
        Optional<DocumentDTO> document = documentService.findByPid(pid);
        if (document.isPresent()) {
            Collection collection = new Collection(getRequestURL());
            collection.setLabel(new PropertyValue(document.get().getLabel()));

            documentService.findByParentPid(pid).filter(doc -> !doc.isPage()).map(doc-> {
                Collection collection1 = new Collection(getRequestBaseUrl() + doc.getPid() + "/collection");
                collection1.setLabel(new PropertyValue(doc.getLabel()));
                return collection1;
            }).forEachOrdered(coll -> collection.addCollection(coll));

            return collection;
        } else {
            return null;
        }
    }

    @GetMapping(value = "{pid}/manifest")
    public Manifest manifest(@PathVariable String pid) {

        Optional<DocumentDTO> document = documentService.findByPid(pid);

        if (document.isPresent()) {
            Manifest manifest = new Manifest(getRequestURL());
            manifest.setLabel(new PropertyValue(document.get().getLabel()));
            Sequence sequence = new Sequence(null);

            documentService.findByParentPid(pid).filter(doc -> doc.isPage())
                    .map(doc -> {
                        Canvas canvas = new Canvas(getRequestBaseUrl() + doc.getPid() + "/canvas");
                        canvas.setWidth(doc.getInfo().getWidth());
                        canvas.setHeight(doc.getInfo().getHeight());
                        canvas.setLabel(new PropertyValue(doc.getLabel()));
                        canvas.addIIIFImage(iiifEndpointURL + doc.getPid(), ImageApiProfile.LEVEL_ONE);
                        return canvas;
                    }).forEachOrdered(canvas -> sequence.addCanvas(canvas));

            manifest.addSequence(sequence);
            return manifest;
        } else {
            return null;
        }
    }

    private HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    private String getRequestURL() {
        return getRequest().getRequestURL().toString();
    }

    private String getRequestBaseUrl() {
        return getRequestURL().replace(getRequest().getRequestURI(), getRequest().getContextPath() + "/");
    }

}
