package no.saua.remock;

import no.saua.remock.exampleapplication.ConfigurationClass;
import no.saua.remock.exampleapplication.SomeService;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Enclosed.class)
public class ReplaceWithMockTest {

    @ReplaceWithMock(SomeService.class)
    @ContextConfiguration(classes = ConfigurationClass.class)
    public static class ReplaceWithMockClassTest extends CommonTest {

        @Inject
        public SomeService someService;

        @Test
        public void test() {
            assertFalse(someService.getClass().equals(SomeService.class));
            assertTrue(isMock(someService));
        }
    }

    @ContextConfiguration(classes = ConfigurationClass.class)
    public static class ReplaceWithMockFieldTest extends CommonTest {

        @ReplaceWithMock
        public SomeService someService;

        @Test
        public void test() {
            assertFalse(someService.getClass().equals(SomeService.class));
            assertTrue(isMock(someService));
        }
    }

}