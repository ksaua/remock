package no.saua.remock;

import no.saua.remock.exampleapplication.AnInterfaceImplOne;
import no.saua.remock.exampleapplication.AnInterfaceImplTwo;
import no.saua.remock.exampleapplication.ExampleFactoryBean;
import no.saua.remock.exampleapplication.SomeService;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/**
 * Checks that Mocks and spies are reset after every test-method. Note that the ordering of the test are significant.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ContextConfiguration(classes = {SomeService.class, AnInterfaceImplOne.class, AnInterfaceImplTwo.class})
public class ResetTest extends CommonTest {

    @ReplaceWithMock
    public AnInterfaceImplOne mock;

    @WrapWithSpy(AnInterfaceImplTwo.class)
    public AnInterfaceImplTwo spy;

    @ReplaceWithMock
    public ExampleFactoryBean.BeanFromFactoryBean mockOfBeanFromFactoryBean;

    @Test
    public void testA_DirtiesMocks() {
        assertEquals(null, mock.someMethod());
        when(mock.someMethod()).thenReturn("abc");
        assertEquals("abc", mock.someMethod());

        assertEquals("someMethodTwo", spy.someMethod());
        doReturn("xyz").when(spy).someMethod();
        assertEquals("xyz", spy.someMethod());

        assertEquals(null, mockOfBeanFromFactoryBean.someMethod());
        when(mockOfBeanFromFactoryBean.someMethod()).thenReturn("mocked");
        assertEquals("mocked", mockOfBeanFromFactoryBean.someMethod());
    }

    @Test
    public void testB_ChecksThatMocksAreNotDirty() {
        assertEquals(null, mock.someMethod());
        assertEquals("someMethodTwo", spy.someMethod());
        assertEquals(null, mockOfBeanFromFactoryBean.someMethod());
    }
}
