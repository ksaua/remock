package no.saua.remock.unittests;

import no.saua.remock.Reject;
import no.saua.remock.RemockContextConfiguration;
import no.saua.remock.ReplaceWithMock;
import no.saua.remock.exampleapplication.AnInterface;
import no.saua.remock.exampleapplication.SomeService;
import no.saua.remock.internal.RemockConfiguration;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class RemockConfigurationTest {

    @Reject(SomeService.class)
    public static class SomeTestClass {
        @ReplaceWithMock
        public AnInterface test;
    }

    @Test
    public void test() {
        RemockConfiguration testClassHandler = RemockConfiguration.findFor(SomeTestClass.class);
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
        RemockConfiguration testClassHandler5 = RemockConfiguration.findFor(SomeSubTestClass.class);
        assertEquals(2, testClassHandler5.getRejecters().size());
    }

    @Test
    public void testEquality() {
        RemockConfiguration testClassHandler = RemockConfiguration.findFor(SomeTestClass.class);
        RemockConfiguration testClassHandler2 = RemockConfiguration.findFor(SomeEqualTestClass.class);
        RemockConfiguration testClassHandler3 = RemockConfiguration.findFor(SomeTestClassNotEqual.class);
        RemockConfiguration testClassHandler4 = RemockConfiguration.findFor(SomeTestClassNotEqual2.class);
        RemockConfiguration testClassHandler5 = RemockConfiguration.findFor(SomeSubTestClass.class);
        RemockConfiguration testClassHandler6 =
                RemockConfiguration.findFor(SomeTestClassWithRemockContextConfiguration.class);

        assertEquals(testClassHandler, testClassHandler2);
        assertNotEquals(testClassHandler, testClassHandler3);
        assertNotEquals(testClassHandler, testClassHandler4);
        assertEquals(testClassHandler, testClassHandler5);
        assertEquals(testClassHandler, testClassHandler6);
    }

    @Test
    public void testCache() {
        RemockConfiguration first = RemockConfiguration.findFor(SomeTestClass.class);
        RemockConfiguration second = RemockConfiguration.findFor(SomeTestClass.class);
        assertTrue(first == second);

        RemockConfiguration subFirst = RemockConfiguration.findFor(SomeSubTestClass.class);
        RemockConfiguration subSecond = RemockConfiguration.findFor(SomeSubTestClass.class);
        assertTrue(subFirst == subSecond);
    }
}
