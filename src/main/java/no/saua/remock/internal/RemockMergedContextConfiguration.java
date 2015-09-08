package no.saua.remock.internal;

import org.springframework.test.context.MergedContextConfiguration;

/**
 * To get Springs context caching mechanism working we extend the MergedContextConfiguration and
 * modifying the equals-method to take into account what beans were {@linkplain Rejecter rejected}
 * and what beans were {@link MockDefinition mocked}.
 */
public class RemockMergedContextConfiguration extends MergedContextConfiguration {

    private final RemockConfiguration annotations;

    public RemockMergedContextConfiguration(MergedContextConfiguration mergedConfig) {
        super(mergedConfig);
        annotations = RemockConfiguration.findFor(getTestClass());
    }

    public RemockConfiguration getAnnotations() {
        return annotations;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + annotations.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other)
                && (other instanceof RemockMergedContextConfiguration)
                && annotations.equals(((RemockMergedContextConfiguration) other).annotations);
    }
}
