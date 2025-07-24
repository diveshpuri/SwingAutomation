package com.diveshpuri.automation.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;

/**
 * Basic Cucumber step definitions for testing BDD integration
 * These steps don't depend on the main framework classes
 */
public class BasicSteps {
    
    private boolean testResult = false;
    private String lastAction = "";
    
    @Given("I have a test environment")
    public void i_have_a_test_environment() {
        System.out.println("Setting up test environment");
        testResult = true;
    }
    
    @When("I perform a test action {string}")
    public void i_perform_a_test_action(String action) {
        System.out.println("Performing action: " + action);
        lastAction = action;
        testResult = true;
    }
    
    @Then("the test should pass")
    public void the_test_should_pass() {
        if (!testResult) {
            throw new AssertionError("Test failed");
        }
        System.out.println("✓ Test passed successfully");
    }
    
    @And("the action {string} should be recorded")
    public void the_action_should_be_recorded(String expectedAction) {
        if (!lastAction.equals(expectedAction)) {
            throw new AssertionError("Expected action '" + expectedAction + "' but got '" + lastAction + "'");
        }
        System.out.println("✓ Action recorded correctly: " + expectedAction);
    }
}
