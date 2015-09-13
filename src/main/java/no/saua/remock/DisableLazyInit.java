package no.saua.remock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotated on test-class this will disable Remock from telling spring to lazily initialize all beans, or the beans
 * matching the class/beanNames specifying the annotation.
 * <p>
 * In other words, the following will disable lazy init for all beans:
 * <pre>
 * &#064;DisableLazyInit
 * </pre>
 * And this will only disable it for services matching the given class or bean name:
 * <pre>
 * &#064;DisableLazyInit(value=MyEagerService.class, beanName="myOtherEagerService")
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DisableLazyInit {
    Class<?>[] value() default {};
    String[] beanName() default {};
}
