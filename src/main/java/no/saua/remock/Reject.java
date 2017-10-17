package no.saua.remock;

import no.saua.remock.internal.*;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;

/**
 * Reject beans, causing them not to be created spring. Can either be annotated a test-class or on a
 * field. Annotating on the test-class requires either {@link #beanName} or {@link #value()} to be
 * set. Annotating on a field will cause Remock to automatically use the field's type.
 * <p>
 * A typical use-case is when you have optional dependencies either by @Autowired(required = false)
 * or through something like {@literal @Inject List<SomeInterface>}.
 * </p>
 * <p>
 * Examples:
 * </p>
 * This will reject every bean which are either SomeDependency.class, or a sub-class of
 * SomeDependency.class. If SomeDependency.class is an interface, then this will reject every
 * implementation of SomeDependency.
 * 
 * <pre>
 *
 * &#064;BootstrapWith(RemockBootstrapper.class)
 * &#064;ContextConfiguration(classes = SomeClass.class)
 * &#064;Reject(SomeDependency.class)
 * public class MyTest {
 * }
 * </pre>
 * 
 * This is equivalent to the example above.
 * 
 * <pre>
 * &#064;BootstrapWith(RemockBootstrapper.class)
 * &#064;ContextConfiguration(classes = SomeClass.class)
 * public class MyTest {
 *     &#064;Reject
 *     public SomeDependency object;
 * }
 * </pre>
 *
 * Alternatively you can inject by bean name.
 *
 * <pre>
 *
 * &#064;BootstrapWith(RemockBootstrapper.class)
 * &#064;ContextConfiguration(classes = SomeClass.class)
 * &#064;Reject(beanName = "myBean")
 * public class MyTest {
 * }
 * </pre>
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Reject.Rejects.class)
public @interface Reject {

    public static final String DEFAULT_BEAN_NAME = "^$$DEFAULT$$^";

    Class<?>[] value() default {};

    String beanName() default DEFAULT_BEAN_NAME;

    /**
     * Reject all classes except for classes of this type.
     */
    Class<?>[] except() default {};

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Rejects {
        Reject[] value();
    }

    public static class RejectAnnotationVisitor implements AnnotationVisitor<Reject> {
        @Override
        public AnnotationVisitorResult visitClass(Reject annotation) {
            AnnotationVisitorResult result = new AnnotationVisitorResult();
            Class<?>[] rejectClasses = annotation.value();
            Class<?>[] exceptClasses = annotation.except();
            if (rejectClasses.length > 0) {
                for (Class<?> rejectClass : rejectClasses) {
                    result.addRejecter(new RejectBeanClassDefinition(rejectClass, Arrays.asList(exceptClasses)));
                }
            } else if (!Reject.DEFAULT_BEAN_NAME.equals(annotation.beanName())) {
                result.addRejecter(new RejectBeanNameDefinition(annotation.beanName()));
            }
            return result;
        }

        @Override
        public AnnotationVisitorResult visitField(Reject annot, Field field) {
            return new AnnotationVisitorResult().addRejecter(new RejectBeanClassDefinition(field.getType()));
        }
    }

}
