package no.saua.remock.internal;

import no.saua.remock.Reject;
import no.saua.remock.Reject.RejectAnnotationVisitor;
import no.saua.remock.ReplaceWithImpl;
import no.saua.remock.ReplaceWithImpl.ReplaceWithImplAnnotationVisitor;
import no.saua.remock.ReplaceWithMock;
import no.saua.remock.ReplaceWithMock.ReplaceWithMockAnnotationVisitor;
import no.saua.remock.ReplaceWithSpy;
import no.saua.remock.ReplaceWithSpy.ReplaceWithSpyAnnotationVisitor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;


/**
 * Finds Remock annotations on the test class
 */
public class RemockTestClassAnnotationFinder extends Entity<RemockTestClassAnnotationFinder> {

    private static Map<Class<? extends Annotation>, AnnotationVisitor> annotationReaders;

    static {
        annotationReaders = new HashMap<>();
        annotationReaders.put(Reject.class, new RejectAnnotationVisitor());
        annotationReaders.put(ReplaceWithImpl.class, new ReplaceWithImplAnnotationVisitor());
        annotationReaders.put(ReplaceWithSpy.class, new ReplaceWithSpyAnnotationVisitor());
        annotationReaders.put(ReplaceWithMock.class, new ReplaceWithMockAnnotationVisitor());
    }

    private Set<MockDefinition> mocks = new HashSet<>();
    private Set<SpyDefinition> spies = new HashSet<>();
    private Set<Rejecter> rejecters = new HashSet<>();

    public RemockTestClassAnnotationFinder(Class<?> testClass) {
        // :: Find super classes
        List<Class<?>> classesToTest = new LinkedList<>();
        Class<?> currentClass = testClass;
        do {
            classesToTest.add(currentClass);
            currentClass = currentClass.getSuperclass();
        } while (currentClass != null);

        for (Class<?> clazz: classesToTest) {
            for (Map.Entry<Class<? extends Annotation>, AnnotationVisitor> entry : annotationReaders.entrySet()) {
                if (clazz.getAnnotation(entry.getKey()) != null) {
                    entry.getValue().visitClass(clazz.getAnnotation(entry.getKey()), mocks, spies, rejecters);
                }
            }

            for (Field field : clazz.getDeclaredFields()) {
                for (Map.Entry<Class<? extends Annotation>, AnnotationVisitor> entry : annotationReaders.entrySet()) {
                    Annotation annotation = field.getAnnotation(entry.getKey());
                    if (annotation != null) {
                        entry.getValue().visitField(annotation, field, mocks, spies, rejecters);
                    }
                }
            }
        }
    }

    public Set<Rejecter> getRejecters() {
        return rejecters;
    }

    public Set<MockDefinition> getMocks() {
        return mocks;
    }

    public Set<SpyDefinition> getSpies() {
        return spies;
    }

    @Override
    public boolean equals(RemockTestClassAnnotationFinder other) {
        return Objects.equals(rejecters, other.rejecters);
    }
}
