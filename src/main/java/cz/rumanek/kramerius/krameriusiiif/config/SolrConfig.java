package cz.rumanek.kramerius.krameriusiiif.config;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static cz.rumanek.kramerius.krameriusiiif.config.Constants.*;

@Configuration
public class SolrConfig {

    private Logger logger = LoggerFactory.getLogger(SolrConfig.class);
    private final String solrEndpointUrl;
    private final String solrHandler;

    public SolrConfig(@Qualifier(SOLR_ENDPOINT) String solrEndpointUrl,
                      @Qualifier(SOLR_HANDLER) @Autowired(required = false) String solrHandler) {
        this.solrEndpointUrl = solrEndpointUrl;
        this.solrHandler = solrHandler;
    }

    /**
     * Solr endpoint at kramerius.mzk.cz doesn`t support default (faster) BinaryResponseParser
     */
    @Bean(SOLR_CLIENT)
    @ConditionalOnProperty(name = SOLR_XML_PARSER)
    public SolrClient solrClientExternal(){
        HttpSolrClient solrClient = solrClient();
        solrClient.setParser(new XMLResponseParser());
        logger.info("XML response parser");
        return solrClient;
    }

    /**
     * Default BinaryResponseParser
     */
    @Bean(SOLR_CLIENT)
    @ConditionalOnMissingBean
    public SolrClient solrClientInternal(){
        logger.info("Binary response parser");
        return solrClient();
    }

    /**
     * If SOLR endpoint uses nonstandard URL like
     * <b>https://kramerius.mzk.cz/search/api/v5.0/search</b><p>
     * default "select" request handler (last segment) must be replaced with "search".
     * Core (collection) is set in model entity.
     * @see cz.rumanek.kramerius.krameriusiiif.model.KDocument
     */
    private HttpSolrClient solrClient() {
        HttpSolrClient solrClient = new HttpSolrClient.Builder(solrEndpointUrl)
                .allowCompression(true)
                .build();
        //workaround to override request handler
        if (solrHandler != null) {
            solrClient.setRequestWriter(new BinaryRequestWriter() {
                @Override
                public String getPath(SolrRequest req) {
                    return (solrHandler);
                }
            });
        }
        return solrClient;
    }

}
