package no.saua.remock;

import no.saua.remock.exampleapplication.AnInterface;
import no.saua.remock.exampleapplication.AnInterfaceImplOne;
import no.saua.remock.exampleapplication.AnInterfaceImplTwo;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;

import static org.junit.Assert.assertTrue;

@RunWith(Enclosed.class)
public class WrapWithSpyTest {

    /**
     * Wraps all implementations of AnInterface with spies.
     */
    @WrapWithSpy(AnInterface.class)
    @ContextConfiguration(classes = {AnInterfaceImplOne.class, AnInterfaceImplTwo.class})
    public static class SpyAllOfTypeTest extends CommonTest {

        @Inject
        private AnInterfaceImplOne one;

        @Inject
        private AnInterfaceImplTwo two;

        @Test
        public void test() {
            assertTrue(isSpy(one));
            assertTrue(isSpy(two));
        }
    }

    /**
     * Wraps the given classes with spies.
     */
    @WrapWithSpy({AnInterfaceImplOne.class, AnInterfaceImplTwo.class})
    @ContextConfiguration(classes = {AnInterfaceImplOne.class, AnInterfaceImplTwo.class})
    public static class SpyAllOfMultipleTypes extends CommonTest {

        @Inject
        private AnInterfaceImplOne one;

        @Inject
        private AnInterfaceImplTwo two;

        @Test
        public void test() {
            assertTrue(isSpy(one));
            assertTrue(isSpy(two));
        }
    }

    /**
     * Wraps the annotated fields with spies.
     */
    @ContextConfiguration(classes = {AnInterfaceImplOne.class, AnInterfaceImplTwo.class})
    public static class SpyFields extends CommonTest {

        @WrapWithSpy
        private AnInterfaceImplOne one;

        @WrapWithSpy
        private AnInterfaceImplTwo two;

        @Test
        public void test() {
            assertTrue(isSpy(one));
            assertTrue(isSpy(two));
        }
    }

    /**
     * Wraps the given bean names with spies.
     */
    @WrapWithSpy(beanNames = {"anInterfaceImplOne", "anInterfaceImplTwo"})
    @ContextConfiguration(classes = {AnInterfaceImplOne.class, AnInterfaceImplTwo.class})
    public static class SpyAllOfMultipleNamed extends CommonTest {

        @Inject
        private AnInterfaceImplOne one;

        @Inject
        private AnInterfaceImplTwo two;

        @Test
        public void test() {
            assertTrue(isSpy(one));
            assertTrue(isSpy(two));
        }
    }


    /**
     * Wraps multiple beans with spies.
     */
    @WrapWithSpy(AnInterfaceImplOne.class)
    @WrapWithSpy(beanNames = "anInterfaceImplTwo")
    @ContextConfiguration(classes = {AnInterfaceImplOne.class, AnInterfaceImplTwo.class})
    public static class SpyRepeatedAnnotation extends CommonTest {

        @Inject
        private AnInterfaceImplOne one;

        @Inject
        private AnInterfaceImplTwo two;

        @Test
        public void test() {
            assertTrue(isSpy(one));
            assertTrue(isSpy(two));
        }
    }
}
