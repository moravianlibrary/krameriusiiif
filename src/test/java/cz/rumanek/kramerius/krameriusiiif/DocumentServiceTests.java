package cz.rumanek.kramerius.krameriusiiif;

import cz.rumanek.kramerius.krameriusiiif.config.FailFastSpringJUnit4Runner;
import cz.rumanek.kramerius.krameriusiiif.model.DocumentEntity;
import cz.rumanek.kramerius.krameriusiiif.model.KDocument;
import cz.rumanek.kramerius.krameriusiiif.model.MappingUtil;
import cz.rumanek.kramerius.krameriusiiif.repository.DocumentRepository;
import cz.rumanek.kramerius.krameriusiiif.repository.ImageInfoRepository;
import cz.rumanek.kramerius.krameriusiiif.service.DocumentService;
import org.assertj.core.api.Condition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.query.result.SolrResultPage;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

//TODO create tests separate for page, collection etc. with image info check
@RunWith(FailFastSpringJUnit4Runner.class)
//@SpringBootTest
@TestExecutionListeners({MockitoTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
public class DocumentServiceTests {

    @Configuration
    @ComponentScan(basePackageClasses = {DocumentService.class, MappingUtil.class})
    public static class SpringConfig {
    }

    @Autowired
    private DocumentService service;

    @MockBean
    private DocumentRepository repository;

    @MockBean
    private ImageInfoRepository imageRepository;

    static final String VALID_PID = "uuid:44ad0636-62ab-4da3-9373-7e8cd0ad4f1b";
    static final String INVALID_PID = "uuid:xxxxxxxx-62ab-4da3-9373-7e8cd0ad4f1c";
    static final String VALID_PARENT_PID = "uuid:6203552b-922b-425b-845a-2a7e1ee04c6c";
    static final String INVALID_PARENT_PID = "uuid:xxxxxxxx-922b-425b-845a-2a7e1ee04c7c";

    @Test
    public void findByValidPid() {
        KDocument document = new KDocument();
        ReflectionTestUtils.setField(document, "model", "page");
        given(repository.findByPid(VALID_PID)).willReturn(Optional.of(document));
        Optional<DocumentEntity> result = service.findByPid(VALID_PID);
        assertThat(result).isPresent();
        assertThat(result.get()).is(new Condition<>(DocumentEntity::isPage, "Document is page"));
    }

    @Test
    public void findByInvalidPid() {
        given(repository.findByPid(INVALID_PID)).willReturn(Optional.empty());
        Optional<DocumentEntity> result = service.findByPid(INVALID_PID);
        assertThat(result).isNotPresent();
    }

    @Test
    public void findByValidParentPid() {
        List<String> pidList = Arrays.asList("12345", "23456", "34567", "45678");
        List<Integer> relsIndexes = Arrays.asList(3, 1, 4, 2);
        List<KDocument> documents = generateDocumentList(pidList, relsIndexes);
        final Map<String, Integer> pidToRelsExt = new HashMap<>();
        documents.forEach(doc -> pidToRelsExt.put(doc.getPid(), doc.getRelsIndex()));
        given(repository.findByParentPid(eq(VALID_PARENT_PID), any())).willReturn(new SolrResultPage<>(documents));

        List<DocumentEntity> resultList = service.findAllByParentPid(VALID_PARENT_PID).collect(Collectors.toList());
        assertThat(resultList).size().isEqualTo(documents.size());
        assertThat(resultList).are(new Condition<>(DocumentEntity::isPage, "Documents are page"));
        assertThat(resultList).isSortedAccordingTo(Comparator.comparingInt(doc -> pidToRelsExt.get(doc.getPid())));
        assertThat(resultList).extracting(DocumentEntity::getPid).containsOnlyElementsOf(pidList);
    }

    @Test
    public void findByInvalidParentPid() {
        given(repository.findByParentPid(eq(INVALID_PARENT_PID), any())).willReturn(new SolrResultPage<>(new ArrayList<>()));
        Stream<DocumentEntity> result = service.findAllByParentPid(INVALID_PARENT_PID);
        assertThat(result).isEmpty();
    }

    private KDocument generateDocument(String pid, Integer index) {
        KDocument document = new KDocument();
        ReflectionTestUtils.setField(document, "pid", pid);
        ReflectionTestUtils.setField(document, "label", String.valueOf(index));
        ReflectionTestUtils.setField(document, "model", "page");
        ReflectionTestUtils.setField(document, "relsIndex", new ArrayList<Integer>() {{
            add(index);
        }});
        return document;
    }

    private List<KDocument> generateDocumentList(List<String> pids, List<Integer> relsIndexes) {
        List<KDocument> documents = new ArrayList<>();
        for (int i = 0; i < pids.size(); i++) {
            documents.add(generateDocument(pids.get(i), relsIndexes.get(i)));
        }
        return documents;
    }
}
