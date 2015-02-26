package no.saua.remock.unittests;

import no.saua.remock.Reject;
import no.saua.remock.ReplaceWithMock;
import no.saua.remock.internal.RemockTestClassAnnotationFinder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class RemockTestClassAnnotationFinderTest {
    @Test
    public void test() {
        RemockTestClassAnnotationFinder testClassHandler = new RemockTestClassAnnotationFinder(SomeTestClass.class);
        assertEquals(2, testClassHandler.getRejecters().size());
    }

    @Reject(SomeTestClass.class)
    public static class SomeTestClass {
        @ReplaceWithMock
        public SomeTestClass test;
    }

    @Reject(SomeTestClass.class)
    public static class SomeEqualTestClass {
        @ReplaceWithMock
        public SomeTestClass test;
    }

    @Reject(SomeTestClass.class)
    public static class SomeTestClassNotEqual {
    }

    public static class SomeTestClassNotEqual2 {
        @ReplaceWithMock
        public SomeTestClass test;
    }

    @Test
    public void testEquality() {
        RemockTestClassAnnotationFinder testClassHandler = new RemockTestClassAnnotationFinder(SomeTestClass.class);
        RemockTestClassAnnotationFinder testClassHandler2 = new RemockTestClassAnnotationFinder(SomeEqualTestClass.class);
        assertEquals(testClassHandler, testClassHandler2);

        RemockTestClassAnnotationFinder testClassHandler3 = new RemockTestClassAnnotationFinder(SomeTestClassNotEqual.class);
        assertNotEquals(testClassHandler, testClassHandler3);

        RemockTestClassAnnotationFinder testClassHandler4 = new RemockTestClassAnnotationFinder(SomeTestClassNotEqual2
                .class);
        assertNotEquals(testClassHandler, testClassHandler4);
    }

}
