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

Note: Remock currently only works with Spring 4.1. Support for Spring 4.0 and 3.2 is planned.

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

`@ReplaceWithMock` can also be annotated directly test-class.

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


`@WrapWithSpy` can also be annotated directly on the test-class.

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

`@ReplaceWithImpl` can also be annotated directly on the test-class.

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

`@Reject` can also be annotated directly on the test-class.

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

## Eagerly intializing beans

Remock will by default set all your beans to be lazily initialized. Annotating your test with `@EagerlyInitialized`
disables the lazy init.

    @RunWith(SpringJUnit4ClassRunner.class)
    @BootstrapWith(RemockBootstrapper.class)
    @ContextConfiguration(classes = {SomeService.class, SomeOtherService.class})
    @EagerlyInitialized
    public class RemockContextConfigurationTest {

        @Test
        public void test() {
            /* ... */
        }
    }


# Difference between Springockito and Remock

The big difference between Springockito and Remock is whether or not the original implementation lives inside springs
bean factory. While Springockito will use @Primary on all mocked/spied beans, thus taking precedence over the originals,
they will still be injected if you `@Inject List<InterfaceOrSuperClass>`, or `@Inject Map<String, InterfaceOrSuperClass>`.

Remock takes a different approach. It takes control over Spring's bean factory and downright rejects adding the
bean definitions of beans it knows should be mocked or rejected.
