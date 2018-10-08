package cz.rumanek.kramerius.krameriusiiif;

import cz.rumanek.kramerius.krameriusiiif.dto.DocumentDTO;
import cz.rumanek.kramerius.krameriusiiif.service.DocumentService;
import java.util.Optional;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import de.digitalcollections.iiif.model.PropertyValue;
import de.digitalcollections.iiif.model.image.ImageApiProfile;
import de.digitalcollections.iiif.model.sharedcanvas.Canvas;
import de.digitalcollections.iiif.model.sharedcanvas.Collection;
import de.digitalcollections.iiif.model.sharedcanvas.Manifest;
import de.digitalcollections.iiif.model.sharedcanvas.Sequence;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

@SpringBootApplication
@RestController
public class KrameriusiiifApplication {

    private DocumentService documentService;

    @Inject
    public KrameriusiiifApplication(DocumentService documentService) {
        this.documentService = documentService;
    }

    @RequestMapping("/")
    public Collection collection(HttpServletRequest request) {
        Collection collection = new Collection(request.getRequestURL().toString());
        collection.setLabel(new PropertyValue("test kolekce"));

        Manifest manifest = new Manifest(request.getRequestURL().toString()+"uuid:6203552b-922b-425b-845a-2a7e1ee04c6c/manifest");
        manifest.setLabel(new PropertyValue("test manifest"));

        collection.addManifest(manifest);
        return collection;
    }

    @RequestMapping("{pid}/manifest")
    public Manifest manifest(HttpServletRequest request, @PathVariable String pid) {

        Optional<DocumentDTO> document = documentService.findByPid(pid);

        if (document.isPresent()) {
            Manifest manifest = new Manifest(request.getRequestURL().toString());
            manifest.setLabel(new PropertyValue(document.get().getLabel()));
            Sequence sequence = new Sequence(null);


            documentService.findByParentPid(pid).map(doc -> {
                try {
                    Canvas canvas = new Canvas(getBaseUrl(request) + doc.getPid() + "/canvas");
                    canvas.setWidth(doc.getWidth());
                    canvas.setHeight(doc.getHeight());
                    canvas.setLabel(new PropertyValue(doc.getLabel()));

                    StringBuffer requestURL = new StringBuffer("https://kramerius.mzk.cz/search/iiif/");
                    canvas.addIIIFImage(requestURL.append(doc.getPid()).toString(), ImageApiProfile.LEVEL_ONE);
                    return canvas;
                } catch (HttpServerErrorException e) {
                    throw new RuntimeException(doc.getPid() + " is simply wrong");
                }
            }).forEachOrdered(canvas -> sequence.addCanvas(canvas));

            manifest.addSequence(sequence);
            return manifest;
        } else {
            return null;
        }
    }

    @RequestMapping("{pid}/canvas")
    public Canvas canvas(HttpServletRequest request, @PathVariable String pid) {
        DocumentDTO doc = documentService.findByPid(pid).get();

        Canvas canvas = new Canvas(getBaseUrl(request) + doc.getPid() + "/canvas");
        canvas.setWidth(doc.getWidth());
        canvas.setHeight(doc.getHeight());
        canvas.setLabel(new PropertyValue(doc.getLabel()));

        StringBuffer requestURL = new StringBuffer("https://kramerius.mzk.cz/search/iiif/");
        canvas.addIIIFImage(requestURL.append(doc.getPid()).toString(), ImageApiProfile.LEVEL_ONE);
        return canvas;
    }

    private String getBaseUrl(HttpServletRequest request) {
        String serverName = request.getServerName();
        int portNumber = request.getServerPort();
        String contextPath = request.getContextPath();
        return request.getScheme() + "://" + serverName + ":" +portNumber + contextPath + "/";
    }

    public static void main(String[] args) {
        SpringApplication.run(KrameriusiiifApplication.class, args);
    }
}
