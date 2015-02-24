package no.saua.remock.internal;

import org.springframework.test.context.MergedContextConfiguration;

/**
 * To get Springs context caching mechanism we extend the MergedContextConfiguration and modifying the equals-method
 * to take into account what  beans were {@linkplain Rejecter rejected} and what beans were {@link MockDefinition
 * mocked}.
 */
public class RemockMergedContextConfiguration extends MergedContextConfiguration {
    public RemockMergedContextConfiguration(MergedContextConfiguration mergedConfig) {
        super(mergedConfig);
    }
}
