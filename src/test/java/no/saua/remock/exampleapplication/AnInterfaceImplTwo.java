package no.saua.remock.exampleapplication;

import org.springframework.stereotype.Component;

@Component
public class AnInterfaceImplTwo implements AnInterface {
    @Override
    public String someMethod() {
        return "someMethodTwo";
    }
}
