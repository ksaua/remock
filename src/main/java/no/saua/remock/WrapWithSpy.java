package no.saua.remock;

import no.saua.remock.internal.AnnotationVisitor;
import no.saua.remock.internal.SpyDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.*;
import java.lang.reflect.Field;

/**
 * Wraps the original spring bean with a Mockito spy. Allowing you to verify calls made on that
 * bean. Note that this annotation also is a meta-annotation for {@link Autowired} causing the spied
 * field to also be injected. Usage:
 * <p>
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
@Repeatable(WrapWithSpy.WrapWithSpies.class)
public @interface WrapWithSpy {
    Class<?>[] value() default {};

    String[] beanNames() default {};

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface WrapWithSpies {
        WrapWithSpy[] value();
    }

    public static class ReplaceWithSpyAnnotationVisitor implements AnnotationVisitor<WrapWithSpy> {

        @Override
        public AnnotationVisitorResult visitClass(WrapWithSpy annotation) {
            AnnotationVisitorResult result = new AnnotationVisitorResult();
            if (annotation.value().length > 0) {
                for (Class<?> spyClass : annotation.value()) {
                    result.addSpy(new SpyDefinition(spyClass, null));
                }
            } else if (annotation.beanNames().length > 0) {
                for (String spyBeanName : annotation.beanNames()) {
                    result.addSpy(new SpyDefinition(null, spyBeanName));
                }
            } else {
                throw new IllegalArgumentException("Either value() or beanNames() must be set for @"
                        + WrapWithSpy.class.getSimpleName() + " when annotated on a test-class.");
            }
            return result;
        }

        @Override
        public AnnotationVisitorResult visitField(WrapWithSpy annotation, Field field) {
            return new AnnotationVisitorResult()
                    .addSpy(new SpyDefinition(field.getType(), field.getName()));
        }
    }
}
