package no.saua.remock.internal;

public class RejectBeanClassDefinition implements Rejecter {
    private final Class<?> rejectClass;

    public RejectBeanClassDefinition(Class<?> rejectClass) {
        this.rejectClass = rejectClass;
    }

    @Override
    public boolean shouldReject(String beanName, Class<?> beanClass) {
        return rejectClass.isAssignableFrom(beanClass);
    }
}
