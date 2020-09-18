package cz.rumanek.kramerius.krameriusiiif.repository;

import cz.rumanek.kramerius.krameriusiiif.model.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RootUriTemplateHandler;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static cz.rumanek.kramerius.krameriusiiif.config.Constants.IIIF_ENDPOINT_CLIENT;

/**
 * URL of IIIF Image API is defined in application.properties as "kramerius.iiif.endpoint"
 */
@Repository
public class ImageInfoRepositoryImpl implements ImageInfoRepository {

    private Logger logger = LoggerFactory.getLogger(ImageInfoRepository.class);
    private RestTemplate restTemplate;
    private static final String INFO_JSON = "info.json";
    private String rootUri;
    private URI request;

    public ImageInfoRepositoryImpl(@Qualifier(IIIF_ENDPOINT_CLIENT) RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        RootUriTemplateHandler handler = (RootUriTemplateHandler) restTemplate.getUriTemplateHandler();
        rootUri = handler.getRootUri();
    }

    @Override
    public Info getInfo(String pid) {
        try {
            long start = System.currentTimeMillis();
            logger.trace("Image get START:" + pid + ":");
            request = new URI("/" + pid + "/" + INFO_JSON);
            Info info = restTemplate.getForObject(request.toString(), Info.class);
            logger.debug(pid + " " + info.toString());
            logger.trace("Image get FINISH:" + pid + ":" + (System.currentTimeMillis() - start));
            return info;
        } catch (HttpStatusCodeException e) {
            logger.error("Invalid request => " + rootUri + request + " => " + e.getStatusText() + " => " + e.getStatusCode()
                    + System.lineSeparator() + e);
            throw e;
        } catch (RestClientException e) {
            logger.error("RestClientException => " + rootUri + request + " => " + System.lineSeparator() + e);
            throw e;
        } catch (Exception e) {
            logger.error("Exception in request => " + request + System.lineSeparator() + e);
            return null;
        }
    }
}
