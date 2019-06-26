# 0.6.1
* Support for spring profiles

# 0.6.0
* Support for rejecting/mocking beans created by a factory bean.
* Stopped post processing mocks.

# 0.5.1
* Inadvertently transitively depended on Spring 4.2.1 instead of 4.1.1 which is the minimum.

# 0.5.0
* Initial support for Spring MVC
* The Remock configuration is now found using the actually test-class insteadof the class annotated with
  @ContextConfiguration.
* @EagerlyInitialized renamed to @DisableLazyInit which is more semantically correct.

# 0.4.0
* Stopped supporting Java 7 (due to future usage of @Repeatable annotations).

# 0.3.1
* Fixed nullpointer issue with certain factory beans.

# 0.3.0
* Tests with lazily initialized contexts will no longer use cached eager initialized contexts (and vice versa).
* Remock caches the annotations found on a given RemockContextConfiguration.
* Added hook point for modifying which bean factory to use. This MUST be an instance of RemockBeanFactory.
* Support for rejecting every bean instanceof a class unless it is also instanceof another class. Use case: Rejecting
  every instance of an interface except the one you are testing.
* Fixed issue with caching of contexts when using @ReplaceWithImpl.
