package no.saua.remock;

import no.saua.remock.exampleapplication.*;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;


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
            assertThat(implementations).hasSize(1);
            assertThat(implementations.get(0)).isInstanceOf(AnInterfaceImplTwo.class);
        }
    }

    /**
     * Reject on field
     */
    @ContextConfiguration(classes = {AnInterfaceImplOne.class, AnInterfaceImplTwo.class})
    public static class RejectOnField extends CommonTest {

        @Reject
        private AnInterfaceImplOne rejected;

        @Inject
        private List<AnInterface> implementations;

        @Test
        public void test() {
            assertThat(implementations).hasSize(1);
            assertThat(implementations.get(0)).isInstanceOf(AnInterfaceImplTwo.class);
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
            assertThat(implementations).isNull();
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
            assertThat(instances).isNull();
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
            assertThat(instances).hasSize(1);
            assertThat(instances.get(0)).isInstanceOf(SuperClass.class);
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
            assertThat(anInterface).isInstanceOf(AnInterfaceImplOne.class);
            assertThat(someService).isNull();
        }
    }

    /**
     * Reject all of a type except
     */
    @Reject(value = AnInterface.class, except = AnInterfaceImplOne.class)
    @ContextConfiguration(classes = {AnInterfaceImplOne.class, AnInterfaceImplTwo.class})
    public static class RejectExcept extends CommonTest {
        @Inject
        private AnInterface one;

        @Autowired(required = false)
        private AnInterfaceImplTwo two;

        @Test
        public void test() {
            assertThat(one).isInstanceOf(AnInterfaceImplOne.class);
            assertThat(two).isNull();
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
            assertThat(someService).isNull();
        }
    }

    /**
     * Reject multiple
     */
    @Reject(AnInterfaceImplOne.class)
    @Reject(beanName = "someService")
    @ContextConfiguration(classes = {ConfigurationClass.class, AnInterfaceImplOne.class, AnInterfaceImplTwo.class})
    public static class RejectRepeated extends CommonTest {
        @Inject
        private AnInterface two;

        @Autowired(required = false)
        private SomeService someService;

        @Test
        public void test() {
            assertThat(two).isInstanceOf(AnInterfaceImplTwo.class);
            assertThat(someService).isNull();
        }
    }
}
