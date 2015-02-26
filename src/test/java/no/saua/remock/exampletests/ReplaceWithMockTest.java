package no.saua.remock.exampletests;

import no.saua.remock.CommonTest;
import no.saua.remock.ReplaceWithMock;
import no.saua.remock.exampletests.application.ConfigurationClass;
import no.saua.remock.exampletests.application.SomeService;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@ContextConfiguration(classes = ConfigurationClass.class)
public class ReplaceWithMockTest extends CommonTest {

    @ReplaceWithMock
    @Inject
    public SomeService someService;

    @Test
    public void test() {
        assertFalse(someService.getClass().equals(SomeService.class));
        assertTrue(isMock(someService));
    }
}
