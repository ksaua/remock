package no.saua.remock;

import no.saua.remock.application.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class RejectTest {
    /**
     * Reject one implementation
     */
    @Reject(AnInterfaceImplOne.class)
    @ContextConfiguration(classes = {AnInterfaceImplOne.class, AnInterfaceImplTwo.class})
    public static class RejectSingleImplementation extends CommonTest {;

        @Inject
        public List<AnInterface> implementations;

        @Test
        public void test() {
            assertEquals(1, implementations.size());
        }
    }

    /**
     * Reject all implementations
     */
    @Reject(AnInterface.class)
    @ContextConfiguration(classes = {AnInterfaceImplOne.class, AnInterfaceImplTwo.class})
    public static class RejectAll extends CommonTest {


        @Autowired(required = false)
        public List<AnInterface> implementations;

        @Test
        public void test() {
            assertNull(implementations);
        }
    }

    /**
     * Reject super-class
     */
    @Reject(SuperClass.class)
    @ContextConfiguration(classes = {SuperClass.class, SubClass.class})
    public static class RejectSuperClass extends CommonTest {

        @Autowired(required = false)
        public List<SuperClass> instances;

        @Test
        public void test() {
            assertNull(instances);
        }
    }

    /**
     * Reject sub-class
     */
    @Reject(SubClass.class)
    @ContextConfiguration(classes = {SuperClass.class, SubClass.class})
    public static class RejectSubClass extends CommonTest {

        @Inject
        public List<SuperClass> instances;

        @Test
        public void test() {
            assertEquals(1, instances.size());
        }
    }
}
