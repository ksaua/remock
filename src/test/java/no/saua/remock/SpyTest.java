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
public class SpyTest {

    @ReplaceWithSpy(AnInterface.class)
    @ContextConfiguration(classes = {AnInterfaceImplOne.class, AnInterfaceImplTwo.class})
    public static class SpyAllOfTypeTest extends CommonTest {

        @Inject
        AnInterfaceImplOne one;

        @Inject
        AnInterfaceImplTwo two;

        @Test
        public void test() {
            assertTrue(isSpy(one));
            assertTrue(isSpy(two));
        }
    }
}
