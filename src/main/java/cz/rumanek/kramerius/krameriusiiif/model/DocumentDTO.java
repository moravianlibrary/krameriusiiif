package cz.rumanek.kramerius.krameriusiiif.model;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Combined retrieved data to construct manifest
 */

// Final class ensures type safety when casting on interface
// such as Future etc. in streaming expressions
public final class DocumentDTO implements DocumentEntity {
    private String pid;
    private String label;
    private String model;

    public Future<Info> info;

    @Override
    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    @Override
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

    @Override
    public Info getInfo(){
        try {
            return info.get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    public void setInfo(Future<Info> info) {
        this.info = info;
    }

    @Override
    public boolean isPage() {
        return model.equals("page");
    }

}
