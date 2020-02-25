package cz.rumanek.kramerius.krameriusiiif;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import cz.rumanek.kramerius.krameriusiiif.config.AppConfiguration;
import cz.rumanek.kramerius.krameriusiiif.config.CorsFilter;
import cz.rumanek.kramerius.krameriusiiif.config.SolrConfig;
import de.digitalcollections.iiif.model.jackson.IiifObjectMapper;
import org.apache.solr.client.solrj.impl.BinaryResponseParser;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.request.RequestWriter;
import org.apache.solr.client.solrj.request.json.JsonQueryRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConfigTests {

    @Test
    public void corsTest() throws IOException, ServletException {
        FilterChain filterChain = new MockFilterChain();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setContentType("text/plain");
        CorsFilter corsFilter = new CorsFilter();
        corsFilter.doFilter(request, response, filterChain);
        assertThat(response.getHeader("Access-Control-Allow-Origin")).isEqualTo("*");
        assertThat(response.getHeader("Access-Control-Allow-Methods")).isEqualTo("GET, OPTIONS");
        assertThat(response.getHeader("Access-Control-Allow-Headers"))
                .isEqualTo("Origin, X-Requested-With, Content-Type, Accept");
    }

    static final String KRAMERIUS_SOLR_URL = "https://kramerius.mzk.cz/search/api/";

    @Test
    public void solrConfigExternalServerTest() {
        SolrConfig solrConfig = new SolrConfig();
        ReflectionTestUtils.setField(solrConfig, "solrEndpointUrl", KRAMERIUS_SOLR_URL);
        HttpSolrClient httpSolrClient = (HttpSolrClient) solrConfig.solrClient();
        assertThat(httpSolrClient.getParser()).as("Validate response parser").isInstanceOf(XMLResponseParser.class);
        RequestWriter requestWriter = (RequestWriter) ReflectionTestUtils.getField(httpSolrClient,"requestWriter");
        assertThat(requestWriter.getPath(new JsonQueryRequest())).isEqualTo("/search");
    }

    @Test
    public void solrConfigLocalServerTest() {
        SolrConfig solrConfig = new SolrConfig();
        ReflectionTestUtils.setField(solrConfig, "solrEndpointUrl", "///");
        HttpSolrClient httpSolrClient = (HttpSolrClient) solrConfig.solrClient();
        assertThat(httpSolrClient.getParser()).as("Validate response parser").isInstanceOf(BinaryResponseParser.class);
        RequestWriter requestWriter = (RequestWriter) ReflectionTestUtils.getField(httpSolrClient,"requestWriter");
        assertThat(requestWriter.getPath(new JsonQueryRequest())).isEqualTo("/select");
    }

    @Mock
    Environment env;

    @InjectMocks
    AppConfiguration appConfiguration;

    @Test
    public void iiifObjectMapperIndentTest() throws JsonProcessingException {
        when(env.getProperty("kramerius.prettyprint","DEFAULT")).thenReturn("YES");
        IiifObjectMapper iiifObjectMapper = appConfiguration.iiifObjectMapper();
        assertThat(iiifObjectMapper.isEnabled(SerializationFeature.INDENT_OUTPUT)).isTrue();
    }

    @Test
    public void iiifObjectMapperNoIndentTest() throws JsonProcessingException {
        when(env.getProperty("kramerius.prettyprint","DEFAULT")).thenReturn("NO");
        IiifObjectMapper iiifObjectMapper = appConfiguration.iiifObjectMapper();
        assertThat(iiifObjectMapper.isEnabled(SerializationFeature.INDENT_OUTPUT)).isFalse();
    }

}
