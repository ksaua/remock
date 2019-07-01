package no.saua.remock;

import no.saua.remock.DisableLazyInitTest_SomeEager.EagerService;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.PostConstruct;

import static org.junit.Assert.assertEquals;

/**
 * Disables the lazy initializtion for only some of the beans.
 */
@DisableLazyInit(value = EagerService.class, beanName = "eagerNamedService123")
@ContextConfiguration
public class DisableLazyInitTest_SomeEager extends CommonTest {

    @Test
    public void test() {
        assertEquals(true, EagerService.postConstructCalled.get());
        assertEquals(true, EagerNamedService.postConstructCalled.get());
    }

    @Configuration
    @Import({EagerService.class, EagerNamedService.class, LazyService.class})
    public static class Config {

    }

    @Service
    public static class EagerService {
        static ThreadLocal<Boolean> postConstructCalled = ThreadLocal.withInitial(() -> Boolean.FALSE);

        @PostConstruct
        public void post() {
            postConstructCalled.set(Boolean.TRUE);
        }
    }

    @Service("eagerNamedService123")
    public static class EagerNamedService {
        static ThreadLocal<Boolean> postConstructCalled = ThreadLocal.withInitial(() -> Boolean.FALSE);

        @PostConstruct
        public void post() {
            postConstructCalled.set(Boolean.TRUE);
        }
    }

    @Service
    public static class LazyService {
        @PostConstruct
        public void post() {
            throw new AssertionError("Should not be called");
        }
    }
}
