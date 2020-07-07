package cz.rumanek.kramerius.krameriusiiif.model;

/**
 * Represents all types document can represent:
 * such as monograph, periodical, periodical volume, page etc.
 * Type is taken from document_type (fedora.model)
 */
public interface DocumentEntity {
    boolean isPage();
    String getPid();
    Info getInfo();
    String getLabel();
}
