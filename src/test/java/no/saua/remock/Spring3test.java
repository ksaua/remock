package no.saua.remock;

import no.saua.remock.exampleapplication.AnInterface;
import no.saua.remock.exampleapplication.AnInterfaceImplOne;
import no.saua.remock.exampleapplication.SomeService;
import no.saua.remock.exampleapplication.SomeServiceWithDependencies;
import no.saua.remock.internal.RemockContextClassLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.saua.remock.CommonTest.isMock;
import static no.saua.remock.CommonTest.isSpy;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Reject(SomeService.class)
@WrapWithSpy(SomeServiceWithDependencies.class)
@ReplaceWithMock(AnInterface.class)
@ContextConfiguration(classes = {SomeService.class, SomeServiceWithDependencies.class, AnInterfaceImplOne.class},
                loader = RemockContextClassLoader.class)
public class Spring3test {

    @Autowired(required = false)
    private SomeService someService;

    @Inject
    private SomeServiceWithDependencies willBeSpied;

    @Inject
    private AnInterface willBeMocked;

    @Test
    public void test() {
        assertNull(someService);
        assertTrue(isMock(willBeMocked));
        assertTrue(isSpy(willBeSpied));
    }
}
