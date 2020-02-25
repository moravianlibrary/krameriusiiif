package cz.rumanek.kramerius.krameriusiiif.repository;

import cz.rumanek.kramerius.krameriusiiif.model.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * URL of IIIF Image API is defined in application.properties as "kramerius.iiif.endpoint"
 */
@Repository
public class ImageInfoRepositoryImpl implements ImageInfoRepository {

    @Value("${kramerius.iiif.endpoint}")
    private String iiifEndpointUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Info get(String pid) {
        try {
            return restTemplate.getForObject(iiifEndpointUrl + pid + "/info.json", Info.class);
        } catch (HttpServerErrorException e) {
            System.out.println("Request for invalid image PID => " + pid);
            return null; //TODO-MR When Kramerius return 500. Shitty, but will be replaced by webflux?
        }
    }
}
