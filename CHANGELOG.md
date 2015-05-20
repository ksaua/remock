# 0.3.1
Fixed nullpointer issue with certain factory beans.

# 0.3.0
* Tests with lazily initialized contexts will no longer use cached eager initialized contexts (and vice versa).
* Remock caches the annotations found on a given RemockContextConfiguration.
* Added hook point for modifying which bean factory to use. This MUST be an instance of RemockBeanFactory.
* Support for rejecting every bean instanceof a class unless it is also instanceof another class. Use case: Rejecting
  every instance of an interface except the one you are testing.
* Fixed issue with caching of contexts when using @ReplaceWithImpl.
