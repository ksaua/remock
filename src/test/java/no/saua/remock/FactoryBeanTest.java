package no.saua.remock;

import no.saua.remock.exampleapplication.SomeServiceWithMethod;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;

@ContextConfiguration(classes = {FactoryBeanTest.TestFactoryBean.class})
public class FactoryBeanTest extends CommonTest {

    @ReplaceWithMock
    private SomeServiceWithMethod someServiceWithMethod;

    @Inject
    private ApplicationContext applicationContext;

    @Test
    public void testAOP() {
        String result = "mocked!";
        Mockito.when(someServiceWithMethod.callMethod()).thenReturn(result);
        assertEquals(result, someServiceWithMethod.callMethod());

        // Assert that only of them exists in the application context.
        assertEquals(1, applicationContext.getBeansOfType(SomeServiceWithMethod.class).size());
    }

    public static class TestFactoryBean implements FactoryBean<SomeServiceWithMethod> {

        public SomeServiceWithMethod getObject() throws Exception {
            throw new AssertionError("Should never happen");
        }

        public Class<?> getObjectType() {
            return SomeServiceWithMethod.class;
        }

        public boolean isSingleton() {
            return true;
        }

    }
}
