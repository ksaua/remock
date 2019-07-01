package no.saua.remock;

import org.junit.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.PostConstruct;

import static org.junit.Assert.assertEquals;

/**
 * All beans are by default lazy - except anything in the {@link ContextConfiguration} which for this class is the
 * default {@link TestConfig}.
 */
@ContextConfiguration
public class DisableLazyInitTest_AllLazy extends CommonTest {

    @Test
    public void test() {
        assertEquals(true, TestConfig.postConstructCalled.get());
    }

    @Configuration
    @Import(LazyService.class)
    public static class TestConfig {
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
