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

    @RunWith(SpringJUnit4ClassRunner.class)
    @BootstrapWith(RemockBootstrapper.class)
    @ContextConfiguration(classes = ConfigurationClass.class)
    public class ReplaceWithMockTest extends CommonTest {

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


For more detailed examples see the test cases.

# Difference between Springockito and Remock

The big difference between Springockito and Remock is whether or not the original implementation lives inside springs
bean factory. While Springockito will use @Primary on all mocked/spied beans, thus taking precedence over the originals,
they will still be injected if you @Inject List<SomeInterface>.

Remock takes a different approach. It takes control over Spring's bean factory and downright rejects adding the
bean definition of mocked/spied beans.
