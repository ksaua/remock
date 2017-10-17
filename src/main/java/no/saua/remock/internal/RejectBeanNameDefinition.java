package no.saua.remock.internal;

import java.util.Objects;

/**
 * Created by knut on 24.02.15.
 */
public class RejectBeanNameDefinition extends Entity<RejectBeanNameDefinition> implements Rejecter {
    private final String beanName;

    public RejectBeanNameDefinition(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public boolean shouldReject(String beanName, Class<?> beanClass) {
        return this.beanName.equals(beanName);
    }

    @Override
    public boolean equals(RejectBeanNameDefinition other) {
        return Objects.equals(beanName, other.beanName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beanName);
    }
}
