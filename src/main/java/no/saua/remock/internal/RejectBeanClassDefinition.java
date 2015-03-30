package no.saua.remock.internal;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RejectBeanClassDefinition extends Entity<RejectBeanClassDefinition> implements Rejecter {

    private final Class<?> rejectClass;
    private final List<Class<?>> exceptClasses;

    public RejectBeanClassDefinition(Class<?> rejectClass) {
        this(rejectClass, Collections.<Class<?>>emptyList());
    }

    public RejectBeanClassDefinition(Class<?> rejectClass, List<Class<?>> exceptClasses) {
        assert rejectClass != null;
        assert exceptClasses != null;
        this.rejectClass = rejectClass;
        this.exceptClasses = exceptClasses;
    }

    @Override
    public boolean shouldReject(String beanName, Class<?> beanClass) {
        if (!rejectClass.isAssignableFrom(beanClass)) {
            // -> Class does not match
            return false;
        }

        // E-> Class matches
        for (Class<?> clazz : exceptClasses) {
            if (clazz.isAssignableFrom(beanClass)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean equals(RejectBeanClassDefinition other) {
        return Objects.equals(rejectClass, other.rejectClass) && Objects.equals(exceptClasses, other.exceptClasses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rejectClass, exceptClasses);
    }
}
