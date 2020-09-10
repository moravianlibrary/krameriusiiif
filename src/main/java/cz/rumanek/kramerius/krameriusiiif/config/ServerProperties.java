package cz.rumanek.kramerius.krameriusiiif.config;

import org.hibernate.validator.constraints.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

import static cz.rumanek.kramerius.krameriusiiif.config.Constants.*;

@ConfigurationProperties(prefix = "kramerius", ignoreUnknownFields = false)
@ConstructorBinding
@Validated
public class ServerProperties {

    private Logger logger = LoggerFactory.getLogger(ServerProperties.class);

    @NotBlank @URL
    private final String serverUrl;
    @NotBlank @URL
    private final String iiifEndpoint;
    @NotBlank @URL
    private final String solrEndpoint;
    @NotBlank
    private final String solrCore;
    @Nullable
    private final String solrHandler;
    @Nullable
    private final Boolean prettyPrint;

    public ServerProperties(String serverUrl,
                            String iiifEndpoint,
                            String solrEndpoint,
                            String solrCore,
                            String solrHandler,
                            Boolean prettyPrint,
                            Boolean solrXml) {

        this.serverUrl = serverUrl;
        this.iiifEndpoint = iiifEndpoint;
        this.solrEndpoint = solrEndpoint;
        this.solrCore = solrCore;
        this.solrHandler = solrHandler;
        this.prettyPrint = prettyPrint;

        logger.info("Configuration properties loaded...");
        logger.debug("Server URL: " + this.serverUrl);
        logger.debug("IIIF Endpoint: " + this.iiifEndpoint);
        logger.debug("SOLR Endpoint: " + this.solrEndpoint);
        logger.debug("SOLR Core: " + this.solrCore);
        logger.debug("SOLR Handler: " + (this.solrHandler == null ? "*default* (/select)": this.solrHandler));
        logger.debug("Indent output: " + (this.prettyPrint == null ? "*default* (false)": this.prettyPrint));
    }

    @Bean(IIIF_ENDPOINT)
    public String getIiifEndpoint() {
        return iiifEndpoint;
    }

    @Bean(SERVER_BASEURL)
    public String getBaseUrl(@Value("${server.servlet.context-path:/iiif}") String contextPath) {
        return serverUrl + contextPath + "/";
    }

    @Bean(SOLR_ENDPOINT)
    String getSolrEndpoint() {
        return solrEndpoint;
    }

    @Bean(SOLR_COLLECTION)
    String getSolrCollectionName() {
        return solrCore;
    }

    @Bean(SOLR_HANDLER)
    String getSolrHandler() {
        return solrHandler;
    }

    @Bean(PRETTY_PRINT)
    Boolean getPrettyPrint() {
        return prettyPrint != null ? prettyPrint : false;
    }

}
