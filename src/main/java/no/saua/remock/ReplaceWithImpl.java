package no.saua.remock;

import no.saua.remock.internal.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Set;

/**
 * Rejects any class with the class {@link #value()} and replaces it with a {@link #with()}. Useful
 * if you want to actually implement a complete mock class.
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReplaceWithImpl {

    public Class<?> value();

    public Class<?> with();

    public String beanName() default DEFAULT_BEAN_NAME;

    static final String DEFAULT_BEAN_NAME = "::DEFAULT::";

    public static class ReplaceWithImplAnnotationVisitor implements AnnotationVisitor<ReplaceWithImpl> {

        @Override
        public void visitClass(ReplaceWithImpl annotation, Set<SpringBeanDefiner> definers, Set<SpyDefinition> spies,
                        Set<Rejecter> rejecters) {
            Class<?> reject = annotation.value();
            Class<?> with = annotation.with();
            if (reject == null || with == null) {
                throw new IllegalArgumentException("Both the class to replace, and the class to replace with "
                                + "must be set for the ReplaceWithImpl annotation to work.");
            }
            String beanName = getBeanName(annotation, reject.getName() + "_ReplaceWithImpl_" + with.getName());
            definers.add(new RegularDefinition(with, beanName));
            rejecters.add(new RejectBeanClassDefinition(reject));
        }

        @Override
        public void visitField(ReplaceWithImpl annotation, Field field, Set<SpringBeanDefiner> definers,
                        Set<SpyDefinition> spies, Set<Rejecter> rejecters) {
            Class<?> reject = annotation.value();
            if (reject == null) {
                throw new IllegalArgumentException("Both the class to replace, and the class to replace with "
                                + "must be set for the ReplaceWithImpl annotation to work.");
            }
            Class<?> with = annotation.with();
            if (with == null) {
                with = field.getType();
            }
            String beanName = getBeanName(annotation, field.getName());
            definers.add(new RegularDefinition(with, beanName));
            rejecters.add(new RejectBeanClassDefinition(reject));
        }

        private String getBeanName(ReplaceWithImpl annotation, String fallbackName) {
            if (!DEFAULT_BEAN_NAME.equals(annotation.beanName())) {
                return annotation.beanName();
            } else {
                return fallbackName;
            }
        }
    }
}
