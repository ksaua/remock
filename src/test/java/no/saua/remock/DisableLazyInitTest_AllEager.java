package no.saua.remock;

import org.junit.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.PostConstruct;

import static org.junit.Assert.assertTrue;

/**
 * Checks that {@link DisableLazyInit} causes initialization of the bean even though it is not @Inject-ed.
 */
@DisableLazyInit
@ContextConfiguration
public class DisableLazyInitTest_AllEager extends CommonTest {

    @Test
    public void test() {
        assertTrue(EagerService.postConstructCalled.get());
    }

    @Configuration
    @Import(EagerService.class)
    public static class Config {
    }

    @Service
    public static class EagerService {
        private static ThreadLocal<Boolean> postConstructCalled = ThreadLocal.withInitial(() -> Boolean.FALSE);

        @PostConstruct
        public void post() {
            postConstructCalled.set(Boolean.TRUE);
        }
    }
}
