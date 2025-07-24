package com.diveshpuri.automation.cucumber.runners;

import org.junit.jupiter.api.Test;
import io.cucumber.core.cli.Main;

/**
 * Simple Cucumber Test Runner for BDD automation tests
 * Alternative runner that doesn't require complex JUnit Platform Suite setup
 */
public class SimpleCucumberRunner {
    
    @Test
    public void runCucumberTests() {
        String[] args = {
            "--glue", "com.diveshpuri.automation.cucumber.steps",
            "--plugin", "pretty",
            "src/test/resources/features"
        };
        
        try {
            Main.main(args);
        } catch (Exception e) {
            System.err.println("Cucumber execution failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
