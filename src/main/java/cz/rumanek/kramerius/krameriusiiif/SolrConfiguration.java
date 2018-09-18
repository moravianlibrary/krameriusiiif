package cz.rumanek.kramerius.krameriusiiif;

import de.digitalcollections.iiif.model.jackson.IiifObjectMapper;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.request.RequestWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

@Configuration
@EnableSolrRepositories(
        basePackages = "cz.rumanek.kramerius.krameriusiiif")
@ComponentScan
public class SolrConfiguration {

    @Bean
    public SolrClient solrClient() {
        String urlString = "https://kramerius.mzk.cz/search/api/";
        HttpSolrClient solrClient = new HttpSolrClient.Builder(urlString).build();

        solrClient.setRequestWriter(new RequestWriter() {
            @Override
            public String getPath(SolrRequest req) {
                String path = super.getPath(req);
                String suffix = "select";
                path = path.substring(0, path.indexOf(suffix));
                path = path + "search";
                return path;
            }
        });
        solrClient.setParser(new XMLResponseParser());
        return solrClient;
    }

    @Bean
    public SolrTemplate solrTemplate(SolrClient client) {
        return new SolrTemplate(client);
    }

    @Bean
    public IiifObjectMapper iiifObjectMapper() {
        return new IiifObjectMapper();
    }
}