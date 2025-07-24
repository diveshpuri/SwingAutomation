Feature: Basic Cucumber Integration Test
  As a developer
  I want to verify that Cucumber BDD integration works
  So that I can write behavior-driven tests

  Scenario: Simple test scenario
    Given I have a test environment
    When I perform a test action "click button"
    Then the test should pass
    And the action "click button" should be recorded

  Scenario: Another test scenario
    Given I have a test environment
    When I perform a test action "type text"
    Then the test should pass
    And the action "type text" should be recorded
