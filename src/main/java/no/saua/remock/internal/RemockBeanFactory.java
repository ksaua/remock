package no.saua.remock.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
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

            // Try to get the bean class
            if (beanClassName != null) {
                beanClazz = Class.forName(beanClassName);
            }

            if (beanClazz != null && isBeanRejected(beanName, beanClazz)) {
                log.info("Rejected bean [{}] with definition [{}]", beanName, beanDefinition);
                return;
            }

            if (beanClazz == null) {
                log.warn("Unable to find beanClass for bean [{}] with definition [{}]", beanName, beanDefinition);
            }

            // Should we set the bean to lazy?
            if (remockConfig.getEagerBeanClasses().contains(beanClazz) || remockConfig.getEagerBeanNames().contains(beanName)) {
                log.info("Bean [{}] was not set to lazy as according to the remock configuration.", beanName);
            } else if (beanDefinition.getRole() == BeanDefinition.ROLE_INFRASTRUCTURE) {
                log.debug("Bean [{}] was not set to lazy because it's role is ROLE_INFRASTRUCTURE.", beanName);
            } else if (!remockConfig.disableLazyInit()) {
                log.trace("Bean [{}] was set to lazy", beanName);
                beanDefinition.setLazyInit(true);
            }

            super.registerBeanDefinition(beanName, beanDefinition);
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
     * Ensures that spring does not post process any of the mocks. There's no reason to. Typically this is used for AOP.
     */
    @Override
    protected Object initializeBean(String beanName, Object bean, RootBeanDefinition mbd) {
        if (bean instanceof MockDefinition.MockFactoryBean) {
            return bean;
        }
        return super.initializeBean(beanName, bean, mbd);
    }

    /**
     * Ensures that any of our factory beans are never proxied further. Problem: If a class annotated with AOP-stuff
     * is proxied then Spring will believe that the mock should be AOP-ed as well. Which we obviously do not want to do.
     */
    @Override
    protected Object getObjectFromFactoryBean(FactoryBean<?> factory, String beanName, boolean shouldPostProcess) {
        if (factory instanceof MockDefinition.MockFactoryBean) {
            try {
                return factory.getObject();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return super.getObjectFromFactoryBean(factory, beanName, shouldPostProcess);
    }

    /**
     * Confuses Spring to not use any factory beans which would have create a rejected class. This ensures that the
     * {@link FactoryBean#getObject()} is never called.
     */
    @Override
    protected Class<?> getTypeForFactoryBean(FactoryBean<?> factoryBean) {
        Class<?> clazz = super.getTypeForFactoryBean(factoryBean);

        // Remock also uses factoryBeans, if it is one of ours, then of course it ok.
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
