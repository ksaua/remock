package no.saua.remock.internal;

import no.saua.remock.Reject;
import no.saua.remock.ReplaceWith;
import no.saua.remock.ReplaceWithMock;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class RemockContextClassLoader extends AnnotationConfigContextLoader {

    private List<Rejecter> rejecters = new ArrayList<>();
    private List<MockDefinition> mockDefinitions = new ArrayList<>();
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
            beanFactory.registerMockBeanDefinition(mockDefinition.getBeanName(), mockDefinition.getFactoryBeanDefinition());
        }
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
            Reject rejectAnnot = clazz.getAnnotation(Reject.class);
            if (rejectAnnot != null) {
                if (!Reject.DEFAULT_CLASS.equals(rejectAnnot.value())) {
                    rejecters.add(new RejectBeanClassDefinition(rejectAnnot.value()));
                } else if (!Reject.DEFAULT_BEAN_NAME.equals(rejectAnnot.beanName())) {
                    rejecters.add(new RejectBeanNameDefinition(rejectAnnot.beanName()));
                }
            }

            ReplaceWith replaceWithAnnot = clazz.getAnnotation(ReplaceWith.class);
            if (replaceWithAnnot != null) {
                Class<?> reject= replaceWithAnnot.value();
                Class<?> with = replaceWithAnnot.with();
                if (reject == null || with == null) {
                    throw new IllegalArgumentException("Both the class to replace, and the class to replace with " +
                            "must be set for the ReplaceWith annotation to work.");
                }
                MockDefinition mockDefinition = new MockDefinition("meh", with);
                mockDefinitions.add(mockDefinition);
                rejecters.add(new RejectBeanClassDefinition(reject));
            }

            for (Field field : clazz.getDeclaredFields()) {
                ReplaceWithMock annotation = field.getAnnotation(ReplaceWithMock.class);
                if (annotation != null) {
                    MockDefinition mockDefinition = new MockDefinition(field.getName(), field.getType());
                    mockDefinitions.add(mockDefinition);
                    rejecters.add(mockDefinition);
                }
                Reject annot2 = field.getAnnotation(Reject.class);
                if (annot2 != null) {
                    rejecters.add(new RejectBeanClassDefinition(field.getType()));
                }
            }
        }
    }
}
