package no.saua.remock;


import org.junit.Test;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;

@ActiveProfiles("remock-profile")
@ContextConfiguration
public class ProfilesTest extends CommonTest {

    @Inject
    private Environment environment;

    @Test
    public void testProfiles() {
        assertEquals("remock-profile", environment.getActiveProfiles()[0]);
        assertEquals(1, environment.getActiveProfiles().length);
    }
}
