package no.saua.remock;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertTrue;

/**
 * Verifies that spring context events are raised as they should. Note the to trigger the {@link ContextClosedEvent}
 * we need to annotate our test with {@link DirtiesContext}. And to ensure that it was indeed raised we need to
 * explicitly run the {@link #method2_assertThatWeReceivedEvents()} after {@link #method1_test()}.
 */
@DisableLazyInit(EventListenerTest.EventReceivingComponent.class)
@ContextConfiguration(classes = EventListenerTest.EventReceivingComponent.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EventListenerTest extends CommonTest {

    private static Logger log = LoggerFactory.getLogger(EventListenerTest.class);
    private static ThreadLocal<Boolean> refreshedEventRaised = ThreadLocal.withInitial(() -> false);
    private static ThreadLocal<Boolean> closedEventRaised = ThreadLocal.withInitial(() -> false);

    @DirtiesContext
    @Test
    public void method1_test() {
        log.info("Now inside the test");
    }

    @Test
    public void method2_assertThatWeReceivedEvents() {
        assertTrue("Received refresh event", refreshedEventRaised.get());
        assertTrue("Received closed event", closedEventRaised.get());
    }

    @Component
    public static class EventReceivingComponent {
        @EventListener
        public void onContextRefreshedEvent(ContextRefreshedEvent e) {
            EventListenerTest.refreshedEventRaised.set(true);
        }

        @EventListener
        public void onContextClosedEvent(ContextClosedEvent e) {
            EventListenerTest.closedEventRaised.set(true);
        }
    }
}
