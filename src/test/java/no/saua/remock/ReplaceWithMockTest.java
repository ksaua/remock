package no.saua.remock;

import no.saua.remock.application.ConfigurationClass;
import no.saua.remock.application.SomeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.MockUtil;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@BootstrapWith(RemockBootstrapper.class)
@DirtiesContext
@ContextConfiguration(classes = ConfigurationClass.class)
public class ReplaceWithMockTest {

    @ReplaceWithMock
    @Inject
    public SomeService someService;

    @Test
    public void test() {
        assertFalse(someService.getClass().equals(SomeService.class));
        assertTrue(new MockUtil().isMock(someService));
    }
}
