package no.saua.remock;

import no.saua.remock.exampleapplication.AnInterface;
import no.saua.remock.exampleapplication.AnInterfaceImplOne;
import no.saua.remock.exampleapplication.AnInterfaceImplTwo;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;


@RunWith(Enclosed.class)
public class ReplaceWithImplTest {

    @ReplaceWithImpl(value = AnInterfaceImplOne.class, with = AnInterfaceImplTwo.class)
    @ContextConfiguration(classes = {AnInterfaceImplOne.class})
    public static class ReplaceWithImplAnnotatedOnClassTest extends CommonTest {

        @Inject
        public AnInterface implementation;

        @Inject
        public AnInterfaceImplTwo implTwo;

        @Test
        public void test() {
            assertEquals(AnInterfaceImplTwo.class, implementation.getClass());
            assertEquals(AnInterfaceImplTwo.class, implTwo.getClass());
        }
    }

    @ContextConfiguration(classes = {AnInterfaceImplOne.class})
    public static class ReplaceWithImplAnnotatedOnFieldTest extends CommonTest {

        @ReplaceWithImpl(value = AnInterfaceImplOne.class, with = AnInterfaceImplTwo.class)
        public AnInterface anInterface;

        @Test
        public void test() {
            assertEquals(AnInterfaceImplTwo.class, anInterface.getClass());
        }
    }
}
