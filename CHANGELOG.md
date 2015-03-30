# 0.3.0 (Not released)
* Tests with lazily initialized contexts will no longer use cached eager initialized contexts (and vice versa).
* Remock caches the annotations found on a given RemockContextConfiguration.
* Added hook point for modifying which bean factory to use. This MUST be an instance of RemockBeanFactory.
