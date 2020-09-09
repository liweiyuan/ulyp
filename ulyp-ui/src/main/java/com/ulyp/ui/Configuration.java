package com.ulyp.ui;

import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public PrimaryViewController viewController() {
        return new PrimaryViewController();
    }
}
