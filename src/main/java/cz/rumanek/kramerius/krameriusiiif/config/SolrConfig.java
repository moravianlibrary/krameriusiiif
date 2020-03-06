package cz.rumanek.kramerius.krameriusiiif.config;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.request.RequestWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SolrConfig {

    Logger logger = LoggerFactory.getLogger(SolrConfig.class);

    private final String solrEndpointUrl;

    public SolrConfig(@Value("#{getSolrEndpoint}") String solrEndpointUrl) {
        this.solrEndpointUrl = solrEndpointUrl;
    }

    @Bean
    public SolrClient solrClient() {
        HttpSolrClient solrClient = new HttpSolrClient.Builder(solrEndpointUrl).build();

        /**
         * When using SOLR endpoint https://kramerius.mzk.cz/search/api/v5.0/search
         * default "select" command in SOLR request path must be replaced with "search"
         * Request path is only request (end part) not whole sent URL.
         * From spring.data.core was removed support for *optional* multicore setup,
         * now multicore setup is FORCED.
         * Core/collection has to be defined in model document @{@link cz.rumanek.kramerius.krameriusiiif.model.KDocument}
         *
         */

        if (solrEndpointUrl.contains("kramerius.mzk.cz")) {
            solrClient.setRequestWriter(new RequestWriter() {
                @Override
                public String getPath(SolrRequest req) {
                    logger.info("SOLR:" + req.getParams().toString().replace("\\", ""));
                    return ("/search");
                }
            });
            // Solr endpoint at kramerius.mzk.cz doesn`t support default (faster) BinaryResponseParser
            solrClient.setParser(new XMLResponseParser());
        }
        return solrClient;
    }

}
