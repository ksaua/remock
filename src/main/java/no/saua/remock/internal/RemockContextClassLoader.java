package no.saua.remock.internal;

import no.saua.remock.internal.RemockAnnotationFinder.RemockAnnotations;
import no.saua.remock.internal.SpyDefinition.SpyInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * This is Remocks core class. It:
 * <ul>
 * <li>Hooks into the spring-test-lifecycle and starts looking for Remock annotation.</li>
 * <li>Replaces Spring's beanFactory with a {@link RemockBeanFactory}.</li>
 * <li>Registers mocked beans.</li>
 * <li>Registers a {@link SpyInitializer}.</li>
 * </ul>
 */
public class RemockContextClassLoader extends AnnotationConfigContextLoader {

    private Set<Rejecter> rejecters;
    private Set<SpringBeanDefiner> definers;
    private Set<SpyDefinition> spyDefinitions;
    private RemockBeanFactory beanFactory;
    private boolean foundEagerAnnotation;

    @Override
    public void prepareContext(GenericApplicationContext context) {
        try {
            Field beanFactoryField = GenericApplicationContext.class.getDeclaredField("beanFactory");
            beanFactoryField.setAccessible(true);
            beanFactory = createBeanFactory();
            beanFactoryField.set(context, beanFactory);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Unable to perform bean factory hack", e);
        }
    }

    protected RemockBeanFactory createBeanFactory() {
        return new RemockBeanFactory(rejecters, foundEagerAnnotation);
    }

    @Override
    protected void customizeContext(GenericApplicationContext context) {
        super.customizeContext(context);
        for (SpringBeanDefiner mockDefinition : definers) {
            beanFactory.registerMockBeanDefinition(mockDefinition.getBeanName(), mockDefinition.getBeanDefinition());
        }

        beanFactory.registerSingleton("$RemockSpyInitializer$", new SpyInitializer(spyDefinitions));
    }

    @Override
    public void processContextConfiguration(ContextConfigurationAttributes configAttributes) {
        super.processContextConfiguration(configAttributes);
        findRemockDefinitionsOnClass(configAttributes.getDeclaringClass());
    }

    private void findRemockDefinitionsOnClass(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            RemockAnnotations testClassHandler = RemockAnnotationFinder.findFor(clazz);
            rejecters = testClassHandler.getRejecters();
            definers = testClassHandler.getDefiners();
            spyDefinitions = testClassHandler.getSpies();
            foundEagerAnnotation = testClassHandler.foundEagerAnnotation();
        }
    }
}
