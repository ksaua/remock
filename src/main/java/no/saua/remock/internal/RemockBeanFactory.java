package no.saua.remock.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.StandardMethodMetadata;

import java.lang.reflect.Method;

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
                    // which may trip this up is having two classes with same name but different return values which is
                    // not something we would expect in (sane) @Configuration classes.
                    for (Method m : factoryClazz.getDeclaredMethods()) {
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
                if (remockConfig.getEagerBeanClasses().contains(beanClazz)
                        || remockConfig.getEagerBeanNames().contains(beanName)) {
                    beanDefinition.setLazyInit(false);
                } else if (!remockConfig.disableLazyInit()) {
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
        } else if (bean instanceof FactoryBean) {
            FactoryBean<?> fb = (FactoryBean<?>) bean;
            if (isBeanRejected(beanName, fb.getObjectType())) {
                log.info("Rejected FactoryBean[{}] beacause it would have created a [{}]", fb, fb.getObjectType());
            }
        } else {
            super.registerSingleton(beanName, bean);
        }
    }

    /**
     * Confuses Spring to not use any factory beans which would have create a rejected class.
     */
    @Override
    protected Class<?> getTypeForFactoryBean(FactoryBean<?> factoryBean) {
        Class<?> clazz = super.getTypeForFactoryBean(factoryBean);

        // Remock also uses factoryBeans, if it one of ours, then of course it ok.
        if (factoryBean instanceof MockDefinition.MockFactoryBean) {
            return clazz;
        }

        // If the bean is not rejected, then return the actual class.
        if (!isBeanRejected(null, clazz)) {
            return clazz;
        }

        // Bean is rejected. Pretend that the class is the bogus class to mis-direct spring to never use this factory bean.
        log.info("To reject [{}] spring has been hacked to believe that the factory bean [{}] creates a different class", clazz, factoryBean);
        return Remock_BogusClassWhenRejectingBeansCreatedByAFactory.class;
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

    private static class Remock_BogusClassWhenRejectingBeansCreatedByAFactory {
    }
}
