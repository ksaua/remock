package no.saua.remock;

import no.saua.remock.DisableLazyInitTest.EagerService;
import org.junit.Test;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.PostConstruct;

import static org.junit.Assert.assertEquals;

/**
 * Checks that {@link DisableLazyInitTest} causes initialization of the bean even though it is not @Inject'ed.
 */
@DisableLazyInit
@ContextConfiguration(classes = EagerService.class)
public class DisableLazyInitTest extends CommonTest {

    private static ThreadLocal<Boolean> postConstructCalled = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return Boolean.FALSE;
        }
    };

    @Test
    public void test() {
        assertEquals(true, postConstructCalled.get());
    }

    @Service
    public static class EagerService {

        @PostConstruct
        public void post() {
            postConstructCalled.set(Boolean.TRUE);
        }
    }
}
