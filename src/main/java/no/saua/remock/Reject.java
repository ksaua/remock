package no.saua.remock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Reject {
    public static final String DEFAULT_BEAN_NAME = "^$$DEFAULT$$^";

    Class<?> value() default Reject.class;
    String beanName() default DEFAULT_BEAN_NAME;
}
