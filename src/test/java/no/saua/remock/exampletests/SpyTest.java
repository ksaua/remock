package no.saua.remock.exampletests;

import no.saua.remock.CommonTest;
import no.saua.remock.ReplaceWithSpy;
import no.saua.remock.exampletests.application.AnInterface;
import no.saua.remock.exampletests.application.AnInterfaceImplOne;
import no.saua.remock.exampletests.application.AnInterfaceImplTwo;
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
