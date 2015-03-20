package no.saua.remock;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Pulls in mock/spy/reject configurations from the specified classes. Usage:
 *
 * <pre>
 *
 * &#064;RunWith(SpringJUnit4ClassRunner.class)
 * &#064;BootstrapWith(RemockBootstrapper.class)
 * &#064;ContextConfiguration(classes = {SomeServiceWithDependencies.class, SomeDependency.class})
 * &#064;RemockContextConfiguration(MyRemockConfig.class)
 * public class RemockContextConfigurationTest {
 * 
 *     &#064;Inject
 *     private SomeServiceWithDependencies someServiceWithDependencies;
 * 
 *     &#064;Test
 *     public void test() {
 *         assertTrue(isMock(someServiceWithDependencies.getDependency()));
 *     }
 * 
 *     &#064;ReplaceWithMock(SomeDependency.class)
 *     &#064;Reject(SomeOtherDependency.class)
 *     public static class MyRemockConfig {
 *     }
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RemockContextConfiguration {
    Class<?>[] value();
}
