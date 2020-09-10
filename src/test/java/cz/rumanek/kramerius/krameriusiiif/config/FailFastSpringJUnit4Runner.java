package cz.rumanek.kramerius.krameriusiiif.config;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * When error in test class initialization - <b>ignore(skip) all tests</b><br>
 * (for example when failing to load ApplicationContext)<br><br>
 * Integration test should also filter automatically added test execution listeners by:<br>
 * {@code @TestExecutionListeners(DependencyInjectionTestExecutionListener.class)}<br>
 * so that init error is not reported twice (due to multiple autoconfigured listeners)<br><br>
 *
 */
public class FailFastSpringJUnit4Runner extends SpringJUnit4ClassRunner {

    private boolean failAll;

    public FailFastSpringJUnit4Runner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    @Override
    protected Object createTest() throws Exception {
        try {
            return super.createTest();
        }
        catch (Throwable e) {
            System.out.println("TEST INIT FAILED! " + e);
            System.out.println("Cause:" + e.getCause());
            failAll = true;
            throw e;
        }
    }

    @Override
    protected boolean isTestMethodIgnored(FrameworkMethod frameworkMethod) {
        return super.isTestMethodIgnored(frameworkMethod) || failAll;
    }

}
