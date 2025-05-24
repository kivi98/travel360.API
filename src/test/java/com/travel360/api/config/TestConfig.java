package com.travel360.api.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;

@TestConfiguration
@ComponentScan(basePackages = "com.travel360.api")
public class TestConfig {
    // This configuration helps ensure all components are scanned during testing
} 