package cz.rumanek.kramerius.krameriusiiif.dao;

import cz.rumanek.kramerius.krameriusiiif.entity.Info;
import javax.inject.Inject;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Repository
public class ImageInfoRepositoryImpl implements ImageInfoRepository {

    @Inject
    private RestTemplate restTemplate;

    @Override
    public Info get(String pid) {
        try {
            return restTemplate.getForObject("https://kramerius.mzk.cz/search/iiif/" + pid + "/info.json", Info.class);
        } catch (HttpServerErrorException e) {
            return null; //TODO-MR When Kramerius return 500. Shitty, but will be replaced by webflux?
        }
    }
}
