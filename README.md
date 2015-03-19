# Remock

[![Build Status](https://travis-ci.org/ksaua/remock.svg?branch=master)](https://travis-ci.org/ksaua/remock)

Remock is a small library which helps integration testing spring applications.
It is heavily inspired by [springockito-annotations](https://bitbucket.org/kubek2k/springockito/wiki/Home) which
unfortunately appears not be maintained anymore.

Remock allows you to easily:

* Replace any spring bean with a Mockito mock.
* Replace any spring bean with a Mockito spy.
* Replace any spring bean with a different implementation.
* Rejecting any spring bean from being created without replacing it.

Note: Remock currently only works with Spring 4.1. Support for Spring 4.0 and 3.2 is planned.

# Getting it

If you're a gradle user, add the following to your build.gradle file

    testCompile 'no.saua.remock:remock:0.1.0'

# Using it

tl;dr: Annotate your Spring test-classes with `@BootstrapWith(RemockBootstrapper.class)`. Then you can annotate the
test-class or field with `@Reject`, `@ReplaceWithImpl`, `@ReplaceWithMock` or `@WrapWithSpy`.

## Mocking out a dependency:

The following code will replace `SomeDependency` with a Mockito mock.

    @RunWith(SpringJUnit4ClassRunner.class)
    @BootstrapWith(RemockBootstrapper.class)
    @ContextConfiguration(classes = SomeService.class)
    public class ReplaceWithMockTest {

        @ReplaceWithMock
        @Inject
        public SomeDependency someDependency;

        @Inject
        public SomeService someService;

        @Test
        public void test() {
            when(someDependency.method()).thenReturn(42);
            assertEquals(21, someService.getHalf());
            assertTrue(isMock(someService));
        }
    }

## Spying on a dependency:

The following code will wrap the original `SomeDependency` instance with a Mockito spy.

    @RunWith(SpringJUnit4ClassRunner.class)
    @BootstrapWith(RemockBootstrapper.class)
    @ContextConfiguration(classes = SomeService.class)
    public class ReplaceWithMockTest {

        @WrapWithSpy
        @Inject
        public SomeDependency someDependency;

        @Inject
        public SomeService someService;

        @Test
        public void test() {
            someService.getHalf()
            verify(someDependency).method();
        }
    }

# Replacing a bean with a non-mockito mock

The following code will replace `ServiceImpl` with `ServiceMock`

    @ContextConfiguration(classes = {ServiceImpl.class})
    public static class ReplaceWithImplAnnotatedOnFieldTest extends CommonTest {

        @Inject
        @ReplaceWithImpl(value = ServiceImpl.class, with = ServiceMock.class)
        public Service service;

        @Test
        public void test() {
            assertEquals(ServiceMock.class, service.getClass());
        }
    }

## Rejecting a dependency:

The following code will reject any bean definitions of the type `SomeDangerousService` from being defined in
Spring's bean factory.

    @RunWith(SpringJUnit4ClassRunner.class)
    @BootstrapWith(RemockBootstrapper.class)
    @Reject(SomeDangerousService.class)
    @ContextConfiguration(classes = SomeService.class)
    public class ReplaceWithMockTest {

        @Test
        public void test() {
            /* SomeDangerousService does not exist */
        }
    }

This is a bit out of the ordinary, but it's quite powerful. This is useful when you inject List or Maps of an interface
or a superclass and want to remove some beans from the bean factory.

Another use-case is for controlling which beans are defined and lifecycled when a @ComponentScan is used.

## Grouping common mocks

Often tests require the same mocks. Remock allows you to easily group mocks, either by specifying the remock-annotations
on a superclass, or by using the @RemockContextConfiguration.

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

        @ReplaceWithMock(SomeDependency.class)
        @Reject(SomeOtherDependency.class)
        public static class MyRemockConfig {
        }
    }

For more detailed examples see the test cases.

## Lazily intializing beans

Annotating your test with `@LazilyInitialized` causes Remock to force all beans to be lazily initialized. For large
applications this can be useful for increasing test performance, allowing you to only instantiate the beans necessary
for the test.

    @RunWith(SpringJUnit4ClassRunner.class)
    @BootstrapWith(RemockBootstrapper.class)
    @ContextConfiguration(classes = {SomeService.class, SomeOtherService.class})
    @LazilyInitialized
    public class RemockContextConfigurationTest {

        @Inject
        private SomeService someService;

        @Test
        public void test() {
            isMock(someServiceWithDependencies.getDependency());
        }
    }



!!NOTE!! You should never depend on this. Problem is due spring's context cache mechanism. It caches the application
context based on the classes found in the @ContextConfiguration. Remock extends this and also handles any
mocks/spies/rejects. It is not, however, able to distinguish between two tests, where you in one test @Inject a bean
which causes a side effect, and in the other test is dependent on that bean not being initialized.

# Difference between Springockito and Remock

The big difference between Springockito and Remock is whether or not the original implementation lives inside springs
bean factory. While Springockito will use @Primary on all mocked/spied beans, thus taking precedence over the originals,
they will still be injected if you @Inject List<...>, or @Inject Map<String, ...>.

Remock takes a different approach. It takes control over Spring's bean factory and downright rejects adding the
bean definitions of beans it knows should be mocked or rejected.
