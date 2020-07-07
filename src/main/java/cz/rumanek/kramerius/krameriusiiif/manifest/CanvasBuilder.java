package cz.rumanek.kramerius.krameriusiiif.manifest;

import cz.rumanek.kramerius.krameriusiiif.model.DocumentEntity;
import de.digitalcollections.iiif.model.image.ImageApiProfile;
import de.digitalcollections.iiif.model.sharedcanvas.Canvas;

public class CanvasBuilder extends AbstractBuilder<CanvasBuilder,Canvas> {

    private Canvas canvas;
    private String imageBaseUrl;
    private boolean resolution;
    private boolean canvasRatio;
    private Integer canvasWidth = 3000;
    private Integer canvasHeight = 2000;

    public CanvasBuilder (DocumentEntity document) {
        super(document,"canvas");
    }

    public static CanvasBuilder of(DocumentEntity document) {
        return new CanvasBuilder(document);
    }

    public CanvasBuilder baseUrl(CharSequence baseUrl) {
        setUrl(baseUrl,"/canvas");
        return this;
    }
    //TODO add builder phases 1,2,3...

    public CanvasBuilder imageBaseUrl(String imageBaseUrl) {
        this.imageBaseUrl = imageBaseUrl;
        return this;
    }

    public CanvasBuilder resolution() {
        resolution = true;
        return this;
    }

    public CanvasBuilder setCanvasRatio(Integer canvasWidth, Integer canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        canvasRatio = true;
        return this;
    }

    public CanvasBuilder canvasRatio() {
        canvasRatio = true;
        return this;
    }

    public Canvas build() {
        canvas = new Canvas(id);
        // Width/height of image must be set before image element is created using addIIIFImage
        // If applied afterwards it will modify outer Canvas resolution (page ratio)
        // If not set  after image element, outer width/height-ratio values will be same as image values
        // https://iiif.io/api/presentation/2.1/#height
        addResolution();
        addLabel();
        addIIIFImage();
        addCanvasRatio();
        return canvas;
    }

    protected void addResolution() {
        if (resolution) {
            canvas.setWidth(document.getInfo().getWidth());
            canvas.setHeight(document.getInfo().getHeight());
        }
    }

    protected void addIIIFImage() {
        if (imageBaseUrl != null) {
            canvas.addIIIFImage(imageBaseUrl + document.getPid(), ImageApiProfile.LEVEL_ONE);
        }
    }

    protected void addCanvasRatio() {
        if (canvasRatio) {
            canvas.setWidth(canvasWidth);
            canvas.setHeight(canvasHeight);
        }
    }

    @Override
    protected final CanvasBuilder getBuilder() {
        return this;
    }

    @Override
    protected Canvas getResource() {
        return canvas;
    }
}
