package no.saua.remock.internal;

import org.springframework.test.context.MergedContextConfiguration;

import java.util.Objects;
import java.util.Set;

/**
 * To get Springs context caching mechanism working we extend the MergedContextConfiguration and modifying the
 * equals-method to take into account what beans were {@linkplain Rejecter rejected} and what beans were {@link
 * MockDefinition mocked}.
 */
public class RemockMergedContextConfiguration extends MergedContextConfiguration {

    private final Set<Rejecter> rejecters;
    private final Set<MockDefinition> mocks;
    private final Set<SpyDefinition> spies;

    public RemockMergedContextConfiguration(MergedContextConfiguration mergedConfig) {
        super(mergedConfig);
        RemockTestClassAnnotationFinder annotationFinder = new RemockTestClassAnnotationFinder(getTestClass());
        rejecters = annotationFinder.getRejecters();
        mocks = annotationFinder.getMocks();
        spies = annotationFinder.getSpies();
    }

    @Override
    public boolean equals(Object other) {
        if (super.equals(other) && other instanceof RemockMergedContextConfiguration) {
            RemockMergedContextConfiguration otherObj = (RemockMergedContextConfiguration) other;
            return super.equals(other) && Objects.equals(rejecters, otherObj.rejecters) && Objects.equals(mocks,
                    otherObj.mocks) && Objects.equals(spies, otherObj.spies);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(rejecters, mocks, spies);
    }
}
