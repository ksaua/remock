package no.saua.remock;

import static org.junit.Assert.*;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import no.saua.remock.ContextCacheTest.SomeTestClass;
import no.saua.remock.ContextCacheTest.SomeTestClassEqual;
import no.saua.remock.ContextCacheTest.SomeTestClassNotEqual0;
import no.saua.remock.ContextCacheTest.SomeTestClassNotEqual1;
import no.saua.remock.ContextCacheTest.SomeTestClassNotEqual2;
import no.saua.remock.ContextCacheTest.SomeTestClassNotEqual3;
import no.saua.remock.exampleapplication.AnInterface;
import no.saua.remock.exampleapplication.AnInterfaceImplTwo;
import no.saua.remock.exampleapplication.SomeService;
import no.saua.remock.exampleapplication.SomeServiceWithDependencies;

/**
 * Tests various forms of cache scenarios. Note that the ordering in the @SuiteClasses is significant.
 */
@RunWith(Suite.class)
@SuiteClasses({SomeTestClass.class, SomeTestClassEqual.class, SomeTestClassNotEqual0.class,
                SomeTestClassNotEqual1.class, SomeTestClassNotEqual2.class, SomeTestClassNotEqual3.class})
public class ContextCacheTest {

    private static ApplicationContext cachedSpringContextWithRejectAndSpy;
    private static ApplicationContext cachedSpringContextWithReject;
    private static ApplicationContext cachedSpringContextWithSpy;
    private static ApplicationContext cachedSpringContextWithImpl;


    // :: The context of this test will be cached
    @ContextConfiguration(classes = SomeService.class)
    @Reject(SomeServiceWithDependencies.class)
    @WrapWithSpy(AnInterface.class)
    @ReplaceWithImpl(value = AnInterface.class, with= AnInterfaceImplTwo.class)
    public static class SomeTestClass extends CommonTest {

        @Inject
        private ApplicationContext springContext;

        @Test
        public void test() {
            assertNotNull(springContext);
            cachedSpringContextWithRejectAndSpy = springContext;
        }
    }

    // :: This test should use same spring context as previous test.
    @ContextConfiguration(classes = SomeService.class)
    @Reject(SomeServiceWithDependencies.class)
    @WrapWithSpy(AnInterface.class)
    @ReplaceWithImpl(value = AnInterface.class, with= AnInterfaceImplTwo.class)
    public static class SomeTestClassEqual extends CommonTest {

        @Inject
        private ApplicationContext springContext;

        @Test
        public void test() {
            assertNotNull(springContext);
            assertTrue("Should get the exact same spring context", cachedSpringContextWithRejectAndSpy ==
                    springContext);
        }
    }

    // :: This test should NOT use same spring context as previous test.
    @EagerlyInitialized
    @ContextConfiguration(classes = SomeService.class)
    @Reject(SomeServiceWithDependencies.class)
    @WrapWithSpy(AnInterface.class)
    @ReplaceWithImpl(value = AnInterface.class, with= AnInterfaceImplTwo.class)
    public static class SomeTestClassNotEqual0 extends CommonTest {

        @Inject
        private ApplicationContext springContext;

        @Test
        public void test() {
            assertNotNull(springContext);
            assertNotNull(cachedSpringContextWithRejectAndSpy);
            assertNotEquals("Should NOT get the exact same spring context", cachedSpringContextWithRejectAndSpy,
                            springContext);
        }
    }

    // :: This test should NOT use same spring context as first test.
    @ContextConfiguration(classes = SomeService.class)
    @Reject(SomeServiceWithDependencies.class)
    public static class SomeTestClassNotEqual1 extends CommonTest {

        @Inject
        private ApplicationContext springContext;

        @Test
        public void test() {
            assertNotNull(springContext);
            assertNotEquals("Should get different spring context", cachedSpringContextWithRejectAndSpy, springContext);
            cachedSpringContextWithReject = springContext;
        }
    }

    // :: This test should NOT use same spring context as first test.
    @ContextConfiguration(classes = SomeService.class)
    @WrapWithSpy(AnInterface.class)
    public static class SomeTestClassNotEqual2 extends CommonTest {

        @Inject
        private ApplicationContext springContext;

        @Test
        public void test() {
            assertNotNull(springContext);
            assertNotEquals("Should get different spring context", cachedSpringContextWithRejectAndSpy, springContext);
            assertNotEquals("Should get different spring context", cachedSpringContextWithReject, springContext);
            cachedSpringContextWithSpy = springContext;
        }
    }

    // :: This test should NOT use same spring context as first test.
    @ContextConfiguration(classes = SomeService.class)
    @ReplaceWithImpl(value = AnInterface.class, with= AnInterfaceImplTwo.class)
    public static class SomeTestClassNotEqual3 extends CommonTest {

        @Inject
        private ApplicationContext springContext;;

        @Test
        public void test() {
            assertNotNull(springContext);
            assertNotEquals("Should get different spring context", cachedSpringContextWithRejectAndSpy, springContext);
            assertNotEquals("Should get different spring context", cachedSpringContextWithReject, springContext);
            cachedSpringContextWithImpl = springContext;
        }
    }

    // :: This test class does not rejects something, thus it should get a fresh spring context
    @ContextConfiguration(classes = SomeService.class)
    public static class SomeTestClassNotEqual4 extends CommonTest {

        @Inject
        private ApplicationContext springContext;

        @Test
        public void test() {
            assertNotNull(springContext);
            assertNotEquals("Should get different spring context", cachedSpringContextWithRejectAndSpy, springContext);
            assertNotEquals("Should get different spring context", cachedSpringContextWithImpl, springContext);
            assertNotEquals("Should get different spring context", cachedSpringContextWithReject, springContext);
            assertNotEquals("Should get different spring context", cachedSpringContextWithSpy, springContext);
        }
    }
}
