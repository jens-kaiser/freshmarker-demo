package de.schegge.freshmarker.demo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;

@ConfigurationProperties("demo")
public record DemoConfig(String from, ClassPathResource textResource, ClassPathResource htmlResource) {
}
