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
import java.util.Set;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReplaceWithMock {

    public static class ReplaceWithMockAnnotationVisitor implements AnnotationVisitor<ReplaceWithMock> {

        @Override
        public void visitClass(ReplaceWithMock annotation, Set<MockDefinition> mocks, Set<SpyDefinition> spies,
                        Set<Rejecter> rejecters) {

        }

        @Override
        public void visitField(ReplaceWithMock annotation, Field field, Set<MockDefinition> mocks,
                        Set<SpyDefinition> spies, Set<Rejecter> rejecters) {
            MockDefinition mockDefinition = new MockDefinition(field.getName(), field.getType());
            mocks.add(mockDefinition);
            rejecters.add(mockDefinition);
        }
    }
}
