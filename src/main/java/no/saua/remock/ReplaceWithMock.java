package no.saua.remock;

import no.saua.remock.internal.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Set;

/**
 * Replaces a given field with a Mockito mock. Since the mock does not depend on any other beans it
 * will sever the ties to the rest of the Spring dependency graph. Usage:
 * 
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
public @interface ReplaceWithMock {

    Class<?>[] value() default {};

    public static class ReplaceWithMockAnnotationVisitor implements AnnotationVisitor<ReplaceWithMock> {

        @Override
        public void visitClass(ReplaceWithMock annotation, Set<SpringBeanDefiner> definers, Set<SpyDefinition> spies,
                        Set<Rejecter> rejecters) {
            Class<?>[] value = annotation.value();
            for (Class<?> classToMock : value) {
                String beanName = classToMock.getSimpleName() + "$Mock$";
                definers.add(new MockDefinition(beanName, classToMock));
                rejecters.add(new RejectBeanClassDefinition(classToMock));
            }
        }

        @Override
        public void visitField(ReplaceWithMock annotation, Field field, Set<SpringBeanDefiner> definers,
                        Set<SpyDefinition> spies, Set<Rejecter> rejecters) {
            definers.add(new MockDefinition(field.getName(), field.getType()));
            rejecters.add(new RejectBeanClassDefinition(field.getType()));
        }
    }
}
