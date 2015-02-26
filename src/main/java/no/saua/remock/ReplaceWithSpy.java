package no.saua.remock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReplaceWithSpy {
    public static final String DEFAULT_BEAN_NAME = "^$$DEFAULT$$^";

    Class<?> value() default ReplaceWithSpy.class;
    String beanName() default DEFAULT_BEAN_NAME;
}
