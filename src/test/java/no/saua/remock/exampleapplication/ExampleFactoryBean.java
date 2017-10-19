package no.saua.remock.exampleapplication;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Service;

@Service
public class ExampleFactoryBean implements FactoryBean<ExampleFactoryBean.BeanFromFactoryBean> {
    @Override
    public BeanFromFactoryBean getObject() throws Exception {
        return new BeanFromFactoryBean();
    }

    @Override
    public Class<BeanFromFactoryBean> getObjectType() {
        return BeanFromFactoryBean.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    public static class BeanFromFactoryBean {
        public String someMethod() {
            return null;
        }
    }
}
