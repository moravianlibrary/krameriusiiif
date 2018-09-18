package cz.rumanek.kramerius.krameriusiiif;

import cz.rumanek.kramerius.krameriusiiif.dto.Info;
import cz.rumanek.kramerius.krameriusiiif.dto.KDocument;
import cz.rumanek.kramerius.krameriusiiif.service.DocumentService;
import java.util.Optional;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import de.digitalcollections.iiif.model.PropertyValue;
import de.digitalcollections.iiif.model.image.ImageApiProfile;
import de.digitalcollections.iiif.model.sharedcanvas.Canvas;
import de.digitalcollections.iiif.model.sharedcanvas.Manifest;
import de.digitalcollections.iiif.model.sharedcanvas.Sequence;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@RestController
public class KrameriusiiifApplication {

    private DocumentService documentService;

    @Inject
    public KrameriusiiifApplication(DocumentService documentService) {
        this.documentService = documentService;
    }

    @RequestMapping("/")
    public Manifest index(HttpServletRequest request) {

        //TODO-MR https://kramerius.mzk.cz/search/iiif/uuid:679942e7-1316-4ecc-a0ca-50560986b449/full/full/0/default.jpg
        String pid = "uuid:9ebcb206-24b7-4dc7-b367-3d9ad7179c23";

        Optional<KDocument> document = documentService.findByPid(pid);

        if (document.isPresent()) {
            Manifest manifest = new Manifest(request.getRequestURL().toString());
            manifest.setLabel(new PropertyValue(document.get().getLabel()));
            Sequence sequence = new Sequence(null);


            documentService.findByParentPid("uuid:9ebcb206-24b7-4dc7-b367-3d9ad7179c23").limit(5).map(doc -> {
                try {
                    RestTemplate restTemplate = new RestTemplate();
                    Info info = restTemplate.getForObject("https://kramerius.mzk.cz/search/iiif/" + doc.getPid() + "/info.json", Info.class);
                    Canvas canvas = new Canvas("http://localhost/canvas/" + doc.getPid());
                    canvas.setWidth(info.getWidth());
                    canvas.setHeight(info.getHeight());
                    canvas.setLabel(new PropertyValue(doc.getLabel()));

                    StringBuffer requestURL = new StringBuffer("http://kramerius.mzk.cz/search/iiif/");
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

    public static void main(String[] args) {
        SpringApplication.run(KrameriusiiifApplication.class, args);
    }
}
