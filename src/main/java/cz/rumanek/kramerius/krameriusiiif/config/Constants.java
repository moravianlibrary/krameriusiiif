package cz.rumanek.kramerius.krameriusiiif.config;

public class Constants {

    public static final String SERVER_BASEURL = "server_baseurl";
    public static final String IIIF_ENDPOINT = "iiif_endpoint";
    public static final String SOLR_ENDPOINT = "solr_endpoint";
    public static final String SOLR_COLLECTION = "solr_collection";
    public static final String SOLR_HANDLER = "solr_handler";
    public static final String PRETTY_PRINT = "pretty_print";

    public static final String IIIF_ENDPOINT_CLIENT = "iiif_endpoint_client";
    /**
     * Set by application property kramerius.solr_xml
     */
    public static final String SOLR_XML_PARSER = "kramerius.solr_xml";
    /**
     * Bean name "solrClient" is required by Spring Data Solr
     */
    public static final String SOLR_CLIENT = "solrClient";
    /**
     * Solr collection bean wired by EL script
     */
    public static final String SOLR_COLLECTION_BEAN = "#{@solr_collection}";


}
