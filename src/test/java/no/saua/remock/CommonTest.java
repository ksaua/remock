package no.saua.remock;


import org.junit.runner.RunWith;
import org.mockito.internal.util.MockUtil;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * This allows us not to write the @RunWith and @BootstrapWith annotations for every damn test.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@BootstrapWith(RemockBootstrapper.class)
public abstract class CommonTest {

    public static boolean isMock(Object object) {
        return new MockUtil().isMock(object);
    }

    public static boolean isSpy(Object object) {
        return new MockUtil().isSpy(object);
    }
}
