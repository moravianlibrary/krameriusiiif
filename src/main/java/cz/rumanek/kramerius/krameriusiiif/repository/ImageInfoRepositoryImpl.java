package cz.rumanek.kramerius.krameriusiiif.repository;

import cz.rumanek.kramerius.krameriusiiif.model.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * URL of IIIF Image API is defined in application.properties as "kramerius.iiif.endpoint"
 */
@Repository
public class ImageInfoRepositoryImpl implements ImageInfoRepository {

    private final String iiifEndpointUrl;

    @Autowired
    private RestTemplate restTemplate;

    public ImageInfoRepositoryImpl(@Value("#{getImageEndpoint}") String iiifEndpointUrl) {
        this.iiifEndpointUrl = iiifEndpointUrl;
    }

    @Override
    public Info getInfo(String pid) {
        try {
            //System.out.println("Request for image PID => " + pid);
            return restTemplate.getForObject(iiifEndpointUrl + pid + "/info.json", Info.class);
        } catch (HttpServerErrorException | HttpClientErrorException e) {
            System.out.println("Request for invalid image PID => " + pid + " => " + e.getStatusText() + " " + e.getStatusCode());
            return null;
        }
    }
}
