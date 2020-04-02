package cz.rumanek.kramerius.krameriusiiif.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import de.digitalcollections.iiif.model.jackson.IiifObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import static cz.rumanek.kramerius.krameriusiiif.config.Constants.*;

@Configuration
public class AppConfiguration {

    @Bean
    public IiifObjectMapper iiifObjectMapper(@Qualifier(PRETTY_PRINT) Boolean prettyPrint) {
        IiifObjectMapper objectMapper = new IiifObjectMapper();
        if (prettyPrint) {
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        }
        return objectMapper;
    }

    @Bean(IIIF_ENDPOINT_CLIENT)
    public RestTemplate restTemplate(RestTemplateBuilder builder, @Qualifier(IIIF_ENDPOINT) String endpointUrl) {
        return builder
                .rootUri(endpointUrl).build(); //TODO change to org.springframework.web.reactive.client.WebClient?
    }

}