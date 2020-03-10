package cz.rumanek.kramerius.krameriusiiif.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import de.digitalcollections.iiif.model.jackson.IiifObjectMapper;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Configuration
@ComponentScan
@ConfigurationProperties
public class AppConfiguration {

    @Resource
    private Environment env;

    @Bean
    String getSolrCollectionName() {
        return env.getProperty("kramerius.solr.core","kramerius");
    }

    @Bean
    String getImageEndpoint() {
        return env.getProperty("kramerius.iiif.endpoint","https://kramerius.mzk.cz/search/iiif/");
    }

    @Bean
    String getSolrEndpoint() {
        return env.getProperty("kramerius.solr.endpoint","https://kramerius.mzk.cz/search/api/");
    }

    @Bean
    String getBaseUrl() {
        String serverUrl = env.getProperty("kramerius.iiif.server.url","http://localhost:8080");
        String contextPath = env.getProperty("server.servlet.context-path","/iiif") + "/";
        return serverUrl + contextPath;
    }

    @Bean
    public IiifObjectMapper iiifObjectMapper() {
        IiifObjectMapper objectMapper = new IiifObjectMapper();
        String prettyPrint = env.getProperty("kramerius.prettyprint","NO");
        if (prettyPrint.equals("YES")) {
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        }
        return objectMapper;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build(); //TODO change to org.springframework.web.reactive.client.WebClient?
    }

}