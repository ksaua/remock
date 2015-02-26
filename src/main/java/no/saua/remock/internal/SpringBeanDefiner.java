package no.saua.remock.internal;

import org.springframework.beans.factory.config.BeanDefinition;

public interface SpringBeanDefiner {
    public BeanDefinition getBeanDefinition();
    public String getBeanName();
}
