package no.saua.remock.internal;

import java.lang.reflect.Method;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.StandardMethodMetadata;

public class RemockBeanFactory extends DefaultListableBeanFactory {

    private final static Logger log = LoggerFactory.getLogger(RemockBeanFactory.class);

    private RemockConfiguration remockConfig;

    public RemockBeanFactory(RemockConfiguration remockConfig) {
        this.remockConfig = remockConfig;
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
                    StandardMethodMetadata methodMetaData = (StandardMethodMetadata) factoryMethodMetadata;
                    beanClazz = methodMetaData.getIntrospectedMethod().getReturnType();
                } else {
                    // Try to find the method directly from the defining class/method
                    String declaringClassName = factoryMethodMetadata.getDeclaringClassName();
                    Class<?> factoryClazz = Class.forName(declaringClassName);

                    // Note that MethodMetadata does not give us the exact method, we have to loop through to find
                    // one which matches the name -- this is not optimal. But it should not matter much, the only thing
                    // which may trip this up is having to classes with same name but different return values which is
                    // not something we would expect in (sane) @Configuration classes.
                    for (Method m: factoryClazz.getDeclaredMethods()) {
                        if (factoryMethodMetadata.getMethodName().equals(m.getName())) {
                            beanClazz = m.getReturnType();
                        }
                    }
                }
            }

            if (beanClassName != null) {
                beanClazz = Class.forName(beanClassName);
            }
            if (beanClazz != null && isBeanRejected(beanName, beanClazz)) {
                log.info("Rejected bean [{}] with definiton [{}]", beanName, beanDefinition);
            } else {
                if (beanClazz == null) {
                    log.warn("Unable to find beanclass for bean [{}] with definition [{}]", beanName, beanDefinition);
                }
                if (!remockConfig.foundDisableLazyInitAnnotation()) {
                    beanDefinition.setLazyInit(true);
                }
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
        for (Rejecter rejecter : remockConfig.getRejecters()) {
            if (rejecter.shouldReject(beanName, beanClazz)) {
                return true;
            }
        }
        return false;
    }
}
