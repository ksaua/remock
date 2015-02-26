package no.saua.remock.internal;

import org.mockito.Mockito;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import java.util.Objects;

public class MockDefinition extends EntityHelper<MockDefinition> implements Rejecter {

    private final Class<?> mockClass;
    private final String beanName;

    public MockDefinition(String beanName, Class<?> mockClass) {
        this.beanName = beanName;
        this.mockClass = mockClass;
    }

    public BeanDefinition getFactoryBeanDefinition() {
        GenericBeanDefinition def = new GenericBeanDefinition();
        def.setBeanClass(MockFactoryBean.class);
        ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
        constructorArgumentValues.addIndexedArgumentValue(0, mockClass);
        def.setConstructorArgumentValues(constructorArgumentValues);
        return def;
    }

    public String getBeanName() {
        return beanName;
    }

    @Override
    public boolean shouldReject(String beanName, Class<?> bean) {
        return beanName.equals(this.beanName) || bean.equals(mockClass);
    }

    @Override
    public boolean equals(MockDefinition other) {
        return Objects.equals(beanName, other.beanName) && Objects.equals(mockClass, other.mockClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beanName, mockClass);
    }

    public static class MockFactoryBean implements FactoryBean {

        private final Class<?> mockClass;

        public MockFactoryBean(Class<?> mockClass) {
            this.mockClass = mockClass;
        }

        @Override
        public Object getObject() throws Exception {
            return Mockito.mock(mockClass);
        }

        @Override
        public Class<?> getObjectType() {
            return mockClass;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }
    }
}
