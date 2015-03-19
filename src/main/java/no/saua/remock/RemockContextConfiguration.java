package no.saua.remock;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Pulls in mock/spy/reject configurations from the specified classes.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RemockContextConfiguration {
    Class<?>[] value();
}
