package no.saua.remock;

import no.saua.remock.exampletests.CommonTest;
import no.saua.remock.exampletests.application.SomeService;
import no.saua.remock.exampletests.application.SomeServiceWithDependencies;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static org.junit.Assert.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({ContextCacheTest.SomeTestClass.class, ContextCacheTest.SomeTestClassEqual.class, ContextCacheTest.SomeTestClassWithDifferentContext.class})
public class ContextCacheTest {

    private static ApplicationContext cachedSpringContext;

    // :: The context of this test will be cached
    @RunWith(SpringJUnit4ClassRunner.class)
    @BootstrapWith(RemockBootstrapper.class)
    @ContextConfiguration(classes = SomeService.class)
    @Reject(SomeServiceWithDependencies.class)
    public static class SomeTestClass extends CommonTest {

        @Inject
        private ApplicationContext springContext;

        @Test
        public void test() {
            assertNotNull(springContext);
            cachedSpringContext = springContext;
        }
    }

    // :: This test should use same spring context as previous test.
    @ContextConfiguration(classes = SomeService.class)
    @Reject(SomeServiceWithDependencies.class)
    public static class SomeTestClassEqual extends CommonTest {

        @Inject
        private ApplicationContext springContext;

        @Test
        public void test() {
            assertEquals("Should get the exact same spring context", cachedSpringContext, springContext);
        }
    }

    // :: This test class rejects something, thus it should get a fresh spring context
    @RunWith(SpringJUnit4ClassRunner.class)
    @BootstrapWith(RemockBootstrapper.class)
    @ContextConfiguration(classes = SomeService.class)
    public static class SomeTestClassWithDifferentContext extends CommonTest {

        @Inject
        private ApplicationContext springContext;

        @Test
        public void test() {
            assertNotNull(springContext);
            assertNotEquals("Should get different spring context", cachedSpringContext, springContext);
        }
    }


}
