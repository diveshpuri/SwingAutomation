Feature: JNLP Application Automation
  As a test automation engineer
  I want to automate JNLP-based Java applications
  So that I can test web-deployed Swing applications

  Scenario: Launch and automate JNLP application
    Given I have a JNLP application at URL "https://example.com/app.jnlp"
    When the application "Sample JNLP App" is launched
    And I execute the automation sequence "Click on main button and take screenshot"
    Then the automation should complete successfully
    And a screenshot should be captured
    And the execution history should be stored

  Scenario: JNLP application workflow
    Given I have a JNLP application at URL "https://demo.app.com/calculator.jnlp"
    When the application "Calculator" is launched
    And I perform the following actions:
      | Click on "7" button         |
      | Click on "+" button         |
      | Click on "3" button         |
      | Click on "=" button         |
      | Take a screenshot named "calculation_result" |
    Then the automation should complete successfully
    And I should see "10" element
    And a GIF should be generated

  Scenario: JNLP launch failure handling
    Given I have a JNLP application at URL "https://invalid.url/nonexistent.jnlp"
    When the application "Invalid App" is launched
    Then the automation should fail
