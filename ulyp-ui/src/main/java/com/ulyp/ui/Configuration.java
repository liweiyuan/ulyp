package com.ulyp.ui;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@org.springframework.context.annotation.Configuration
@ComponentScan(value = "com.ulyp.ui")
public class Configuration {

    @Bean
    public PrimaryViewController viewController() {
        return new PrimaryViewController();
    }
}
