package no.saua.remock.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationClass {

    @Bean
    public SomeService someService() {
        return new SomeService();
    }
}
