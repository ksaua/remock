package no.saua.remock;

import no.saua.remock.exampleapplication.*;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;


@RunWith(Enclosed.class)
public class RejectTest {
    /**
     * Reject one implementation
     */
    @Reject(AnInterfaceImplOne.class)
    @ContextConfiguration(classes = {AnInterfaceImplOne.class, AnInterfaceImplTwo.class})
    public static class RejectSingleImplementation extends CommonTest {

        @Inject
        private List<AnInterface> implementations;

        @Test
        public void test() {
            assertEquals(1, implementations.size());
            assertThat(implementations.get(0), instanceOf(AnInterfaceImplTwo.class));
        }
    }

    /**
     * Reject all implementations
     */
    @Reject(AnInterface.class)
    @ContextConfiguration(classes = {AnInterfaceImplOne.class, AnInterfaceImplTwo.class})
    public static class RejectAll extends CommonTest {


        @Autowired(required = false)
        private List<AnInterface> implementations;

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
        private List<SuperClass> instances;

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
        private List<SuperClass> instances;

        @Test
        public void test() {
            assertEquals(1, instances.size());
            assertThat(instances.get(0), instanceOf(SuperClass.class));
        }
    }

    /**
     * Reject multiple classes
     */
    @Reject({AnInterfaceImplTwo.class, SomeService.class})
    @ContextConfiguration(classes = {AnInterfaceImplOne.class, AnInterfaceImplTwo.class, ConfigurationClass.class})
    public static class RejectMultiple extends CommonTest {
        @Inject
        private AnInterface anInterface;

        @Autowired(required = false)
        private SomeService someService;

        @Test
        public void test() {
            assertThat(anInterface, instanceOf(AnInterfaceImplOne.class));
            assertNull(someService);
        }
    }

    /**
     * Reject all of a type except
     */
    @Reject(value = AnInterface.class, except = AnInterfaceImplOne.class)
    @ContextConfiguration(classes = {AnInterfaceImplOne.class, AnInterfaceImplTwo.class})
    public static class RejectExcept extends CommonTest {
        @Inject
        private AnInterfaceImplOne one;

        @Autowired(required = false)
        private AnInterfaceImplTwo two;

        @Test
        public void test() {
            assertThat(one, instanceOf(AnInterfaceImplOne.class));
            assertNull(two);
        }
    }

    /**
     * Reject bean name
     */
    @Reject(beanName = "someService")
    @ContextConfiguration(classes = ConfigurationClass.class)
    public static class RejectBeanName extends CommonTest {

        @Autowired(required = false)
        private SomeService someService;

        @Test
        public void test() {
            assertNull("bean should not have been injected", someService);
        }
    }
}
