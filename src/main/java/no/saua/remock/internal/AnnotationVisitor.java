package no.saua.remock.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public interface AnnotationVisitor<T extends Annotation> {
    AnnotationVisitorResult visitClass(T annotation);

    AnnotationVisitorResult visitField(T annotation, Field field);

    public static class AnnotationVisitorResult {
        private Set<SpringBeanDefiner> definers = new HashSet<>();
        private Set<SpyDefinition> spies = new HashSet<>();
        private Set<Rejecter> rejecters = new HashSet<>();

        public AnnotationVisitorResult addSpringBeanDefiner(SpringBeanDefiner springBeanDefiner) {
            definers.add(springBeanDefiner);
            return this;
        }

        public AnnotationVisitorResult addSpy(SpyDefinition spyDefinition) {
            spies.add(spyDefinition);
            return this;
        }

        public AnnotationVisitorResult addRejecter(Rejecter rejecter) {
            rejecters.add(rejecter);
            return this;
        }

        Set<SpringBeanDefiner> getDefiners() {
            return definers;
        }

        Set<SpyDefinition> getSpies() {
            return spies;
        }

        Set<Rejecter> getRejecters() {
            return rejecters;
        }
    }
}
