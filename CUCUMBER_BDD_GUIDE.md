# Cucumber BDD Integration Guide

This guide explains how to use the Cucumber BDD (Behavior-Driven Development) integration in the Java Swing Automation Framework.

## Overview

The framework now includes comprehensive Cucumber integration that allows you to write automation tests using natural language Gherkin syntax. This makes tests more readable, maintainable, and accessible to non-technical stakeholders.

## Features

- **Natural Language Testing**: Write tests in plain English using Gherkin syntax
- **Comprehensive Step Definitions**: Pre-built steps for common automation scenarios
- **LLM Integration Testing**: Test AI-powered natural language processing capabilities
- **JNLP Application Support**: BDD tests for Java Web Start applications
- **Screenshot and GIF Generation**: Visual validation and proof of execution
- **Script Generation Testing**: Validate automation script creation
- **Spring Integration**: Dependency injection support for complex test scenarios

## Directory Structure

```
src/test/
├── java/com/diveshpuri/automation/cucumber/
│   ├── runners/
│   │   └── CucumberTestRunner.java
│   ├── steps/
│   │   ├── SwingAutomationSteps.java
│   │   ├── LLMIntegrationSteps.java
│   │   └── ScreenshotCaptureSteps.java
│   └── config/
│       └── CucumberSpringConfiguration.java
└── resources/
    ├── features/
    │   ├── basic_automation.feature
    │   ├── jnlp_automation.feature
    │   ├── llm_integration.feature
    │   ├── screenshot_capture.feature
    │   └── script_generation.feature
    └── cucumber.properties
```

## Running Cucumber Tests

### Maven Commands

```bash
# Run all Cucumber tests
mvn test

# Run specific feature file
mvn test -Dcucumber.features=src/test/resources/features/basic_automation.feature

# Run tests with specific tags
mvn test -Dcucumber.filter.tags="@smoke"

# Generate detailed reports
mvn test -Dcucumber.plugin="pretty,html:target/cucumber-reports,json:target/cucumber-reports/Cucumber.json"
```

### Cucumber Maven Plugin

```bash
# Run using Cucumber Maven plugin
mvn cucumber:run

# Run with specific configuration
mvn cucumber:run -Dcucumber.features=src/test/resources/features
```

## Feature File Examples

### Basic Automation

```gherkin
Feature: Basic Swing Application Automation
  As a test automation engineer
  I want to automate Java Swing applications using natural language descriptions
  So that I can create maintainable and readable automation tests

  Scenario: Simple click automation
    Given I have a Java Swing application running
    When I click on "OK button"
    Then the automation should complete successfully
    And a screenshot should be captured
```

### JNLP Application Testing

```gherkin
Feature: JNLP Application Automation
  Scenario: Launch and automate JNLP application
    Given I have a JNLP application at URL "https://example.com/app.jnlp"
    When the application "Sample JNLP App" is launched
    And I execute the automation sequence "Click on main button and take screenshot"
    Then the automation should complete successfully
```

### LLM Integration Testing

```gherkin
Feature: LLM Integration for Natural Language Processing
  Scenario: LLM-powered action parsing
    Given I have configured the LLM with provider "OPENAI"
    When I request action parsing for "Click the login button and enter username admin"
    Then the LLM should provide structured actions
```

## Available Step Definitions

### Application Setup Steps

- `Given I have a Java Swing application running`
- `Given I have a JNLP application at URL "{string}"`
- `Given the application "{string}" is launched`

### Action Steps

- `When I click on "{string}"`
- `When I type "{string}" in "{string}"`
- `When I press "{string}" key`
- `When I wait for {int} seconds`
- `When I take a screenshot named "{string}"`
- `When I execute the automation sequence "{string}"`
- `When I perform the following actions:`

### Verification Steps

- `Then the automation should complete successfully`
- `Then the automation should fail`
- `Then I should see "{string}" element`
- `Then a screenshot should be captured`
- `Then a GIF should be generated`
- `Then an automation script should be created`

