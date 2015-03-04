package no.saua.remock.unittests;

import no.saua.remock.Reject;
import no.saua.remock.ReplaceWithMock;
import no.saua.remock.exampleapplication.SomeService;
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

    @Reject(SomeService.class)
    public static class SomeTestClass {
        @ReplaceWithMock
        public SomeService test;
    }

    @Reject(SomeService.class)
    public static class SomeEqualTestClass {
        @ReplaceWithMock
        public SomeService test;
    }

    @Reject(SomeService.class)
    public static class SomeTestClassNotEqual {
    }

    public static class SomeTestClassNotEqual2 {
        @ReplaceWithMock
        public SomeService test;
    }

    public static class SomeSubTestClass extends SomeTestClassNotEqual {
        @ReplaceWithMock
        public SomeService test;
    }

    @Test
    public void testSubClass() {
        RemockTestClassAnnotationFinder testClassHandler5 = new RemockTestClassAnnotationFinder(SomeSubTestClass.class);
        assertEquals(2, testClassHandler5.getRejecters().size());
    }

    @Test
    public void testEquality() {
        RemockTestClassAnnotationFinder testClassHandler = new RemockTestClassAnnotationFinder(SomeTestClass.class);
        RemockTestClassAnnotationFinder testClassHandler2 =
                        new RemockTestClassAnnotationFinder(SomeEqualTestClass.class);
        RemockTestClassAnnotationFinder testClassHandler3 =
                        new RemockTestClassAnnotationFinder(SomeTestClassNotEqual.class);
        RemockTestClassAnnotationFinder testClassHandler4 =
                        new RemockTestClassAnnotationFinder(SomeTestClassNotEqual2.class);
        RemockTestClassAnnotationFinder testClassHandler5 = new RemockTestClassAnnotationFinder(SomeSubTestClass.class);

        assertEquals(testClassHandler, testClassHandler2);
        assertNotEquals(testClassHandler, testClassHandler3);
        assertNotEquals(testClassHandler, testClassHandler4);
        assertEquals(testClassHandler, testClassHandler5);

    }

}
