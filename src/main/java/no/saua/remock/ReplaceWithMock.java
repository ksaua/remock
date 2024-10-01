package no.saua.remock;

import no.saua.remock.internal.AnnotationVisitor;
import no.saua.remock.internal.MockDefinition;
import no.saua.remock.internal.RejectBeanClassDefinition;
import no.saua.remock.internal.RejectBeanNameDefinition;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;
import java.lang.reflect.Field;

/**
 * Replaces a given field with a Mockito mock. Since the mock does not depend on any other beans it will sever the ties
 * to the rest of the Spring dependency graph. Usage:
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
@Target({ ElementType.TYPE, ElementType.FIELD })
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
                result.addSpringBeanDefiner(new MockDefinition(beanName, classToMock, true));
                result.addRejecter(new RejectBeanClassDefinition(classToMock));
            }
            return result;
        }

        @Override
        public AnnotationVisitorResult visitField(ReplaceWithMock annotation, Field field) {
            Qualifier qualified = field.getAnnotation(Qualifier.class);
            boolean isQualifiedAnnotationInUse = qualified != null;
            // ?: Is @Qualifier utilized on the field?
            // -> Yes, then utilize the value provided by the annotation as the bean name.
            // -> No, then simply use the field name as the bean name.
            String beanName = isQualifiedAnnotationInUse ? qualified.value() : field.getName();
            return new AnnotationVisitorResult()
                    // If @Qualifier is utilized one can assume that this bean is one of many beans of the same class.
                    // Thus, we can assume that any field annotated with @Qualifier is not to be considered the primary
                    // Spring bean for injection.
                    .addSpringBeanDefiner(new MockDefinition(beanName, field.getType(), !isQualifiedAnnotationInUse))
                    // For beans which are named via @Qualifier we should rely on the bean name for rejection instead of
                    // the type. This is because the bean in question might be created via a FactoryBean. If this is the
                    // case then, we need to utilize the beanName as the types will differ when registering the bean
                    // definition.
                    .addRejecter(isQualifiedAnnotationInUse
                            ? new RejectBeanNameDefinition(beanName)
                            : new RejectBeanClassDefinition(field.getType()));
        }
    }
}
