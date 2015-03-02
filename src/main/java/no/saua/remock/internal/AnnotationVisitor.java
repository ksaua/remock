package no.saua.remock.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

public interface AnnotationVisitor<T extends Annotation> {
    void visitClass(T annotation, List<MockDefinition> mocks, List<SpyDefinition> spies, List<Rejecter>
            rejecters);

    void visitField(T annotation, Field field, List<MockDefinition> mocks, List<SpyDefinition> spies,
                    List<Rejecter> rejecters);
}
