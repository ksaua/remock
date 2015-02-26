package no.saua.remock.internal;

import no.saua.remock.Reject;
import no.saua.remock.ReplaceWith;
import no.saua.remock.ReplaceWithMock;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Finds Remock annotations on the test class
 */
public class RemockTestClassAnnotationFinder extends EntityHelper<RemockTestClassAnnotationFinder> {

    private ArrayList<MockDefinition> definers = new ArrayList<>();
    private ArrayList<Rejecter> rejecters = new ArrayList<>();

    public RemockTestClassAnnotationFinder(Class<?> testClass) {
        Reject rejectAnnot = testClass.getAnnotation(Reject.class);
        if (rejectAnnot != null) {
            if (!Reject.DEFAULT_CLASS.equals(rejectAnnot.value())) {
                rejecters.add(new RejectBeanClassDefinition(rejectAnnot.value()));
            } else if (!Reject.DEFAULT_BEAN_NAME.equals(rejectAnnot.beanName())) {
                rejecters.add(new RejectBeanNameDefinition(rejectAnnot.beanName()));
            }
        }

        ReplaceWith replaceWithAnnot = testClass.getAnnotation(ReplaceWith.class);
        if (replaceWithAnnot != null) {
            Class<?> reject= replaceWithAnnot.value();
            Class<?> with = replaceWithAnnot.with();
            if (reject == null || with == null) {
                throw new IllegalArgumentException("Both the class to replace, and the class to replace with " +
                        "must be set for the ReplaceWith annotation to work.");
            }
            MockDefinition mockDefinition = new MockDefinition("meh", with);
            definers.add(mockDefinition);
            rejecters.add(new RejectBeanClassDefinition(reject));
        }

        for (Field field : testClass.getDeclaredFields()) {
            ReplaceWithMock annotation = field.getAnnotation(ReplaceWithMock.class);
            if (annotation != null) {
                MockDefinition mockDefinition = new MockDefinition(field.getName(), field.getType());
                definers.add(mockDefinition);
                rejecters.add(mockDefinition);
            }
            Reject annot2 = field.getAnnotation(Reject.class);
            if (annot2 != null) {
                rejecters.add(new RejectBeanClassDefinition(field.getType()));
            }
        }
    }

    public List<Rejecter> getRejecters() {
        return rejecters;
    }

    public List<MockDefinition> getDefiners() {
        return definers;
    }

    @Override
    public boolean equals(RemockTestClassAnnotationFinder other) {
        return Objects.equals(rejecters, other.rejecters);
    }
}
