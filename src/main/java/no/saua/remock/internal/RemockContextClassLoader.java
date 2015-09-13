package no.saua.remock.internal;

import no.saua.remock.internal.SpyDefinition.SpyInitializer;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;

import java.lang.reflect.Field;

/**
 * This is Remocks core class. It:
 * <ul>
 * <li>Hooks into the spring-test-lifecycle and starts looking for Remock annotation.</li>
 * <li>Replaces Spring's beanFactory with a {@link RemockBeanFactory}.</li>
 * <li>Registers mocked beans.</li>
 * <li>Registers a {@link SpyInitializer}.</li>
 * </ul>
 */
public class RemockContextClassLoader {

    public static class Regular extends AnnotationConfigContextLoader {
        @Override
        public void prepareContext(ConfigurableApplicationContext context, MergedContextConfiguration mergedConfig) {
            prepareContextCommon(context, mergedConfig);
        }
    }

    /**
     * Created by knut on 09.09.15.
     */
    public static class WebApp extends AnnotationConfigWebContextLoader {
        @Override
        protected void prepareContext(ConfigurableApplicationContext context, MergedContextConfiguration mergedConfig) {
            prepareContextCommon(context, mergedConfig);
        }
    }

    public static void prepareContextCommon(ConfigurableApplicationContext context, MergedContextConfiguration mergedConfig) {
        if (!(mergedConfig instanceof RemockMergedContextConfiguration)) {
            throw new AssertionError("MergedContextConfiguration is not a RemockMergedContextConfiguration. This should not happen.");
        }

        RemockConfiguration remockConfig = ((RemockMergedContextConfiguration) mergedConfig).getRemockConfiguration();
        try {
            Field beanFactoryField = GenericApplicationContext.class.getDeclaredField("beanFactory");
            beanFactoryField.setAccessible(true);
            BeanFactory beanFactory = createBeanFactory(remockConfig);
            beanFactoryField.set(context, beanFactory);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Unable to perform bean factory hack", e);
        }
    }

    public static RemockBeanFactory createBeanFactory(RemockConfiguration remockConfig) {
        RemockBeanFactory remockBeanFactory = new RemockBeanFactory(remockConfig);

        // :: Initialize mock definitions
        for (SpringBeanDefiner mockDefinition : remockConfig.getDefiners()) {
            remockBeanFactory.registerMockBeanDefinition(mockDefinition.getBeanName(), mockDefinition.getBeanDefinition());
        }

        // :: Register the spy initializer
        remockBeanFactory.registerSingleton("$RemockSpyInitializer$", new SpyInitializer(remockConfig.getSpies()));
        return remockBeanFactory;
    }


}
