package no.saua.remock.internal;

import no.saua.remock.*;
import no.saua.remock.Reject.RejectAnnotationVisitor;
import no.saua.remock.ReplaceWithImpl.ReplaceWithImplAnnotationVisitor;
import no.saua.remock.ReplaceWithMock.ReplaceWithMockAnnotationVisitor;
import no.saua.remock.WrapWithSpy.ReplaceWithSpyAnnotationVisitor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Finds Remock annotations on the test class, creates a {@link no.saua.remock.internal.RemockAnnotationFinder.RemockConfiguration}.
 */
public class RemockAnnotationFinder {

    private static final ConcurrentHashMap<Class<?>, RemockConfiguration> cache = new ConcurrentHashMap<>();

    private static Map<Class<? extends Annotation>, AnnotationVisitor> annotationReaders;

    static {
        annotationReaders = new HashMap<>();
        annotationReaders.put(Reject.class, new RejectAnnotationVisitor());
        annotationReaders.put(ReplaceWithImpl.class, new ReplaceWithImplAnnotationVisitor());
        annotationReaders.put(WrapWithSpy.class, new ReplaceWithSpyAnnotationVisitor());
        annotationReaders.put(ReplaceWithMock.class, new ReplaceWithMockAnnotationVisitor());
    }

    public static class RemockConfiguration extends Entity<RemockConfiguration> {
        private boolean foundDisableLazyInitAnnotation = false;
        private Set<SpringBeanDefiner> definers = new HashSet<>();
        private Set<SpyDefinition> spies = new HashSet<>();
        private Set<Rejecter> rejecters = new HashSet<>();

        public boolean foundDisableLazyInitAnnotation() {
            return foundDisableLazyInitAnnotation;
        }

        public Set<Rejecter> getRejecters() {
            return rejecters;
        }

        public Set<SpringBeanDefiner> getDefiners() {
            return definers;
        }

        public Set<SpyDefinition> getSpies() {
            return spies;
        }

        @Override
        public boolean equals(RemockConfiguration other) {
            return Objects.equals(foundDisableLazyInitAnnotation, other.foundDisableLazyInitAnnotation)
                    && Objects.equals(definers, other.definers) && Objects.equals(rejecters, other.rejecters)
                    && Objects.equals(spies, other.spies);
        }

        @Override
        public int hashCode() {
            return Objects.hash(rejecters, definers, spies, foundDisableLazyInitAnnotation);
        }

        public RemockConfiguration mergeWith(RemockConfiguration other) {
            RemockConfiguration result = new RemockConfiguration();
            result.definers.addAll(definers);
            result.definers.addAll(other.definers);
            result.spies.addAll(spies);
            result.spies.addAll(other.spies);
            result.rejecters.addAll(rejecters);
            result.rejecters.addAll(other.rejecters);
            result.foundDisableLazyInitAnnotation = foundDisableLazyInitAnnotation || other.foundDisableLazyInitAnnotation;
            return result;
        }
    }


    public static RemockConfiguration findFor(Class<?> clazz) {
        if (cache.containsKey(clazz)) {
            return cache.get(clazz);
        }

        RemockConfiguration result = new RemockConfiguration();

        // :: Find configuration present on the current class
        if (clazz.getAnnotation(DisableLazyInit.class) != null) {
            result.foundDisableLazyInitAnnotation = true;
        }
        for (Map.Entry<Class<? extends Annotation>, AnnotationVisitor> entry : annotationReaders.entrySet()) {
            for (Annotation annot: clazz.getAnnotationsByType(entry.getKey())) {
                entry.getValue().visitClass(annot, result.definers, result.spies, result.rejecters);
            }
        }

        for (Field field : clazz.getDeclaredFields()) {
            for (Map.Entry<Class<? extends Annotation>, AnnotationVisitor> entry : annotationReaders.entrySet()) {
                Annotation annotation = field.getAnnotation(entry.getKey());
                if (annotation != null) {
                    entry.getValue().visitField(annotation, field, result.definers, result.spies, result.rejecters);
                }
            }
        }

        // :: Merge with configuration present on the super class
        if (!clazz.getSuperclass().equals(Object.class)) {
            result = result.mergeWith(findFor(clazz.getSuperclass()));
        }

        // :: Merge from all classes found in the RemockContextConfiguration annotation
        RemockContextConfiguration annotation = clazz.getAnnotation(RemockContextConfiguration.class);
        if (annotation != null) {
            for (Class contextClazz: Arrays.asList(annotation.value())) {
                result = result.mergeWith(findFor(contextClazz));
            }
        }

        // Insert into cache and return
        cache.put(clazz, result);
        return result;
    }

}
