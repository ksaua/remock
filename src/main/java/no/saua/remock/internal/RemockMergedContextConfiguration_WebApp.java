package no.saua.remock.internal;

import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.web.WebMergedContextConfiguration;

public class RemockMergedContextConfiguration_WebApp extends WebMergedContextConfiguration implements RemockMergedContextConfiguration {

    private final RemockConfiguration remockConfiguration;

    public RemockMergedContextConfiguration_WebApp(MergedContextConfiguration mergedConfig,
                                                   RemockConfiguration remockConfiguration, String webAppPath) {
        super(mergedConfig, webAppPath);
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
                && (other instanceof RemockMergedContextConfiguration_WebApp)
                && remockConfiguration.equals(((RemockMergedContextConfiguration_WebApp) other).remockConfiguration);
    }
}
