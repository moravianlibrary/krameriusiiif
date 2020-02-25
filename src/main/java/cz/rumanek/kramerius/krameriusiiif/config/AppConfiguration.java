package cz.rumanek.kramerius.krameriusiiif.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import de.digitalcollections.iiif.model.jackson.IiifObjectMapper;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Configuration
@EnableSolrRepositories(
        basePackages = "cz.rumanek.kramerius.krameriusiiif")
@ComponentScan
@ConfigurationProperties
public class AppConfiguration {

    @Resource
    private Environment env;

    @Bean
    String solrCollectionName() {
        return env.getProperty("kramerius.solr.core");
    }

    @Bean
    public IiifObjectMapper iiifObjectMapper() {
        IiifObjectMapper objectMapper = new IiifObjectMapper();
        String prettyPrint = env.getProperty("kramerius.prettyprint","DEFAULT");
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