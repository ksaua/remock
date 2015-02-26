package no.saua.remock;

import no.saua.remock.exampletests.application.AnInterfaceImplOne;
import no.saua.remock.exampletests.application.AnInterfaceImplTwo;
import no.saua.remock.exampletests.application.SomeService;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/**
 * Checks that Mocks and spies are reset after every test-method
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ContextConfiguration(classes = {SomeService.class, AnInterfaceImplOne.class, AnInterfaceImplTwo.class})
public class ResetTest extends CommonTest {

    @Inject
    @ReplaceWithMock
    public AnInterfaceImplOne mock;

    @Inject
    @ReplaceWithSpy(AnInterfaceImplTwo.class)
    public AnInterfaceImplTwo spy;

    @Test
    public void testA_DirtiesMocks() {
        assertEquals(null, mock.someMethod());
        when(mock.someMethod()).thenReturn("abc");
        assertEquals("abc", mock.someMethod());

        assertEquals("someMethodTwo", spy.someMethod());
        doReturn("xyz").when(spy).someMethod();
        assertEquals("xyz", spy.someMethod());
    }

    @Test
    public void testB_ChecksThatMocksAreNotDirty() {
        assertEquals(null, mock.someMethod());
        assertEquals("someMethodTwo", spy.someMethod());
    }
}
