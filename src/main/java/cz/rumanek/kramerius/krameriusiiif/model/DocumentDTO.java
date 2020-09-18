package cz.rumanek.kramerius.krameriusiiif.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Combined retrieved data to construct manifest
 */

// Final class ensures type safety when casting on interface
// such as Future etc. in streaming expressions
public final class DocumentDTO implements DocumentEntity {

    private Logger logger = LoggerFactory.getLogger(DocumentDTO.class);
    private String pid;
    private String label;
    private String model;

    private Future<Info> info;

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

    public Info getInfo() {
        Info instance = null;
        try {
            instance = info.get();
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error getting image info! " + e.getCause().getClass());
        }
        if (instance != null) {
            return instance;
        } else {
            logger.trace("Info is null!");
            return new Info();
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
