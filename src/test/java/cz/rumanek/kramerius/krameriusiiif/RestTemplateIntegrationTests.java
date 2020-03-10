package cz.rumanek.kramerius.krameriusiiif;

import com.fasterxml.jackson.databind.SerializationFeature;
import de.digitalcollections.iiif.model.jackson.IiifObjectMapper;
import de.digitalcollections.iiif.model.sharedcanvas.Collection;
import de.digitalcollections.iiif.model.sharedcanvas.Manifest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StreamUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;

//TODO validate baseURL in requests
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestTemplateIntegrationTests {

    static final String MANIFEST_URL = "/uuid:6203552b-922b-425b-845a-2a7e1ee04c6c/manifest";
    static final String INVALID_MANIFEST_URL = "/uuid:6303552b-914b-425b-845a-2a7e1ee04c6c/manifest";
    static final String COLLECTION_URL = "/uuid:5a2dd690-54b9-11de-8bcd-000d606f5dc6/collection";
    static final String INVALID_COLLECTION_URL = "/uuid:5a2dd123-54b9-11de-8bcd-111d606f5dc6/collection";
    static final String ROOT_PAGE_FILE = "Root.json";
    static final String MANIFEST_FILE = "Manifest.json";
    static final String COLLECTION_FILE = "Collection.json";
    static final String FILE_ENCODING = "UTF-8";

    @Resource
    Environment env;

    @Autowired
    private TestRestTemplate restTemplate;

    IiifObjectMapper objectMapper;

    String contextPath;

    public RestTemplateIntegrationTests() {
        objectMapper = new IiifObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Before
    public void init() {
        contextPath = env.getProperty("server.servlet.context-path");
    }

    @Test
    public void rootTest() throws IOException {
        ResponseEntity<Collection> responseEntity = restTemplate.getForEntity("/", Collection.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        compareResponseAndFileInResources(responseEntity, ROOT_PAGE_FILE);
    }

    @Test
    public void manifestTest() throws IOException {
        ResponseEntity<Manifest> responseEntity = restTemplate.getForEntity(MANIFEST_URL, Manifest.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        compareResponseAndFileInResources(responseEntity, MANIFEST_FILE);
    }

    @Test
    public void invalidManifestUuidTest() {
        ResponseEntity<Manifest> responseEntity = restTemplate.getForEntity(INVALID_MANIFEST_URL, Manifest.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(null);
    }

    @Test
    public void collectionTest() throws IOException {
        ResponseEntity<Collection> responseEntity = restTemplate.getForEntity(COLLECTION_URL, Collection.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        compareResponseAndFileInResources(responseEntity, COLLECTION_FILE);
    }

    @Test
    public void invalidCollectionUuidTest() {
        ResponseEntity<Collection> responseEntity = restTemplate.getForEntity(INVALID_COLLECTION_URL, Collection.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(null);
    }

    private void compareResponseAndFileInResources(ResponseEntity<?> entity, String file) throws IOException {
        String responseBody = objectMapper.writeValueAsString(entity.getBody());
        org.springframework.core.io.Resource resource = new DefaultResourceLoader().getResource(file);
        String savedFile = StreamUtils.copyToString(resource.getInputStream(), Charset.forName(FILE_ENCODING));
        assertThat(removeHostAndPort(responseBody, contextPath)).isEqualTo(removeHostAndPort(savedFile,"/iiif"));
    }

    @Test
    public void tryPost() {
        assertRequestIsForbidden(HttpMethod.POST);
    }

    @Test
    public void tryPut() {
        assertRequestIsForbidden(HttpMethod.PUT);
    }

    @Test
    public void tryDelete() {
        assertRequestIsForbidden(HttpMethod.DELETE);
    }

    private void assertRequestIsForbidden(HttpMethod method) {
        ResponseEntity<String> responseEntity = restTemplate.exchange(COLLECTION_URL, method,null,String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
    /**
     * Stripping actual host and port for comparing content
     */
    private static String removeHostAndPort(String manifest, String sourceContextPath) {
        return  manifest.replaceAll("(http|https)://.*"+ sourceContextPath + "/","http://host:port/iiif/");
    }
}
