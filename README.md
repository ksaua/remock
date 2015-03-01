# Remock

Remock is a small library which helps you when integration tests a spring applications.
It is heavily inspired by springockito-annotations which unfortunately appears not be maintained anymore.

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