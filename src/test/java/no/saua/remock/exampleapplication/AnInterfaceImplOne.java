package no.saua.remock.exampleapplication;

import org.springframework.stereotype.Component;

@Component
public class AnInterfaceImplOne implements AnInterface {

    @Override
    public String someMethod() {
        return "someMethodOne";
    }
}
