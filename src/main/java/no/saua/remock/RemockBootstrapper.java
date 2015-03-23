package no.saua.remock;

import no.saua.remock.internal.RemockBeanFactory;
import no.saua.remock.internal.RemockContextClassLoader;
import no.saua.remock.internal.RemockMergedContextConfiguration;
import no.saua.remock.internal.RemockTestExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextLoader;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.support.AbstractTestContextBootstrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Bootstrapper for Remock. Sets up the context loader {@link RemockContextClassLoader}.
 */
public class RemockBootstrapper extends AbstractTestContextBootstrapper {

    private static final Logger log = LoggerFactory.getLogger(RemockBeanFactory.class);

    @Override
    protected Class<? extends ContextLoader> getDefaultContextLoaderClass(Class<?> testClass) {
        return RemockContextClassLoader.class;
    }

    @Override
    protected MergedContextConfiguration processMergedContextConfiguration(MergedContextConfiguration mergedConfig) {
        log.debug("Using Remock's MergecContextConfiguration");
        return new RemockMergedContextConfiguration(mergedConfig);
    }

    @Override
    protected List<String> getDefaultTestExecutionListenerClassNames() {
        List<String> classes = new ArrayList<>();
        classes.addAll(super.getDefaultTestExecutionListenerClassNames());
        classes.add(RemockTestExecutionListener.class.getName());
        return Collections.unmodifiableList(classes);
    }
}
