package no.saua.remock.internal;

import org.springframework.test.context.MergedContextConfiguration;

/**
 * To get Springs context caching mechanism working we extend the MergedContextConfiguration and
 * modifying the equals-method to take into account what beans were {@linkplain Rejecter rejected}
 * and what beans were {@link MockDefinition mocked}.
 */
public class RemockMergedContextConfiguration_Regular extends MergedContextConfiguration implements RemockMergedContextConfiguration {

    private final RemockConfiguration remockConfiguration;

    public RemockMergedContextConfiguration_Regular(MergedContextConfiguration mergedConfig,
                                                    RemockConfiguration remockConfiguration) {
        super(mergedConfig);
        this.remockConfiguration = remockConfiguration;
    }

    @Override
    public RemockConfiguration getRemockConfiguration() {
        return remockConfiguration;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + remockConfiguration.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other)
                && (other instanceof RemockMergedContextConfiguration_Regular)
                && remockConfiguration.equals(((RemockMergedContextConfiguration_Regular) other).remockConfiguration);
    }
}
