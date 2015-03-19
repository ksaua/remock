package no.saua.remock;

import no.saua.remock.exampleapplication.ServiceWhichShouldNotBeInstantiaed;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

@LazilyInitialized
@ContextConfiguration(classes = ServiceWhichShouldNotBeInstantiaed.class)
public class LazyInitializedTest extends CommonTest {
    @Test
    public void test() {
        /* No need to do anything. This will fail if the test is not lazy */
    }
}
