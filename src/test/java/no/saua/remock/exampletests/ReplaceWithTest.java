package no.saua.remock.exampletests;

import no.saua.remock.RemockBootstrapper;
import no.saua.remock.ReplaceWith;
import no.saua.remock.exampletests.application.AnInterface;
import no.saua.remock.exampletests.application.AnInterfaceImplOne;
import no.saua.remock.exampletests.application.AnInterfaceImplTwo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@BootstrapWith(RemockBootstrapper.class)
@DirtiesContext
@ReplaceWith(value = AnInterfaceImplOne.class, with = AnInterfaceImplTwo.class)
@ContextConfiguration(classes = { AnInterfaceImplOne.class })
public class ReplaceWithTest {

    @Inject
    public AnInterface implementation;

    @Test
    public void test() {
        assertThat(implementation, instanceOf(AnInterfaceImplTwo.class));
    }
}
