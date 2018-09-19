package cz.rumanek.kramerius.krameriusiiif.dao;

import cz.rumanek.kramerius.krameriusiiif.entity.Info;
import org.springframework.web.client.RestTemplate;

public class ImageInfoRepositoryImpl implements ImageInfoRepository {
    @Override
    public Info get(String pid) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            return restTemplate.getForObject("https://kramerius.mzk.cz/search/iiif/" + pid + "/info.json", Info.class);
        } catch (Exception ex) {
            return null;
        }
    }
}
