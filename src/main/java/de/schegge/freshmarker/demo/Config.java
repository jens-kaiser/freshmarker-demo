package de.schegge.freshmarker.demo;

import org.freshmarker.TemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Bean
    TemplateBuilder getTemplateBuilder() {
        return new org.freshmarker.Configuration().builder();
    }
}
