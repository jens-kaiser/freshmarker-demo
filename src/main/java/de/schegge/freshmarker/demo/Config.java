package de.schegge.freshmarker.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Bean
    org.freshmarker.Configuration getConfiguration() {
        return new org.freshmarker.Configuration();
    }

    @Bean
    org.freshmarker.Configuration.TemplateBuilder getTemplateBuilder(org.freshmarker.Configuration configuration) {
        return configuration.builder();
    }
}
