package no.saua.remock;

import no.saua.remock.RemockContextConfigurationTest.RemockConfig;
import no.saua.remock.exampleapplication.AnInterfaceImplOne;
import no.saua.remock.exampleapplication.AnInterfaceImplTwo;
import no.saua.remock.exampleapplication.SomeServiceWithDependencies;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;

/**
 * Test
 */
@ContextConfiguration(classes = {SomeServiceWithDependencies.class, AnInterfaceImplOne.class, AnInterfaceImplTwo.class})
@RemockContextConfiguration(RemockConfig.class)
public class RemockContextConfigurationTest extends CommonTest {

    @Inject
    private SomeServiceWithDependencies someServiceWithDependencies;

    @Test
    public void test() {
        isMock(someServiceWithDependencies.getDependency());
    }

    @Reject(AnInterfaceImplTwo.class)
    @ReplaceWithMock(AnInterfaceImplOne.class)
    public static class RemockConfig {
    }
}
