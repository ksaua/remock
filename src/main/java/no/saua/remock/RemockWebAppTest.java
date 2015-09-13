package no.saua.remock;

import org.springframework.test.context.web.WebAppConfiguration;

import java.lang.annotation.*;

/**
 * Remock version of {@link WebAppConfiguration}
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RemockWebAppTest {

    /**
     * @see WebAppConfiguration#value()
     */
    String value() default "src/main/webapp";

}
