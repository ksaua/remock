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
 * Finds Remock annotations on the test class, creates a {@link no.saua.remock.internal.RemockConfiguration}.
 */
public class RemockConfiguration extends Entity<RemockConfiguration> {

    private static final ConcurrentHashMap<Class<?>, RemockConfiguration> cache = new ConcurrentHashMap<>();

    private static Map<Class<? extends Annotation>, AnnotationVisitor> annotationReaders;

    static {
        annotationReaders = new HashMap<>();
        annotationReaders.put(Reject.class, new RejectAnnotationVisitor());
        annotationReaders.put(ReplaceWithImpl.class, new ReplaceWithImplAnnotationVisitor());
        annotationReaders.put(WrapWithSpy.class, new ReplaceWithSpyAnnotationVisitor());
        annotationReaders.put(ReplaceWithMock.class, new ReplaceWithMockAnnotationVisitor());
    }

    private boolean disableLazyInit = false;
    private Set<SpringBeanDefiner> definers = new HashSet<>();
    private Set<SpyDefinition> spies = new HashSet<>();
    private Set<Rejecter> rejecters = new HashSet<>();
    private Set<String> eagerBeanNames = new HashSet<>();
    private Set<Class<?>> eagerBeanClasses = new HashSet<>();

    public boolean disableLazyInit() {
        return disableLazyInit;
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

    public Set<String> getEagerBeanNames() {
        return eagerBeanNames;
    }

    public Set<Class<?>> getEagerBeanClasses() {
        return eagerBeanClasses;
    }

    @Override
    public boolean equals(RemockConfiguration other) {
        return Objects.equals(disableLazyInit, other.disableLazyInit)
                && Objects.equals(definers, other.definers)
                && Objects.equals(rejecters, other.rejecters)
                && Objects.equals(spies, other.spies)
                && Objects.equals(eagerBeanNames, other.eagerBeanNames)
                && Objects.equals(eagerBeanClasses, other.eagerBeanClasses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rejecters, definers, spies, disableLazyInit, eagerBeanNames, eagerBeanClasses);
    }

    public RemockConfiguration mergeWith(RemockConfiguration other) {
        RemockConfiguration result = new RemockConfiguration();
        result.definers.addAll(definers);
        result.definers.addAll(other.definers);
        result.spies.addAll(spies);
        result.spies.addAll(other.spies);
        result.rejecters.addAll(rejecters);
        result.rejecters.addAll(other.rejecters);
        result.disableLazyInit = disableLazyInit || other.disableLazyInit;
        result.eagerBeanClasses.addAll(eagerBeanClasses);
        result.eagerBeanClasses.addAll(other.eagerBeanClasses);
        result.eagerBeanNames.addAll(eagerBeanNames);
        result.eagerBeanNames.addAll(other.eagerBeanNames);
        return result;
    }


    public static RemockConfiguration findFor(Class<?> clazz) {
        if (cache.containsKey(clazz)) {
            return cache.get(clazz);
        }

        RemockConfiguration result = new RemockConfiguration();

        // :: Find configuration present on the current class
        DisableLazyInit disableLazyInit = clazz.getAnnotation(DisableLazyInit.class);
        if (disableLazyInit != null) {
             if (disableLazyInit.value().length != 0 || disableLazyInit.beanName().length != 0) {
                 result.eagerBeanClasses = new HashSet<>(Arrays.asList(disableLazyInit.value()));
                 result.eagerBeanNames = new HashSet<>(Arrays.asList(disableLazyInit.beanName()));
             } else {
                result.disableLazyInit = true;
             }
        }

        // :: Get the rejecters/definers/spies from the annotation
        List<AnnotationVisitor.AnnotationVisitorResult> annotationVisitorResults = new ArrayList<>();
        for (Map.Entry<Class<? extends Annotation>, AnnotationVisitor> entry : annotationReaders.entrySet()) {
            for (Annotation annot: clazz.getAnnotationsByType(entry.getKey())) {
                annotationVisitorResults.add(entry.getValue().visitClass(annot));
            }
        }

        for (Field field : clazz.getDeclaredFields()) {
            for (Map.Entry<Class<? extends Annotation>, AnnotationVisitor> entry : annotationReaders.entrySet()) {
                Annotation annotation = field.getAnnotation(entry.getKey());
                if (annotation != null) {
                    annotationVisitorResults.add(entry.getValue().visitField(annotation, field));
                }
            }
        }

        for (AnnotationVisitor.AnnotationVisitorResult annotationVisitorResult : annotationVisitorResults) {
            result.definers.addAll(annotationVisitorResult.getDefiners());
            result.spies.addAll(annotationVisitorResult.getSpies());
            result.rejecters.addAll(annotationVisitorResult.getRejecters());
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
