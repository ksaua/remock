package no.saua.remock.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.StandardMethodMetadata;

import java.util.Set;

public class RemockBeanFactory extends DefaultListableBeanFactory {

    private final static Logger log = LoggerFactory.getLogger(RemockBeanFactory.class);

    private Set<Rejecter> rejecters;

    public RemockBeanFactory(Set<Rejecter> rejecters) {
        this.rejecters = rejecters;
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        try {
            Class<?> beanClazz = null;
            String beanClassName = beanDefinition.getBeanClassName();

            // Fix for @Bean annotations in @Configuration will return a "null" beanClassName. This
            // finds the class anyway:
            if (beanClassName == null && beanDefinition instanceof AnnotatedBeanDefinition) {
                MethodMetadata factoryMethodMetadata =
                                ((AnnotatedBeanDefinition) beanDefinition).getFactoryMethodMetadata();
                if (factoryMethodMetadata instanceof StandardMethodMetadata) {
                    beanClazz =
                                    ((StandardMethodMetadata) factoryMethodMetadata).getIntrospectedMethod()
                                                    .getReturnType();
                }
            }


            if (beanClassName != null) {
                beanClazz = Class.forName(beanClassName);
            }
            if (isBeanRejected(beanName, beanClazz)) {
                log.info("Rejected bean [{}] with definiton [{}]", beanName, beanDefinition);
            } else {
                super.registerBeanDefinition(beanName, beanDefinition);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void registerSingleton(String beanName, Object bean) {
        if (isBeanRejected(beanName, bean.getClass())) {
            log.info("Rejected bean [{}] which is the singleton [{}]", beanName, bean);
        } else {
            super.registerSingleton(beanName, bean);
        }
    }

    public void registerMockBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        super.registerBeanDefinition(beanName, beanDefinition);
    }

    private boolean isBeanRejected(String beanName, Class<?> beanClazz) {
        for (Rejecter rejecter : rejecters) {
            if (rejecter.shouldReject(beanName, beanClazz)) {
                return true;
            }
        }
        return false;
    }
}
