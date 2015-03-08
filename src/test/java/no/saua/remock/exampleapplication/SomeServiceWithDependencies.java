package no.saua.remock.exampleapplication;

import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class SomeServiceWithDependencies {

    @Inject
    AnInterface anInterface;
}
