Feature: LLM Integration for Natural Language Processing
  As a test automation engineer
  I want to use AI-powered natural language processing
  So that I can write automation tests in plain English

  Scenario: LLM-powered action parsing with OpenAI
    Given I have configured the LLM with provider "OPENAI"
    When I request action parsing for "Click the login button and enter username admin"
    Then the LLM should provide structured actions
    And the response should include automation context

  Scenario: LLM-powered action parsing with Azure OpenAI
    Given I have configured the LLM with provider "AZURE_OPENAI"
    When I request action parsing for "Open the file menu, select new document, and save as test.txt"
    Then the LLM should provide structured actions
    And the response should include automation context

  Scenario: Screenshot analysis with LLM
    Given I have configured the LLM with provider "OPENAI"
    When I request screenshot analysis for "screenshots/login_screen.png" with intent "Login to the application"
    Then the LLM should provide structured actions
    And the response should include automation context

  Scenario: Fallback behavior when LLM not configured
    Given the LLM is not configured
    When I request action parsing for "Click the submit button"
    Then the response should contain fallback message

  Scenario: Multiple LLM provider support
    Given I have configured the LLM with provider "ANTHROPIC"
    When I request action parsing for "Navigate to settings and change theme to dark mode"
    Then the LLM should provide structured actions

  Scenario: Custom LLM endpoint integration
    Given I have configured the LLM with provider "CUSTOM"
    When I request action parsing for "Perform data validation and generate report"
    Then the LLM should provide structured actions
