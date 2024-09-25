package no.saua.remock;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import no.saua.remock.ReplaceWithMockQualifierTest.ClassUsingManySomeClasses;
import no.saua.remock.ReplaceWithMockQualifierTest.QualifierConfiguration;

/**
 * Verifies correct injection/autowiring when utilizing {@link ReplaceWithMock} to replace multiple instances of the
 * same class while also utilizing {@link Qualifier} to separate the mock instances.
 * <p>
 * A "common" example would be when mocking a class which injects/autowires multiple {@link javax.sql.DataSource}s and
 * differentiating the datasource's by qualifying the injection.
 * <p>
 * This class showcases the {@link Named} approach from a central DI class, however, the following class being picked
 * up by Springs auto detect ({@link ComponentScan}) features i.e {@link org.springframework.stereotype.Service} would
 * also work:
 * <pre>
 *     &#064;Service
 *     public static class ClassUsingManySomeClasses {
 *         public final SomeClass someClass;
 *         public final SomeClass someClassButDifferent;
 *
 *         &#064;Inject / &#064;AutoWired
 *         public ClassUsingManySomeClasses(SomeClass someClass,
 *         &#064;Qualifier("someSomeClass") SomeClass someClassButDifferent) {
 *             this.someClass = someClass;
 *             this.someClassButDifferent = someClassButDifferent;
 *         }
 *     }
 * </pre>
 *
 * @author Kevin Mc Tiernan, 2024-09-25, kevin.mc.tiernan@storebrand.no
 */
@RunWith(SpringJUnit4ClassRunner.class)
@BootstrapWith(RemockBootstrapper.class)
@DisableLazyInit({ ClassUsingManySomeClasses.class })
@ContextConfiguration(classes = QualifierConfiguration.class)
public class ReplaceWithMockQualifierTest {

    @ReplaceWithMock
    private SomeClass _someClass;
    @ReplaceWithMock
    @Qualifier("someSomeClass")
    private SomeClass _thisIsTheFieldYourLookingFor;

    @Inject
    private ClassUsingManySomeClasses _classUsingManySomeClasses;


    @Test
    public void verifyQualifiedBeanDiffersFromNonQualified() {
        Assert.assertNotEquals(_classUsingManySomeClasses.someClass, _classUsingManySomeClasses.someClassButDifferent);
        Assert.assertEquals(_thisIsTheFieldYourLookingFor, _classUsingManySomeClasses.someClassButDifferent);
        Assert.assertEquals(_someClass, _classUsingManySomeClasses.someClass);

        Mockito.when(_someClass.someReturnString()).thenReturn("SomeClass");
        Mockito.when(_thisIsTheFieldYourLookingFor.someReturnString()).thenReturn("ImDifferent");

        Assert.assertNotEquals(_someClass.someReturnString(), _thisIsTheFieldYourLookingFor.someReturnString());
    }

    @Configuration
    public static class QualifierConfiguration {
        @Bean
        public ClassUsingManySomeClasses classUsingManySomeClasses(
                SomeClass someClass,
                @Named("someSomeClass") SomeClass someClassButDifferent) {
            return new ClassUsingManySomeClasses(someClass, someClassButDifferent);
        }

        @Bean
        @Primary
        public SomeClass someClass() {
            return new SomeClass();
        }

        @Bean("someSomeClass")
        public SomeClass someSomeClass() {
            return new SomeClass();
        }
    }

    public static class SomeClass {

        public String someReturnString() {
            return null;
        }
    }

    public static class ClassUsingManySomeClasses {
        public final SomeClass someClass;
        public final SomeClass someClassButDifferent;

        public ClassUsingManySomeClasses(SomeClass someClass, SomeClass someClassButDifferent) {
            this.someClass = someClass;
            this.someClassButDifferent = someClassButDifferent;
        }
    }
}
