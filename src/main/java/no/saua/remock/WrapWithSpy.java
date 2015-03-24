package no.saua.remock;

import no.saua.remock.internal.AnnotationVisitor;
import no.saua.remock.internal.Rejecter;
import no.saua.remock.internal.SpringBeanDefiner;
import no.saua.remock.internal.SpyDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Set;

/**
 * Wraps the original spring bean with a Mockito spy. Allowing you to verify calls made on that
 * bean. Note that this annotation also is a meta-annotation for {@link Autowired} causing the spied
 * field to also be injected. Usage:
 * 
 * <pre>
 * &#064;RunWith(SpringJUnit4ClassRunner.class)
 * &#064;BootstrapWith(RemockBootstrapper.class)
 * &#064;ContextConfiguration(classes = SomeService.class)
 * public class ReplaceWithMockTest {
 * 
 *     &#064;WrapWithSpy
 *     public SomeDependency someDependency;
 * 
 *     &#064;Inject
 *     public SomeService someService;
 * 
 *     &#064;Test
 *     public void test() {
 *         someService.getHalf()
 *         verify(someDependency).method();
 *     }
 * }
 * 
 * </pre>
 */
@Autowired
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface WrapWithSpy {
    Class<?>[] value() default {};

    String[] beanNames() default {};

    public static class ReplaceWithSpyAnnotationVisitor implements AnnotationVisitor<WrapWithSpy> {

        @Override
        public void visitClass(WrapWithSpy annotation, Set<SpringBeanDefiner> mocks, Set<SpyDefinition> spies,
                        Set<Rejecter> rejecters) {
            if (annotation.value().length > 0) {
                for (Class<?> spyClass : annotation.value()) {
                    spies.add(new SpyDefinition(spyClass, null));
                }
            } else if (annotation.beanNames().length > 0) {
                for (String spyBeanName : annotation.beanNames()) {
                    spies.add(new SpyDefinition(null, spyBeanName));
                }
            } else {
                throw new IllegalArgumentException("Either value() or beanNames() must be set for @"
                                + WrapWithSpy.class.getSimpleName() + " when annotated on a test-class.");
            }
        }

        @Override
        public void visitField(WrapWithSpy annotation, Field field, Set<SpringBeanDefiner> mocks,
                        Set<SpyDefinition> spies, Set<Rejecter> rejecters) {
            spies.add(new SpyDefinition(field.getType(), field.getName()));
        }
    }
}
