package cz.rumanek.kramerius.krameriusiiif.dao;

import cz.rumanek.kramerius.krameriusiiif.entity.Info;

public interface ImageInfoRepository {

    /**
     * https://iiif.io/api/image/2.1/#image-information-request
     * @param pid
     * @return
     */
    Info get(String pid);
}
