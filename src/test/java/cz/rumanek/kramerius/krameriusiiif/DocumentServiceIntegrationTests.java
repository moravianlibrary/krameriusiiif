package cz.rumanek.kramerius.krameriusiiif;

import cz.rumanek.kramerius.krameriusiiif.model.DocumentEntity;
import cz.rumanek.kramerius.krameriusiiif.service.DocumentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

//TODO create tests separate for page, collection etc. with image info check
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DocumentServiceIntegrationTests {

	@Autowired
	DocumentService service;
	static final String VALID_PID = "uuid:44ad0636-62ab-4da3-9373-7e8cd0ad4f1b";
	static final String INVALID_PID = "uuid:40ad0636-62ab-4da3-9373-7e8cd0ad4f1c";
	static final String VALID_PARENT_PID = "uuid:6203552b-922b-425b-845a-2a7e1ee04c6c";
	static final String INVALID_PARENT_PID = "uuid:75893552b-922b-425b-845a-2a7e1ee04c7c";

	@Test
	public void findByValidPid() {
		Optional<DocumentEntity> result = service.findByPid(VALID_PID);
		assertThat(result).isPresent();
	}

	@Test
	public void findByInvalidPid() {
		Optional<DocumentEntity> result = service.findByPid(INVALID_PID);
		assertThat(result).isNotPresent();
	}

	@Test
	public void findByValidParentPid() {
		Stream<DocumentEntity> result = service.findAllByParentPid(VALID_PARENT_PID);
		assertThat(result).size().isGreaterThan(0);
	}

	@Test
	public void findByInvalidParentPid() {
		Stream<DocumentEntity> result = service.findAllByParentPid(INVALID_PARENT_PID);
		assertThat(result).size().isEqualTo(0);
	}
}
