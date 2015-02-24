package no.saua.remock.internal;

public interface Rejecter {
    boolean shouldReject(String beanName, Class<?> bean);
}
