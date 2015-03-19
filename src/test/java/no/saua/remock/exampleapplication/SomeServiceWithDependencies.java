package no.saua.remock.exampleapplication;

import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class SomeServiceWithDependencies {

    @Inject
    private AnInterface anInterface;

    public AnInterface getDependency() {
        return anInterface;
    }
}
