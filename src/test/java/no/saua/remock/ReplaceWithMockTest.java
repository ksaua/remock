package no.saua.remock;

import no.saua.remock.exampleapplication.ConfigurationClass;
import no.saua.remock.exampleapplication.SomeService;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@ContextConfiguration(classes = ConfigurationClass.class)
public class ReplaceWithMockTest extends CommonTest {

    @ReplaceWithMock
    public SomeService someService;

    @Test
    public void test() {
        assertFalse(someService.getClass().equals(SomeService.class));
        assertTrue(isMock(someService));
    }
}
