package no.saua.remock.internal;

import java.util.Objects;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;

public class RegularDefinition extends Entity<RegularDefinition> implements SpringBeanDefiner {

    private final Class<?> beanClass;
    private final String beanName;

    public RegularDefinition(Class<?> beanClass, String beanName) {
        this.beanClass = beanClass;
        this.beanName = beanName;
    }

    @Override
    public BeanDefinition getBeanDefinition() {
        GenericBeanDefinition beanDef = new GenericBeanDefinition();
        beanDef.setBeanClass(beanClass);
        return beanDef;
    }

    @Override
    public String getBeanName() {
        return beanName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(beanClass, beanName);
    }

    @Override
    public boolean equals(RegularDefinition other) {
        return Objects.equals(beanClass, other.beanClass) && Objects.equals(beanName, other.beanName);
    }
}
