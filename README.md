# Remock

[![Build Status](https://travis-ci.org/ksaua/remock.svg?branch=master)](https://travis-ci.org/ksaua/remock)

Remock is a small library which helps you when integration tests a spring applications.
It is heavily inspired by [springockito-annotations](https://bitbucket.org/kubek2k/springockito/wiki/Home) which
unfortunately appears not be maintained anymore.

Remock allows you to easily:

* Replace any spring bean with a Mockito mock.
* Replace any spring bean with a Mockito spy.
* Replace any spring bean with a different implementation.
* Rejecting any spring bean from being created without replacing it.

# Getting it

If you're a gradle user, add the following to your build.gradle file

    testCompile 'no.saua.remock:remock:0.1.0'

# Using it

tl;dr: Annotate your spring test-classes with `@BootstrapWith(RemockBootstrapper.class)`. Then you can annotate the
test-class or field with `@Reject`, `@ReplaceWithImpl`, `@ReplaceWithMock` or `@WrapWithSpy`.

For detailed examples the test case

# Difference between Springockito and Remock

The big difference between Springockito and Remock is whether or not the original implementation lives inside springs
bean factory. While Springockito will use @Primary on all mocked/spied beans, thus taking precedence over the originals,
they will still be injected if you @Inject List<SomeInterface>.

Remock takes a different approach. It takes control over springs bean factory and downright rejects adding the
bean definition of mocked/spied beans.
