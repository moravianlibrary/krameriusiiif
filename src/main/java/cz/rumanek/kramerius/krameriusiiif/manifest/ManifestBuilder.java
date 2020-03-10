package cz.rumanek.kramerius.krameriusiiif.manifest;

import cz.rumanek.kramerius.krameriusiiif.model.DocumentEntity;
import de.digitalcollections.iiif.model.sharedcanvas.Manifest;

public class ManifestBuilder extends AbstractBuilder<ManifestBuilder,Manifest> {

    private Manifest manifest;

    public ManifestBuilder(DocumentEntity documentEntity) {
        super (documentEntity,"manifest");
    }

    public static ManifestBuilder of(DocumentEntity document){
        return new ManifestBuilder(document);
    }

    @Override
    protected ManifestBuilder getBuilder() {
        return this;
    }

    @Override
    protected Manifest getResource() {
        return manifest;
    }

    public Manifest build() {
        manifest = new Manifest(id);
        addLabel();
        return manifest;
    }
}