### LLM Integration Steps

- `Given I have configured the LLM with provider "{string}"`
- `Given the LLM is not configured`
- `When I request action parsing for "{string}"`
- `When I request screenshot analysis for "{string}" with intent "{string}"`
- `Then the LLM should provide structured actions`
- `Then the response should contain fallback message`

### Configuration Steps

- `And retry logic should be configured to {int} attempts`
- `And the success rate should be at least {int}%`
- `And the execution history should be stored`

## Writing Custom Step Definitions

To add custom step definitions, create a new class in the `steps` package:

```java
package com.diveshpuri.automation.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

public class CustomSteps {
    
    @Given("I have a custom application setup")
    public void i_have_a_custom_application_setup() {
        // Implementation
    }
    
    @When("I perform custom action {string}")
    public void i_perform_custom_action(String action) {
        // Implementation
    }
    
    @Then("the custom result should be {string}")
    public void the_custom_result_should_be(String expectedResult) {
        // Implementation
    }
}
```

## Configuration

### Cucumber Properties

The `cucumber.properties` file contains global configuration:

```properties
cucumber.plugin=pretty,html:target/cucumber-reports,json:target/cucumber-reports/Cucumber.json
cucumber.glue=com.diveshpuri.automation.cucumber.steps
cucumber.features=src/test/resources/features
cucumber.execution.parallel.enabled=false
```

### Spring Integration

The framework uses Spring for dependency injection in step definitions. Configure beans in `CucumberSpringConfiguration.java`.

## Reports

Cucumber generates multiple report formats:

- **HTML Report**: `target/cucumber-reports/index.html`
- **JSON Report**: `target/cucumber-reports/Cucumber.json`
- **JUnit XML**: `target/cucumber-reports/Cucumber.xml`

## Best Practices

### Feature File Organization

1. **One Feature per File**: Keep related scenarios together
2. **Descriptive Names**: Use clear, business-focused feature names
3. **Background Steps**: Use `Background:` for common setup steps
4. **Tags**: Use `@tag` annotations for test organization

### Step Definition Guidelines

1. **Reusable Steps**: Write generic steps that can be reused
2. **Clear Parameters**: Use descriptive parameter names
3. **Error Handling**: Include proper exception handling
4. **State Management**: Use instance variables to maintain test state

### Scenario Design

1. **Single Responsibility**: Each scenario should test one specific behavior
2. **Independent Tests**: Scenarios should not depend on each other
3. **Clear Assertions**: Use specific verification steps
4. **Data Tables**: Use tables for multiple test data

## Integration with CI/CD

### Jenkins Pipeline

```groovy
pipeline {
    agent any
    stages {
        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/cucumber-reports',
                        reportFiles: 'index.html',
                        reportName: 'Cucumber Report'
                    ])
                }
            }
        }
    }
}
```

### GitHub Actions

```yaml
name: Cucumber Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Run Cucumber tests
        run: mvn test
      - name: Upload test reports
        uses: actions/upload-artifact@v2
        with:
          name: cucumber-reports
          path: target/cucumber-reports/
```

## Troubleshooting

### Common Issues

1. **Step Definition Not Found**: Ensure glue package is correct in `cucumber.properties`
2. **Spring Context Issues**: Check `CucumberSpringConfiguration.java` setup
3. **Feature File Parsing**: Validate Gherkin syntax
4. **Report Generation**: Verify plugin configuration in Maven

### Debug Mode

Run tests with debug output:

```bash
mvn test -Dcucumber.plugin="pretty" -Dcucumber.execution.dry-run=true
```

## Examples and Templates

See the `src/test/resources/features/` directory for comprehensive examples of:

- Basic automation scenarios
- JNLP application testing
- LLM integration testing
- Screenshot capture workflows
- Script generation validation

These examples serve as templates for creating your own BDD test scenarios.
