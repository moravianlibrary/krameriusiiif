package cz.rumanek.kramerius.krameriusiiif;

import cz.rumanek.kramerius.krameriusiiif.config.Constants;
import cz.rumanek.kramerius.krameriusiiif.config.FailFastSpringJUnit4Runner;
import cz.rumanek.kramerius.krameriusiiif.config.TestContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(FailFastSpringJUnit4Runner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = TestContext.class)
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
public class CompleteIntegrationTests {

    static final String MANIFEST_URL = "uuid:6203552b-922b-425b-845a-2a7e1ee04c6c/manifest";
    static final String INVALID_MANIFEST_URL = "uuid:xxxxxxxx-914b-425b-845a-2a7e1ee04c6c/manifest";
    static final String COLLECTION_URL = "uuid:5a2dd690-54b9-11de-8bcd-000d606f5dc6/collection";
    static final String INVALID_COLLECTION_URL = "uuid:xxxxxxxx-54b9-11de-8bcd-111d606f5dc6/collection";
    static final String ROOT_PAGE_FILE = "Root.json";
    static final String MANIFEST_FILE = "Manifest.json";
    static final String COLLECTION_FILE = "Collection.json";
    static final String FILE_ENCODING = "UTF-8";

    @Autowired
    private MockMvc mockMvc;

    @Value("${server.servlet.context-path:/iiif}")
    private String contextPath;

    @Autowired
    @Qualifier(Constants.SERVER_BASEURL)
    private String baseUrl;

    @Autowired
    @Qualifier(Constants.IIIF_ENDPOINT)
    private String iiiEndpointUrl;

    public CompleteIntegrationTests() {
    }

    @Test
    public void rootTest() throws Exception {
        MvcResult result = mockMvc.perform(get("/"))
                //.andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        compareResponseToFile(result.getResponse(), ROOT_PAGE_FILE);
    }

    static int iteration = 0;
    @Test
    //@Repeat(150)
    @Repeat(10)

    public void manifestTest() throws Exception {
        Logger logger = LoggerFactory.getLogger(CompleteIntegrationTests.class);
        long start = System.currentTimeMillis();
        iteration++;
        MvcResult result = mockMvc.perform(get("/" + MANIFEST_URL))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.@id",
                        is(baseUrl + MANIFEST_URL)))
                .andExpect(jsonPath("$.sequences[0].canvases[0].images[*].resource.@id",
                        everyItem(startsWith(iiiEndpointUrl))))
                .andExpect(jsonPath("$.sequences[0].canvases[0].images[*].resource.service.@id",
                        everyItem(startsWith(iiiEndpointUrl))))
                .andReturn();

        compareResponseToFile(result.getResponse(), MANIFEST_FILE);
        logger.info("Manifest test - iteration" + iteration + " " + (System.currentTimeMillis() - start));
    }

    @Test
    public void invalidManifestUuidTest() throws Exception {
        mockMvc.perform(get("/" + INVALID_MANIFEST_URL))
                .andExpect(status().isNotFound());
    }

    @Test
    public void collectionTest() throws Exception {
        MvcResult result = mockMvc.perform(get("/" + COLLECTION_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.@id",
                        is(baseUrl + COLLECTION_URL)))
                .andExpect(jsonPath("$.collections[*].@id",
                        everyItem(allOf(startsWith(baseUrl), endsWith("/collection")))))
                .andReturn();
        compareResponseToFile(result.getResponse(), COLLECTION_FILE);
    }

    @Test
    public void invalidCollectionUuidTest() throws Exception {
        mockMvc.perform(get("/" + INVALID_COLLECTION_URL))
                .andExpect(status().isNotFound());
    }

    @Test
    public void tryPost() throws Exception {
        assertRequestIsForbidden(HttpMethod.POST);
    }

    @Test
    public void tryPut() throws Exception {
        assertRequestIsForbidden(HttpMethod.PUT);
    }

    @Test
    public void tryDelete() throws Exception {
        assertRequestIsForbidden(HttpMethod.DELETE);
    }

    private void assertRequestIsForbidden(HttpMethod method) throws Exception {
        mockMvc.perform(request(method, "/" + COLLECTION_URL))
                .andExpect(status().isForbidden());
    }

    /**
     * Stripping actual host and port for comparing content
     */
    private String removeHostAndPort(String manifest, String sourceContextPath) {
        return manifest.replaceAll("(http|https)://.*" + sourceContextPath + "/", "http://HOST:PORT/CONTEXT/");
    }

    private void compareResponseToFile(MockHttpServletResponse response, String file) throws IOException {
        compareResponseToFile(response.getContentAsString(Charset.forName(FILE_ENCODING)), file);
    }

    private void compareResponseToFile(String responseBody, String file) throws IOException {
        Resource resource = new DefaultResourceLoader().getResource(file);
        String savedFile = StreamUtils.copyToString(resource.getInputStream(), Charset.forName(FILE_ENCODING));
        assertThat(removeHostAndPort(responseBody, contextPath)).isEqualTo(removeHostAndPort(savedFile, "/iiif"));
    }
}
