package no.saua.remock.internal;


import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

/**
 * Resets all spies and mocks on every test
 */
public class RemockTestExecutionListener extends AbstractTestExecutionListener {

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        ApplicationContext applicationContext = testContext.getApplicationContext();
        for (Resettable resettable : applicationContext.getBeansOfType(Resettable.class).values()) {
            resettable.reset();
        }
    }

}
