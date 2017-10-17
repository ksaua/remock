# Remock

[![Build Status](https://travis-ci.org/ksaua/remock.svg?branch=master)](https://travis-ci.org/ksaua/remock)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/no.saua.remock/remock/badge.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22no.saua.remock%22)
[![Coverage Status](https://coveralls.io/repos/ksaua/remock/badge.svg)](https://coveralls.io/r/ksaua/remock)

Remock is a small library which helps integration testing spring applications.
It is heavily inspired by [springockito-annotations](https://bitbucket.org/kubek2k/springockito/wiki/Home) which
unfortunately appears not be maintained anymore.

Remock allows you to easily:

* Replace any spring bean with a Mockito mock.
* Replace any spring bean with a Mockito spy.
* Replace any spring bean with a different implementation.
* Reject any spring bean from being created without replacing it.

Note: Remock only works with Spring 4.1.1 (or later) and Java 8 (or later).

# Table of Contents

  * [Remock](#remock)
  * [Getting it](#getting-it)
  * [Using it](#using-it)
    * [Mocking out a dependency:](#mocking-out-a-dependency)
    * [Spying on a dependency:](#spying-on-a-dependency)
    * [Replacing a bean with a non-mockito mock](#replacing-a-bean-with-a-non-mockito-mock)
    * [Rejecting a dependency:](#rejecting-a-dependency)
    * [Grouping common mocks](#grouping-common-mocks)
    * [Disable lazy initialization of beans](#disable-lazy-initialization-of-beans)
    * [Using Remock and Spring MVC](#using-remock-and-spring-mvc)
  * [Difference between Springockito and Remock](#difference-between-springockito-and-remock)

# Getting it

Follow [this link](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22no.saua.remock%22) to maven central. Choose the
latest version, and choose a build system.

# Using it

tl;dr: Annotate your Spring test-classes with `@BootstrapWith(RemockBootstrapper.class)`. Then you can annotate the
test-class or field with `@Reject`, `@ReplaceWithImpl`, `@ReplaceWithMock` or `@WrapWithSpy`.

## Mocking out a dependency:

The following code will replace `SomeDependency` with a Mockito mock. Since `@ReplaceWithMock` is a meta-annotation for
 `@Autowired`, it will also automatically be injected into the test. Usage:

    @RunWith(SpringJUnit4ClassRunner.class)
    @BootstrapWith(RemockBootstrapper.class)
    @ContextConfiguration(classes = SomeService.class)
    public class ReplaceWithMockTest {

        @ReplaceWithMock
        public SomeDependency someDependency;

        @Inject
        public SomeService someService;

        @Test
        public void test() {
            when(someDependency.method()).thenReturn(42);
            assertEquals(21, someService.getHalf());
            assertTrue(isMock(someDependency));
        }
    }

`@ReplaceWithMock` can also be annotated directly test-class where it is repeatable.

## Spying on a dependency:

The following code will wrap the original `SomeDependency` instance with a Mockito spy. Since `@WrapWithSpy` is a
meta-annotation for `@Autowired`, it will also automatically be injected into the test. Usage:

    @RunWith(SpringJUnit4ClassRunner.class)
    @BootstrapWith(RemockBootstrapper.class)
    @ContextConfiguration(classes = SomeService.class)
    public class ReplaceWithMockTest {

        @WrapWithSpy
        public SomeDependency someDependency;

        @Inject
        public SomeService someService;

        @Test
        public void test() {
            someService.getHalf()
            verify(someDependency).method();
        }
    }


`@WrapWithSpy` can also be annotated directly on the test-class where it is repeatable. 

## Replacing a bean with a non-mockito mock

The following code will replace `ServiceImpl` with `ServiceMock`. Since @ReplaceWithImpl is a meta-annotation the
bean will be auto injected. Usage:

    @RunWith(SpringJUnit4ClassRunner.class)
    @BootstrapWith(RemockBootstrapper.class)
    @ContextConfiguration(classes = {ServiceImpl.class})
    public static class ReplaceWithImplAnnotatedOnFieldTest {

        @ReplaceWithImpl(value = ServiceImpl.class, with = ServiceMock.class)
        public Service service;

        @Test
        public void test() {
            assertEquals(ServiceMock.class, service.getClass());
        }
    }

`@ReplaceWithImpl` can also be annotated directly on the test-class where it is repeatable.

## Rejecting a dependency:

The following code will reject any bean definitions of the type `SomeDangerousService` from being defined in
Spring's bean factory.

    @RunWith(SpringJUnit4ClassRunner.class)
    @BootstrapWith(RemockBootstrapper.class)
    @Reject(SomeDangerousService.class)
    @ContextConfiguration(classes = SomeService.class)
    public class RejectTest {

        @Test
        public void test() {
            /* SomeDangerousService does not exist */
        }
    }

This is a bit out of the ordinary, but it's quite powerful. This is particularly useful when you inject List or Maps of
 an interface or a superclass and want to remove some beans from the bean factory.

Another use-case is for controlling which beans are defined and lifecycled when a `@ComponentScan` is used.

`@Reject` can also be used for a field, though I cannot see a reason why you would. `@Reject` is repeatable.

## Grouping common mocks

Often tests require the same mocks. Remock allows you to easily group mocks, either by specifying the remock-annotations
on a superclass, or by using the `@RemockContextConfiguration`.

    @RunWith(SpringJUnit4ClassRunner.class)
    @BootstrapWith(RemockBootstrapper.class)
    @ContextConfiguration(classes = {SomeServiceWithDependencies.class, SomeDependency.class})
    @RemockContextConfiguration(MyRemockConfig.class)
    public class RemockContextConfigurationTest {

        @Inject
        private SomeServiceWithDependencies someServiceWithDependencies;

        @Test
        public void test() {
            isMock(someServiceWithDependencies.getDependency());
        }

        @ReplaceWithMock(SomeServiceWithDependencies.class)
        @Reject(SomeDependency.class)
        public static class MyRemockConfig {
        }
    }

For more detailed examples see the test cases.

## Disable lazy initialization of beans

Remock will by default set all your beans to be lazily initialized. Annotating your test with `@DisableLazyInit`
disables the lazy init.

    @RunWith(SpringJUnit4ClassRunner.class)
    @BootstrapWith(RemockBootstrapper.class)
    @ContextConfiguration(classes = {SomeService.class, SomeOtherService.class})
    @DisableLazyInit
    public class MyTest {

        @Test
        public void test() {
            /* ... */
        }
    }

If you do not want everything to be eagerly initialized, you can specify which beans you want to disable lazy init for.

## Using Remock and Spring MVC

Trying to use Spring's `@WebAppConfiguration` in conjunction with Remock will fail with an error message like:

    java.lang.IllegalStateException: Configuration error: found multiple declarations of @BootstrapWith on test class
    [org.example.MyClass] with values [class org.springframework.test.context.web.WebTestContextBootstrapper, class
    no.saua.remock.RemockBootstrapper]

Instead you'll have to use the equivalent `@RemockWebAppTest` annotation:

    @RunWith(SpringJUnit4ClassRunner.class)
    @BootstrapWith(RemockBootstrapper.class)
    @ContextConfiguration(classes = WebAppTest.MyController.class)
    @RemockWebAppTest
    public class WebAppTest extends CommonTest {

        @Inject
        private WebApplicationContext context;

        @Test
        public void meh() throws Exception {
            MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/abc")).andReturn();
            String contentAsString = mvcResult.getResponse().getContentAsString();
            assertEquals("abcxyz", contentAsString);
        }

        @Controller
        public static class MyController {
            @RequestMapping("/abc")
            @ResponseBody
            public String abc() {
                return "abcxyz";
            }
        }
    }

Note that lazy loading does not appear to work correctly with spring mvc `@Controller`s.


# Difference between Springockito and Remock

The big difference between Springockito and Remock is whether or not the original implementation lives inside springs
bean factory. While Springockito will use @Primary on all mocked/spied beans, thus taking precedence over the originals,
they will still be injected if you `@Inject List<InterfaceOrSuperClass>`, or `@Inject Map<String, InterfaceOrSuperClass>`.

Remock takes a different approach. It takes control over Spring's bean factory and downright rejects adding the
bean definitions of beans it knows should be mocked or rejected.

With regards to bean factories Remock cannot reject them because it cannot know which class the bean factory will create
until after Spring has resolved the bean factory. Remock will instead ensure that Spring never uses the factory-method
by returning a bogus class if the bean factory was about to create a rejected/mocked class.
