Feature: Automation Script Generation
  As a test automation engineer
  I want to generate reusable automation scripts from LLM responses
  So that I can reduce API costs and create deterministic automation

  Background:
    Given I have a Java Swing application running
    And I have configured the LLM with provider "OPENAI"

  Scenario: Generate script from simple workflow
    When I execute the automation sequence "Click login button, enter credentials, and submit"
    Then the automation should complete successfully
    And an automation script should be created
    And the execution history should be stored

  Scenario: Generate script from complex workflow
    When I perform the following actions:
      | Click on "File menu"                    |
      | Click on "New document"                 |
      | Type "Sample Document" in "title field" |
      | Click on "Format menu"                  |
      | Click on "Bold option"                  |
      | Type "Bold text content" in "content area" |
      | Press "Ctrl+S" key                      |
      | Type "document.txt" in "filename field" |
      | Click on "Save button"                  |
    Then the automation should complete successfully
    And an automation script should be created
    And a GIF should be generated

  Scenario: Script generation with metadata
    When I execute the automation sequence "Perform user registration workflow"
    Then the automation should complete successfully
    And an automation script should be created
    And the execution history should be stored
    And the success rate should be at least 90%

  Scenario: Reusable script validation
    When I execute the automation sequence "Standard login procedure"
    Then the automation should complete successfully
    And an automation script should be created
    And the script should contain proper error handling
