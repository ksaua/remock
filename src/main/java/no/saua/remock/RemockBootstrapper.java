package no.saua.remock;

import no.saua.remock.internal.*;
import no.saua.remock.internal.RemockMergedContextConfiguration_WebApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
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
        if (AnnotationUtils.findAnnotation(testClass, RemockWebAppTest.class) != null) {
            return RemockContextClassLoader.WebApp.class;
        }

        return RemockContextClassLoader.Regular.class;
    }

    @Override
    protected MergedContextConfiguration processMergedContextConfiguration(MergedContextConfiguration mergedConfig) {
        Class<?> testClass = getBootstrapContext().getTestClass();
        RemockConfiguration remockConfig = RemockConfiguration.findFor(testClass);

        RemockWebAppTest annotation = AnnotationUtils.findAnnotation(testClass, RemockWebAppTest.class);
        if (annotation != null) {
            return new RemockMergedContextConfiguration_WebApp(mergedConfig, remockConfig, annotation.value());
        }
        return new RemockMergedContextConfiguration_Regular(mergedConfig, remockConfig);
    }

    @Override
    protected List<String> getDefaultTestExecutionListenerClassNames() {
        List<String> classes = new ArrayList<>();
        classes.addAll(super.getDefaultTestExecutionListenerClassNames());
        classes.add(RemockTestExecutionListener.class.getName());
        return Collections.unmodifiableList(classes);
    }
}
