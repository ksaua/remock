package no.saua.remock.internal;

import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SpyDefinition extends Entity<SpyDefinition> {

    private String beanNameToSpy;
    private Class<?> classToSpy;

    public SpyDefinition(Class<?> beanClass, String beanName) {
        classToSpy = beanClass;
        beanNameToSpy = beanName;
    }

    public boolean matches(Class<?> beanClass, String beanName) {
        return (classToSpy != null && classToSpy.isAssignableFrom(beanClass)) || beanName.equals(beanNameToSpy);
    }

    @Override
    public boolean equals(SpyDefinition other) {
        return Objects.equals(beanNameToSpy, other.beanNameToSpy) && Objects.equals(classToSpy, other.classToSpy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beanNameToSpy, classToSpy);
    }

    public static class SpyInitializer implements BeanPostProcessor, Resettable {

        private List<SpyDefinition> spyDefinitions = new ArrayList<>();
        private List<Object> spies = new ArrayList<>();

        public SpyInitializer(List<SpyDefinition> spyDefinitions) {
            this.spyDefinitions = spyDefinitions;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            for (SpyDefinition spyDefinition : spyDefinitions) {
                if (spyDefinition.matches(bean.getClass(), beanName)) {
                    Object spy = Mockito.spy(bean);
                    spies.add(spy);
                    return spy;
                }
            }
            return bean;
        }

        @Override
        public void reset() {
            for (Object spy: spies) {
                Mockito.reset(spy);
            }
        }
    }
}
