package no.saua.remock.internal;

import org.mockito.Mockito;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import java.util.Objects;

public class MockDefinition extends Entity<MockDefinition> implements SpringBeanDefiner {

    private final Class<?> mockClass;
    private final String beanName;
    private final boolean primary;

    public MockDefinition(String beanName, Class<?> mockClass, boolean primary) {
        this.beanName = beanName;
        this.mockClass = mockClass;
        this.primary = primary;
    }

    @Override
    public BeanDefinition getBeanDefinition() {
        GenericBeanDefinition def = new GenericBeanDefinition();
        def.setBeanClass(MockFactoryBean.class);
        def.setPrimary(primary);
        ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
        constructorArgumentValues.addIndexedArgumentValue(0, mockClass);
        def.setConstructorArgumentValues(constructorArgumentValues);
        return def;
    }

    @Override
    public String getBeanName() {
        return beanName;
    }

    @Override
    public boolean equals(MockDefinition other) {
        return Objects.equals(beanName, other.beanName) && Objects.equals(mockClass, other.mockClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beanName, mockClass);
    }

    public static class MockFactoryBean implements FactoryBean, Resettable {

        private final Object mock;
        private final Class<?> mockClass;

        public MockFactoryBean(Class<?> mockClass) {
            this.mockClass = mockClass;
            this.mock = Mockito.mock(mockClass);
        }

        @Override
        public Object getObject() throws Exception {
            return mock;
        }

        @Override
        public Class<?> getObjectType() {
            return mockClass;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }

        @Override
        public void reset() {
            Mockito.reset(mock);
        }
    }
}
