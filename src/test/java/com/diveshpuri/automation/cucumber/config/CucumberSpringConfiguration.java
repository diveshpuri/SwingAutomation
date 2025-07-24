package com.diveshpuri.automation.cucumber.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for Cucumber tests
 * Enables dependency injection in step definitions
 */
@CucumberContextConfiguration
@Configuration
@ComponentScan(basePackages = "com.diveshpuri.automation")
public class CucumberSpringConfiguration {
}
