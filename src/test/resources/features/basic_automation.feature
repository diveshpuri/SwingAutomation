Feature: Basic Swing Application Automation
  As a test automation engineer
  I want to automate Java Swing applications using natural language descriptions
  So that I can create maintainable and readable automation tests

  Background:
    Given I have a Java Swing application running
    And retry logic should be configured to 3 attempts

  Scenario: Simple click automation
    When I click on "OK button"
    Then the automation should complete successfully
    And a screenshot should be captured
    And the execution history should be stored

  Scenario: Text input automation
    When I type "Hello World" in "text field"
    And I press "Enter" key
    Then the automation should complete successfully
    And the success rate should be at least 80%

  Scenario: Complex workflow automation
    When I perform the following actions:
      | Click on File menu          |
      | Click on Open option        |
      | Type "document.txt" in filename field |
      | Press Enter key             |
      | Wait for 2 seconds          |
      | Take a screenshot named "file_opened" |
    Then the automation should complete successfully
    And a GIF should be generated
    And an automation script should be created

  Scenario: Screenshot capture workflow
    When I take a screenshot named "initial_state"
    And I click on "Settings button"
    And I take a screenshot named "settings_opened"
    Then the automation should complete successfully
    And a screenshot should be captured

  Scenario: Element verification
    When I click on "Login button"
    Then I should see "Dashboard" element
    And the automation should complete successfully
