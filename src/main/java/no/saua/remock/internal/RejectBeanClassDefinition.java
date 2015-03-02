package no.saua.remock.internal;

import java.util.Objects;

public class RejectBeanClassDefinition extends Entity<RejectBeanClassDefinition> implements Rejecter {
    private final Class<?> rejectClass;

    public RejectBeanClassDefinition(Class<?> rejectClass) {
        assert rejectClass != null;
        this.rejectClass = rejectClass;
    }

    @Override
    public boolean shouldReject(String beanName, Class<?> beanClass) {
        return rejectClass.isAssignableFrom(beanClass);
    }

    @Override
    public boolean equals(RejectBeanClassDefinition other) {
        return Objects.equals(rejectClass, other.rejectClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rejectClass);
    }
}
