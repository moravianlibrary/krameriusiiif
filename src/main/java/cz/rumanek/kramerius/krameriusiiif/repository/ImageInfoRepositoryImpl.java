package cz.rumanek.kramerius.krameriusiiif.repository;

import cz.rumanek.kramerius.krameriusiiif.model.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import static cz.rumanek.kramerius.krameriusiiif.config.Constants.IIIF_ENDPOINT_CLIENT;

/**
 * URL of IIIF Image API is defined in application.properties as "kramerius.iiif.endpoint"
 */
@Repository
public class ImageInfoRepositoryImpl implements ImageInfoRepository {

    private Logger logger = LoggerFactory.getLogger(ImageInfoRepository.class);
    private RestTemplate restTemplate;

    public ImageInfoRepositoryImpl(@Qualifier(IIIF_ENDPOINT_CLIENT) RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Info getInfo(String pid) {
        try {
            return restTemplate.getForObject("/" + pid + "/info.json", Info.class);
        } catch (HttpServerErrorException | HttpClientErrorException e) {
            logger.error("Invalid request => " + pid + " => " + e.getStatusText() + " " + e.getStatusCode());
            return null;
        }
    }
}
