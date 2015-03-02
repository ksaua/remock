package no.saua.remock;

import no.saua.remock.internal.AnnotationVisitor;
import no.saua.remock.internal.MockDefinition;
import no.saua.remock.internal.Rejecter;
import no.saua.remock.internal.SpyDefinition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.List;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReplaceWithSpy {
    Class<?>[] value() default {};

    String[] beanNames() default {};

    public static class ReplaceWithSpyAnnotationVisitor implements AnnotationVisitor<ReplaceWithSpy> {

        @Override
        public void visitClass(ReplaceWithSpy annotation, List<MockDefinition> mocks, List<SpyDefinition> spies,
                        List<Rejecter> rejecters) {
            if (annotation.value().length > 0) {
                for (Class<?> spyClass : annotation.value()) {
                    spies.add(new SpyDefinition(spyClass, null));
                }
            } else if (annotation.beanNames().length > 0) {
                for (String spyBeanName : annotation.beanNames()) {
                    spies.add(new SpyDefinition(null, spyBeanName));
                }
            } else {
                throw new IllegalArgumentException("Either value() or beanNames() must be set for @"
                                + ReplaceWithSpy.class.getSimpleName() + " when annotated on a test-class.");
            }
        }

        @Override
        public void visitField(ReplaceWithSpy annotation, Field field, List<MockDefinition> mocks,
                        List<SpyDefinition> spies, List<Rejecter> rejecters) {
            spies.add(new SpyDefinition(field.getType(), field.getName()));
        }
    }
}
