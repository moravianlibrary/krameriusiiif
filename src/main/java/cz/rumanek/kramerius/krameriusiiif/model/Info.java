package cz.rumanek.kramerius.krameriusiiif.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Image information from info.json
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Info {
    private Integer width;
    private Integer height;

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }
}