package no.saua.remock.internal;

import no.saua.remock.internal.SpyDefinition.SpyInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.lang.reflect.Field;
import java.util.Set;

public class RemockContextClassLoader extends AnnotationConfigContextLoader {

    private Set<Rejecter> rejecters;
    private Set<MockDefinition> mockDefinitions;
    private Set<SpyDefinition> spyDefinitions;
    private RemockBeanFactory beanFactory;

    @Override
    public void prepareContext(GenericApplicationContext context) {
        try {
            Field beanFactoryField = GenericApplicationContext.class.getDeclaredField("beanFactory");
            beanFactoryField.setAccessible(true);
            beanFactory = new RemockBeanFactory(rejecters);
            beanFactoryField.set(context, beanFactory);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Unable to perform hack", e);
        }
    }

    @Override
    protected void customizeContext(GenericApplicationContext context) {
        super.customizeContext(context);
        for (MockDefinition mockDefinition : mockDefinitions) {
            beanFactory.registerMockBeanDefinition(mockDefinition.getBeanName(), mockDefinition.getBeanDefinition());
        }

        beanFactory.registerSingleton("$RemockSpyInitializer$", new SpyInitializer(spyDefinitions));
    }

    @Override
    protected Class<?>[] detectDefaultConfigurationClasses(Class<?> declaringClass) {
        Class<?>[] classes = super.detectDefaultConfigurationClasses(declaringClass);
        findRemockDefinitionsOnClass(classes);
        return classes;
    }

    @Override
    public void processContextConfiguration(ContextConfigurationAttributes configAttributes) {
        super.processContextConfiguration(configAttributes);
        findRemockDefinitionsOnClass(configAttributes.getDeclaringClass());
    }

    private void findRemockDefinitionsOnClass(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            RemockTestClassAnnotationFinder testClassHandler = new RemockTestClassAnnotationFinder(clazz);
            rejecters = testClassHandler.getRejecters();
            mockDefinitions = testClassHandler.getMocks();
            spyDefinitions = testClassHandler.getSpies();
        }
    }
}
