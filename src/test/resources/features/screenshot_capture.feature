Feature: Screenshot Capture and Image Library Building
  As a test automation engineer
  I want to capture screenshots with action highlights
  So that I can build a comprehensive image library for automation

  Background:
    Given I have a Java Swing application running

  Scenario: Basic screenshot capture
    When I take a screenshot named "main_window"
    Then the automation should complete successfully
    And a screenshot should be captured

  Scenario: Screenshot with action highlighting
    When I click on "Submit button"
    Then the automation should complete successfully
    And a screenshot should be captured

  Scenario: Multiple screenshots for workflow
    When I perform the following actions:
      | Take a screenshot named "step1_initial"     |
      | Click on "File menu"                        |
      | Take a screenshot named "step2_menu_open"   |
      | Click on "Save option"                      |
      | Take a screenshot named "step3_save_dialog" |
    Then the automation should complete successfully
    And a GIF should be generated

  Scenario: Application-specific screenshot binding
    Given the application "Calculator" is launched
    When I take a screenshot named "calculator_main"
    And I click on "Number 5"
    And I take a screenshot named "calculator_input"
    Then the automation should complete successfully
    And a screenshot should be captured
