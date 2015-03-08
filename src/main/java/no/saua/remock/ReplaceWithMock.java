package no.saua.remock;

import no.saua.remock.internal.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Set;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReplaceWithMock {

    Class<?>[] value() default {};

    public static class ReplaceWithMockAnnotationVisitor implements AnnotationVisitor<ReplaceWithMock> {

        @Override
        public void visitClass(ReplaceWithMock annotation, Set<SpringBeanDefiner> definers, Set<SpyDefinition> spies,
                        Set<Rejecter> rejecters) {
            Class<?>[] value = annotation.value();
            for (Class<?> classToMock: value) {
                String beanName = classToMock.getSimpleName() + "$Mock$";
                definers.add(new MockDefinition(beanName, classToMock));
                rejecters.add(new RejectBeanClassDefinition(classToMock));
            }
        }

        @Override
        public void visitField(ReplaceWithMock annotation, Field field, Set<SpringBeanDefiner> definers,
                        Set<SpyDefinition> spies, Set<Rejecter> rejecters) {
            definers.add(new MockDefinition(field.getName(), field.getType()));
            rejecters.add(new RejectBeanClassDefinition(field.getType()));
        }
    }
}
