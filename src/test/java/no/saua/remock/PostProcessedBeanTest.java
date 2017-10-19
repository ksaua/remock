package no.saua.remock;

import no.saua.remock.exampleapplication.SomeService;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertTrue;

/**
 * Verifies that Spring does NOT post process any mocked classes.
 */
@ContextConfiguration(classes = {PostProcessedBeanTest.ABeanPostProcessor.class, SomeService.class})
public class PostProcessedBeanTest extends CommonTest {

    @ReplaceWithMock
    private SomeService someService;

    @Test
    public void test() {
        assertTrue(isMock(someService));
    }

    @Component
    public static class ABeanPostProcessor implements BeanPostProcessor {
        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof FactoryBean) {
                if (SomeService.class.isAssignableFrom(((FactoryBean) bean).getObjectType())) {
                    throw new AssertionError("Should never happen");
                }
            }
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof FactoryBean) {
                if (SomeService.class.isAssignableFrom(((FactoryBean) bean).getObjectType())) {
                    throw new AssertionError("Should never happen");
                }
            }
            return bean;
        }
    }
}
