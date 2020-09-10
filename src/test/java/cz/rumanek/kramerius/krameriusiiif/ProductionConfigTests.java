package cz.rumanek.kramerius.krameriusiiif;

import cz.rumanek.kramerius.krameriusiiif.config.FailFastSpringJUnit4Runner;
import cz.rumanek.kramerius.krameriusiiif.config.ServerProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

@RunWith(FailFastSpringJUnit4Runner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles(profiles = "production")
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)

public class ProductionConfigTests {

    @Autowired
    ServerProperties properties;

    @Test
    public void launchTest() throws MalformedURLException, URISyntaxException {
        //launches with no errors
    }
}
