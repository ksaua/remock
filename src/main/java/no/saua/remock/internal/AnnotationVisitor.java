package no.saua.remock.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Set;

public interface AnnotationVisitor<T extends Annotation> {
    void visitClass(T annotation, Set<SpringBeanDefiner> mocks, Set<SpyDefinition> spies, Set<Rejecter>
            rejecters);

    void visitField(T annotation, Field field, Set<SpringBeanDefiner> mocks, Set<SpyDefinition> spies,
                    Set<Rejecter> rejecters);
}