package cz.rumanek.kramerius.krameriusiiif;

import cz.rumanek.kramerius.krameriusiiif.config.FailFastSpringJUnit4Runner;
import cz.rumanek.kramerius.krameriusiiif.config.TestContext;
import cz.rumanek.kramerius.krameriusiiif.model.Info;
import cz.rumanek.kramerius.krameriusiiif.repository.ImageInfoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(FailFastSpringJUnit4Runner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = TestContext.class)
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
public class ImageRepositoryIntegrationTests {

    @Autowired
    private ImageInfoRepository imageRepository;
    static final String VALID_IMAGE_PID = "uuid:c3dc5a1d-32d8-42fe-9cc4-b4aa38b6a3dd";
    static final String FORBIDDEN_IMAGE_PID = "uuid:c1bc5a1d-32d8-42fe-9cc4-b4aa38b6a3dd";
    static final String INVALID_IMAGE_PID = "uuid:xxxxxxxx-32d8-42fe-9cc4-b4aa38b6a3dd";
    static final String IMAGE_SERVER_URL = "https://kramerius.mzk.cz/search/iiif/";

    @Test
    public void validImagePidTest(){
        Info imageInfo = imageRepository.getInfo(VALID_IMAGE_PID);
        assertThat(imageInfo).isNotNull();
        assertThat(imageInfo.getHeight()).isGreaterThan(0);
        assertThat(imageInfo.getWidth()).isGreaterThan(0);
    }

    @Test
    public void invalidImagePidTest(){
        Info imageInfo = imageRepository.getInfo(INVALID_IMAGE_PID);
        assertThat(imageInfo).isNull();
    }
}
