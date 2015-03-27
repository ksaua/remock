package no.saua.remock.unittests;

import no.saua.remock.Reject;
import no.saua.remock.RemockContextConfiguration;
import no.saua.remock.ReplaceWithMock;
import no.saua.remock.exampleapplication.AnInterface;
import no.saua.remock.exampleapplication.SomeService;
import no.saua.remock.internal.RemockAnnotationFinder;
import no.saua.remock.internal.RemockAnnotationFinder.RemockAnnotations;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class RemockAnnotationFinderTest {

    @Reject(SomeService.class)
    public static class SomeTestClass {
        @ReplaceWithMock
        public AnInterface test;
    }

    @Test
    public void test() {
        RemockAnnotations testClassHandler = RemockAnnotationFinder.findFor(SomeTestClass.class);
        assertEquals(2, testClassHandler.getRejecters().size());
    }

    @Reject(SomeService.class)
    public static class SomeEqualTestClass {
        @ReplaceWithMock
        public AnInterface test;
    }

    @Reject(SomeService.class)
    public static class SomeTestClassNotEqual {
    }

    public static class SomeTestClassNotEqual2 {
        @ReplaceWithMock
        public AnInterface test;
    }

    public static class SomeSubTestClass extends SomeTestClassNotEqual {
        @ReplaceWithMock
        public AnInterface test;
    }

    @RemockContextConfiguration(SomeTestClassNotEqual.class)
    public static class SomeTestClassWithRemockContextConfiguration {
        @ReplaceWithMock
        public AnInterface test;
    }

    @Test
    public void testSubClass() {
        RemockAnnotations testClassHandler5 = RemockAnnotationFinder.findFor(SomeSubTestClass.class);
        assertEquals(2, testClassHandler5.getRejecters().size());
    }

    @Test
    public void testEquality() {
        RemockAnnotations testClassHandler = RemockAnnotationFinder.findFor(SomeTestClass.class);
        RemockAnnotations testClassHandler2 = RemockAnnotationFinder.findFor(SomeEqualTestClass.class);
        RemockAnnotations testClassHandler3 = RemockAnnotationFinder.findFor(SomeTestClassNotEqual.class);
        RemockAnnotations testClassHandler4 = RemockAnnotationFinder.findFor(SomeTestClassNotEqual2.class);
        RemockAnnotations testClassHandler5 = RemockAnnotationFinder.findFor(SomeSubTestClass.class);
        RemockAnnotations testClassHandler6 =
                        RemockAnnotationFinder.findFor(SomeTestClassWithRemockContextConfiguration.class);

        assertEquals(testClassHandler, testClassHandler2);
        assertNotEquals(testClassHandler, testClassHandler3);
        assertNotEquals(testClassHandler, testClassHandler4);
        assertEquals(testClassHandler, testClassHandler5);
        assertEquals(testClassHandler, testClassHandler6);
    }

    @Test
    public void testCache() {
        RemockAnnotations first = RemockAnnotationFinder.findFor(SomeTestClass.class);
        RemockAnnotations second = RemockAnnotationFinder.findFor(SomeTestClass.class);
        assertTrue(first == second);
    }
}
