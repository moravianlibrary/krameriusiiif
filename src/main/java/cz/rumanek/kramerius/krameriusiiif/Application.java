package cz.rumanek.kramerius.krameriusiiif;

import cz.rumanek.kramerius.krameriusiiif.config.ServerProperties;
import cz.rumanek.kramerius.krameriusiiif.config.SolrConfig;
import cz.rumanek.kramerius.krameriusiiif.controller.ManifestController;
import cz.rumanek.kramerius.krameriusiiif.model.MappingUtil;
import cz.rumanek.kramerius.krameriusiiif.repository.DocumentRepository;
import cz.rumanek.kramerius.krameriusiiif.repository.ImageInfoRepository;
import cz.rumanek.kramerius.krameriusiiif.service.DocumentService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

//@SpringBootApplication
@EnableSolrRepositories(basePackageClasses = DocumentRepository.class)
@ComponentScan(basePackageClasses = {SolrConfig.class, DocumentService.class,
        ImageInfoRepository.class, MappingUtil.class, ManifestController.class})
@ConfigurationPropertiesScan(basePackageClasses = ServerProperties.class)
@Import({
        DispatcherServletAutoConfiguration.class,
        ServletWebServerFactoryAutoConfiguration.class,
        JacksonAutoConfiguration.class,
        RestTemplateAutoConfiguration.class,
        HttpMessageConvertersAutoConfiguration.class,
        WebMvcAutoConfiguration.class,
        ErrorMvcAutoConfiguration.class,
        ValidationAutoConfiguration.class,
        //SolrAutoConfiguration.class
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
