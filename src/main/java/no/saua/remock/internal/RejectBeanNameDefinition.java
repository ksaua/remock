package no.saua.remock.internal;

/**
 * Created by knut on 24.02.15.
 */
public class RejectBeanNameDefinition implements Rejecter {
    private final String beanName;

    public RejectBeanNameDefinition(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public boolean shouldReject(String beanName, Class<?> beanClass) {
        return beanName.equals(this.beanName);
    }
}
