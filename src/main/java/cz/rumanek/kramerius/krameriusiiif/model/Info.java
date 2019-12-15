package cz.rumanek.kramerius.krameriusiiif.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Info {
    private Integer width;
    private Integer height;

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }
}