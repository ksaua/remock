package no.saua.remock;

import no.saua.remock.internal.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.Set;

/**
 * Rejects any class with the class {@link #value()} and replaces it with a {@link #with()}. Useful
 * if you want to actually implement a complete mock class. Note that this annotation also is a
 * meta-annotation for {@link Autowired} causing the annotated field to automatically be injected.
 * Usage:
 * 
 * <pre>
 * &#064;RunWith(SpringJUnit4ClassRunner.class)
 * &#064;BootstrapWith(RemockBootstrapper.class)
 * &#064;ContextConfiguration(classes = {ServiceImpl.class})
 * public static class ReplaceWithImplAnnotatedOnFieldTest {
 * 
 *     &#064;ReplaceWithImpl(value = ServiceImpl.class, with = ServiceMock.class)
 *     public Service service;
 * 
 *     &#064;Test
 *     public void test() {
 *         assertEquals(ServiceMock.class, service.getClass());
 *     }
 * }
 * </pre>
 * 
 */
@Autowired
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ReplaceWithImpl.ReplaceWithImpls.class)
public @interface ReplaceWithImpl {

    Class<?> value();

    Class<?> with();

    String beanName() default DEFAULT_BEAN_NAME;

    public static final String DEFAULT_BEAN_NAME = "::DEFAULT::";

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface ReplaceWithImpls {
        ReplaceWithImpl[] value();
    }

    public static class ReplaceWithImplAnnotationVisitor implements AnnotationVisitor<ReplaceWithImpl> {

        @Override
        public void visitClass(ReplaceWithImpl annotation, Set<SpringBeanDefiner> definers, Set<SpyDefinition> spies,
                        Set<Rejecter> rejecters) {
            Class<?> reject = annotation.value();
            Class<?> with = annotation.with();
            if (reject == null || with == null) {
                throw new IllegalArgumentException("Both the class to replace, and the class to replace with "
                                + "must be set for the ReplaceWithImpl annotation to work.");
            }
            String beanName = getBeanName(annotation, reject.getName() + "_ReplaceWithImpl_" + with.getName());
            definers.add(new RegularDefinition(with, beanName));
            rejecters.add(new RejectBeanClassDefinition(reject));
        }

        @Override
        public void visitField(ReplaceWithImpl annotation, Field field, Set<SpringBeanDefiner> definers,
                        Set<SpyDefinition> spies, Set<Rejecter> rejecters) {
            Class<?> reject = annotation.value();
            if (reject == null) {
                throw new IllegalArgumentException("Both the class to replace, and the class to replace with "
                                + "must be set for the ReplaceWithImpl annotation to work.");
            }
            Class<?> with = annotation.with();
            if (with == null) {
                with = field.getType();
            }
            String beanName = getBeanName(annotation, field.getName());
            definers.add(new RegularDefinition(with, beanName));
            rejecters.add(new RejectBeanClassDefinition(reject));
        }

        private String getBeanName(ReplaceWithImpl annotation, String fallbackName) {
            if (!DEFAULT_BEAN_NAME.equals(annotation.beanName())) {
                return annotation.beanName();
            } else {
                return fallbackName;
            }
        }
    }
}
