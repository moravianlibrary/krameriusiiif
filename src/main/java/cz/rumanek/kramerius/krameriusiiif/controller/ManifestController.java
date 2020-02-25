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

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

@RestController
public class ManifestController {

    /**
     * IIIF Image API endpoint url
     */
    @Value("${kramerius.iiif.endpoint}")
    private String iiifEndpointURL;

    @Autowired
    private DocumentService documentService;

    @GetMapping(value = "/")
    public Collection home(HttpServletRequest request) {
        Collection collection = new Collection(request.getRequestURL().toString());
        collection.setLabel(new PropertyValue("Vybrané kolekce"));

        Manifest manifest = new Manifest(request.getRequestURL().toString()+"uuid:6203552b-922b-425b-845a-2a7e1ee04c6c/manifest");
        manifest.setLabel(new PropertyValue("test manifest"));
        collection.addManifest(manifest);

        Collection collection1 = new Collection(request.getRequestURL().toString()+"uuid:5a2dd690-54b9-11de-8bcd-000d606f5dc6/collection");
        collection1.setLabel(new PropertyValue("Davidova houpačka"));
        collection.addCollection(collection1);

        return collection;
    }

    @GetMapping(value = "{pid}/collection")
    public Collection collection(HttpServletRequest request, @PathVariable String pid) {
        Optional<DocumentDTO> document = documentService.findByPid(pid);
        if (document.isPresent()) {
            Collection collection = new Collection(request.getRequestURL().toString());
            collection.setLabel(new PropertyValue(document.get().getLabel()));

            documentService.findByParentPid(pid).filter(doc -> !Arrays.asList("page").contains(doc)).map(doc-> {
                Collection collection1 = new Collection(getBaseUrl(request) + doc.getPid() + "/collection");
                collection1.setLabel(new PropertyValue(doc.getLabel()));
                return collection1;
            }).forEachOrdered(coll -> collection.addCollection(coll));


            return collection;
        } else {
            return null;
        }
    }

    @GetMapping(value = "{pid}/manifest")
    public Manifest manifest(HttpServletRequest request, @PathVariable String pid) {

        Optional<DocumentDTO> document = documentService.findByPid(pid);

        if (document.isPresent()) {
            Manifest manifest = new Manifest(request.getRequestURL().toString());
            manifest.setLabel(new PropertyValue(document.get().getLabel()));
            Sequence sequence = new Sequence(null);

            documentService.findByParentPid(pid).filter(doc -> doc.getModel().equals("page"))
                    .map(doc -> {
                        Canvas canvas = new Canvas(getBaseUrl(request) + doc.getPid() + "/canvas");
                        canvas.setWidth(doc.getWidth());
                        canvas.setHeight(doc.getHeight());
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

    private String getBaseUrl(HttpServletRequest request) {
        String serverName = request.getServerName();
        int portNumber = request.getServerPort();
        String contextPath = request.getContextPath();
        return request.getScheme() + "://" + serverName + ":" +portNumber + contextPath + "/";
    }

}
