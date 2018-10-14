package cz.rumanek.kramerius.krameriusiiif.dto;

import cz.rumanek.kramerius.krameriusiiif.entity.Info;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class DocumentDTO {
    private String pid;
    private String label;
    private String model;

    public Future<Info> info;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getHeight() {
        try {
            return info.get().getHeight();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    public Integer getWidth() {
        try {
            return info.get().getWidth();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    public void setInfo(Future<Info> info) {
        this.info = info;
    }

}
