package no.saua.remock;

import no.saua.remock.internal.AnnotationVisitor;
import no.saua.remock.internal.MockDefinition;
import no.saua.remock.internal.RejectBeanClassDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.*;
import java.lang.reflect.Field;

/**
 * Replaces a given field with a Mockito mock. Since the mock does not depend on any other beans it
 * will sever the ties to the rest of the Spring dependency graph. Usage:
 * <p>
 * <pre>
 * &#064;RunWith(SpringJUnit4ClassRunner.class)
 * &#064;BootstrapWith(RemockBootstrapper.class)
 * &#064;ContextConfiguration(classes = SomeService.class)
 * public class ReplaceWithMockTest {
 *
 *     &#064;ReplaceWithMock
 *     public SomeDependency someDependency;
 *
 *     &#064;Inject
 *     public SomeService someService;
 *
 *     &#064;Test
 *     public void test() {
 *         when(someDependency.method()).thenReturn(42);
 *         assertEquals(21, someService.getHalf());
 *         assertTrue(isMock(someService));
 *     }
 * }
 * </pre>
 */
@Autowired
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ReplaceWithMock.ReplaceWithMocks.class)
public @interface ReplaceWithMock {

    Class<?>[] value() default {};

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface ReplaceWithMocks {
        ReplaceWithMock[] value();
    }

    public static class ReplaceWithMockAnnotationVisitor implements AnnotationVisitor<ReplaceWithMock> {

        @Override
        public AnnotationVisitorResult visitClass(ReplaceWithMock annotation) {
            AnnotationVisitorResult result = new AnnotationVisitorResult();
            Class<?>[] value = annotation.value();
            for (Class<?> classToMock : value) {
                String beanName = classToMock.getSimpleName() + "$Mock$";
                result.addSpringBeanDefiner(new MockDefinition(beanName, classToMock));
                result.addRejecter(new RejectBeanClassDefinition(classToMock));
            }
            return result;
        }

        @Override
        public AnnotationVisitorResult visitField(ReplaceWithMock annotation, Field field) {
            return new AnnotationVisitorResult()
                    .addSpringBeanDefiner(new MockDefinition(field.getName(), field.getType()))
                    .addRejecter(new RejectBeanClassDefinition(field.getType()));
        }
    }
}
