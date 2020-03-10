package cz.rumanek.kramerius.krameriusiiif.manifest;

import cz.rumanek.kramerius.krameriusiiif.model.DocumentEntity;
import de.digitalcollections.iiif.model.PropertyValue;
import de.digitalcollections.iiif.model.sharedcanvas.Resource;

public abstract class AbstractBuilder<T extends AbstractBuilder<T,R>,R extends Resource<R>> {

    protected DocumentEntity document;
    protected String label;
    protected String id;

    public AbstractBuilder(DocumentEntity document, String id) {
        this.document = document;
        this.id = id;
    }

    public T label() {
        label = document.getLabel();
        return getBuilder();
    }

    public  T setId(CharSequence id) {
        this.id = id.toString();
        return getBuilder();
    }

    public T setUrl(CharSequence baseUrl, String suffix) {
        id = baseUrl + document.getPid() + suffix;
        return getBuilder();
    }

    abstract protected T getBuilder();

    abstract protected R getResource();

    protected void addLabel(){
        if (label != null) {
           getResource().setLabel(new PropertyValue(label));
        }
    }
}
