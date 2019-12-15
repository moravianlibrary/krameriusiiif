package cz.rumanek.kramerius.krameriusiiif.repository;

import cz.rumanek.kramerius.krameriusiiif.model.Info;

public interface ImageInfoRepository {

    /**
     * https://iiif.io/api/image/2.1/#image-information-request
     * @param pid
     * @return
     */
    Info get(String pid);
}
