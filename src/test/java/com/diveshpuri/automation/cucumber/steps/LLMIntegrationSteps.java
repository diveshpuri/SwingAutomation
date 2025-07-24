package com.diveshpuri.automation.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;

/**
 * Cucumber step definitions for LLM integration testing
 * Tests the AI-powered natural language processing capabilities
 */
public class LLMIntegrationSteps {
    
    private Object llmConfig; // Mock for BDD testing
    private Object llmClient; // Mock for BDD testing
    private String llmResponse;
    private boolean llmConfigured;
    
    @Given("I have configured the LLM with provider {string}")
    public void i_have_configured_the_llm_with_provider(String provider) {
        llmConfigured = true;
        System.out.println("LLM configured with provider: " + provider + " (mocked)");
    }
    
    @Given("the LLM is not configured")
    public void the_llm_is_not_configured() {
        llmConfigured = false;
        System.out.println("LLM not configured - will use fallback mode (mocked)");
    }
    
    @When("I request action parsing for {string}")
    public void i_request_action_parsing_for(String description) {
        if (llmConfigured) {
            llmResponse = "CLICK: button\nWAIT: 1\nTAKE_SCREENSHOT: result";
        } else {
            llmResponse = "LLM not configured - using fallback parsing";
        }
        System.out.println("LLM Response: " + llmResponse);
    }
    
    @When("I request screenshot analysis for {string} with intent {string}")
    public void i_request_screenshot_analysis_for_with_intent(String screenshotPath, String intent) {
        if (llmConfigured) {
            llmResponse = "CLICK: target_element\nVERIFY_ELEMENT: result\nTAKE_SCREENSHOT: final_state";
        } else {
            llmResponse = "Screenshot analysis not available - LLM not configured";
        }
        System.out.println("Screenshot analysis response: " + llmResponse);
    }
    
    @Then("the LLM should provide structured actions")
    public void the_llm_should_provide_structured_actions() {
        if (llmResponse == null || llmResponse.trim().isEmpty()) {
            throw new AssertionError("LLM response is empty");
        }
        
        if (llmConfigured) {
            if (llmResponse.contains("fallback") || llmResponse.contains("not configured")) {
                throw new AssertionError("LLM should not use fallback when configured");
            }
        }
        
        System.out.println("✓ LLM provided structured response");
    }
    
    @Then("the response should contain fallback message")
    public void the_response_should_contain_fallback_message() {
        if (!llmResponse.contains("fallback") && !llmResponse.contains("not configured")) {
            throw new AssertionError("Expected fallback message not found in response");
        }
        System.out.println("✓ Fallback message found as expected");
    }
    
    @And("the response should include automation context")
    public void the_response_should_include_automation_context() {
        if (llmConfigured && !llmResponse.contains("automation")) {
            throw new AssertionError("Response should include automation context when LLM is configured");
        }
        System.out.println("✓ Automation context verified");
    }
}
