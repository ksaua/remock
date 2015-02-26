package no.saua.remock.exampletests.application;

import org.springframework.stereotype.Component;

@Component
public class AnInterfaceImplOne implements AnInterface {

    @Override
    public String someMethod() {
        return "someMethodOne";
    }
}
