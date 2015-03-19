package no.saua.remock.exampleapplication;

import org.springframework.stereotype.Service;

@Service
public class ServiceWhichShouldNotBeInstantiaed {
    public ServiceWhichShouldNotBeInstantiaed() {
        throw new RuntimeException("This service should not be instantiated");
    }
}
