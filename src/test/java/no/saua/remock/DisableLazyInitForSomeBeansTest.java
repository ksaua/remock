package no.saua.remock;

import org.junit.Test;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.PostConstruct;

import no.saua.remock.DisableLazyInitForSomeBeansTest.*;

import static org.junit.Assert.assertEquals;

/**
 * Disables the lazy initializtion for only some of the beans.
 */
@DisableLazyInit(value = EagerService.class, beanName = "eagerNamedService")
@ContextConfiguration(classes = {EagerService.class, EagerNamedService.class, LazyService.class})
public class DisableLazyInitForSomeBeansTest extends CommonTest {

    @Test
    public void test() {
        assertEquals(true, EagerService.postConstructCalled.get());
        assertEquals(true, EagerNamedService.postConstructCalled.get());
    }

    @Service
    public static class EagerService {
        static ThreadLocal<Boolean> postConstructCalled = new ThreadLocal<Boolean>() {
            @Override
            protected Boolean initialValue() {
                return Boolean.FALSE;
            }
        };

        @PostConstruct
        public void post() {
            postConstructCalled.set(Boolean.TRUE);
        }
    }

    @Service("eagerNamedService")
    public static class EagerNamedService {
        static ThreadLocal<Boolean> postConstructCalled = new ThreadLocal<Boolean>() {
            @Override
            protected Boolean initialValue() {
                return Boolean.FALSE;
            }
        };

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
