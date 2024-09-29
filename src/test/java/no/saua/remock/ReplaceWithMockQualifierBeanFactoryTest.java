package no.saua.remock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.test.context.ContextConfiguration;

import no.saua.remock.ReplaceWithMockQualifierBeanFactoryTest.CampaignConfiguration;
import no.saua.remock.ReplaceWithMockQualifierBeanFactoryTest.TheAnnotation.TheAnnotations;

/**
 * Similar to {@link ReplaceWithMockQualifierTest}, however, this test verifies that utilizing {@link ReplaceWithMock}
 * with {@link Qualifier} also handles beans being created by a {@link FactoryBean}. In these cases the bean name will
 * be linked to the {@link BeanDefinition} of the {@link FactoryBean} not the underlying type which the
 * {@link FactoryBean}. The bean name isn't connected to the type created by the FactoryBean until the factory has
 * produced an instance.
 * <p>
 * To summarize, this test verifies that the {@link BeanDefinition} for the field annotated with
 * {@link Qualifier &#064;Qualifier("Steve")} isn't override by the {@link TheRealAnnotation_Factory} type. Step by
 * step:
 * <ul>
 *     <li>Remock registered {@link BeanDefinition} for the field _theRealRealAnnotation of the type
 *     {@link TheRealAnnotation} and named "Steve"</li>
 *     <li>The {@link Configuration} class is processed {@link CampaignConfiguration} and thus the
 *     {@link TheAnnotation}s are processed.</li>
 *     <li>{@link TheAnnotation} are processed via {@link TheAnnotationSetup} which will attempt to register
 *     {@link BeanDefinition}s with the underlying {@link BeanFactory}</li>
 *     <li>The underlying {@link BeanFactory} should reject both because of type and because of the bean name for the
 *     instance of "Steve"</li>
 * </ul>
 *
 * @author Kevin Mc Tiernan, 2024-09-29, kevin.mc.tiernan@storebrand.no
 */
@ContextConfiguration(classes = CampaignConfiguration.class)
public class ReplaceWithMockQualifierBeanFactoryTest extends CommonTest {

    @ReplaceWithMock
    private TheRealAnnotation _theRealAnnotation;
    @ReplaceWithMock
    @Qualifier("Steve")
    private TheRealAnnotation _theRealRealAnnotation;

    @Inject
    private TheCampaign _theCampaign;

    @Test
    public void verifyQualifiedBeanDiffersFromNonQualified() {
        Assert.assertNotEquals(_theCampaign._theRealAnnotation, _theCampaign._otherRealAnnotation);
        Assert.assertEquals(_theRealAnnotation, _theCampaign._theRealAnnotation);
        Assert.assertEquals(_theRealRealAnnotation, _theCampaign._otherRealAnnotation);

        Mockito.when(_theRealAnnotation.getGreatestTradeDeal()).thenReturn("GameStop");
        Mockito.when(_theRealRealAnnotation.getGreatestTradeDeal()).thenReturn("Nvidia");

        Assert.assertNotEquals(_theRealAnnotation.getGreatestTradeDeal(),
                _theRealRealAnnotation.getGreatestTradeDeal());
    }

    @Configuration
    @TheAnnotation
    @TheAnnotation(beanName = "Steve")
    public static class CampaignConfiguration {

        @Bean
        public TheCampaign myCampaign(TheRealAnnotation realAnnotation,
                @Named("Steve") TheRealAnnotation otherAnnon) {
            return new TheCampaign(realAnnotation, otherAnnon);
        }

    }

    public static class TheCampaign {
        final TheRealAnnotation _theRealAnnotation;
        final TheRealAnnotation _otherRealAnnotation;

        public TheCampaign(TheRealAnnotation theRealAnnotation, TheRealAnnotation otherRealAnnotation) {
            _theRealAnnotation = theRealAnnotation;
            _otherRealAnnotation = otherRealAnnotation;
        }
    }

    public static class TheRealAnnotation {

        public String getGreatestTradeDeal() {
            return null;
        }

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Repeatable(TheAnnotations.class)
    @Import(TheAnnotationSetup.class)
    public @interface TheAnnotation {

        String beanName() default "";

        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.TYPE)
        @Import(TheAnnotationSetup.class)
        public @interface TheAnnotations {
            TheAnnotation[] value();
        }
    }

    public static class TheAnnotationSetup implements ImportBeanDefinitionRegistrar {

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                BeanDefinitionRegistry registry) {
            AnnotationAttributes annotationAttributes = (AnnotationAttributes)
                    importingClassMetadata.getAnnotationAttributes(TheAnnotations.class.getName());

            AnnotationAttributes[] annonAttriArray = (AnnotationAttributes[]) annotationAttributes.get("value");

            for (AnnotationAttributes attri : annonAttriArray) {
                GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
                beanDefinition.setBeanClass(TheRealAnnotation_Factory.class);

                String beanName = (String) attri.get("beanName");
                // ?: Is a beanName set? // -> Yes, then use this // -> No, use random UUID.
                beanName = !"".equalsIgnoreCase(beanName) ? beanName : UUID.randomUUID().toString();
                registry.registerBeanDefinition(beanName, beanDefinition);
            }
        }
    }

    public static class TheRealAnnotation_Factory extends AbstractFactoryBean<TheRealAnnotation> {

        @Override
        public Class<?> getObjectType() {
            return TheRealAnnotation.class;
        }

        @Override
        protected TheRealAnnotation createInstance() throws Exception {
            return new TheRealAnnotation();
        }
    }

}
