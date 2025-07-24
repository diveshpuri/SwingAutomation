import java.awt.AWTException;

/**
 * Enhanced test runner for SwingAutomationAgent
 * Tests all new features including JNLP launch, retry logic, GIF creation
 */
public class AgentTestRunner {
    
    public static void main(String[] args) {
        try {
            System.out.println("=== Enhanced SwingAutomationAgent Test Runner ===");
            System.out.println("Testing all new features...\n");
            
            SwingAutomationAgent agent = new SwingAutomationAgent();
            
            System.out.println("Test 1: Basic Action Execution with Retry Logic");
            agent.setMaxRetries(3);
            String testDescription1 = "Click on center of screen. Wait 2 seconds. Take screenshot.";
            System.out.println("Input: " + testDescription1);
            agent.executeActionSequence(testDescription1, "Test Application");
            System.out.println("✓ Test 1 completed\n");
            
            System.out.println("Test 2: Complex Workflow with Script Generation");
            String testDescription2 = "Click on File menu. " +
                                     "Click on Open option. " +
                                     "Type \"test.txt\" in filename field. " +
                                     "Press Enter key. " +
                                     "Verify that file is opened. " +
                                     "Take screenshot for verification.";
            System.out.println("Input: " + testDescription2);
            agent.executeActionSequence(testDescription2, "Text Editor");
            System.out.println("✓ Test 2 completed\n");
            
            if (args.length >= 2 && args[0].equals("jnlp")) {
                System.out.println("Test 3: JNLP Application Launch");
                String jnlpUrl = args[1];
                String appName = args.length > 2 ? args[2] : "JNLP Application";
                String description = args.length > 3 ? args[3] : "Take screenshot of application";
                
                System.out.println("JNLP URL: " + jnlpUrl);
                System.out.println("App Name: " + appName);
                System.out.println("Description: " + description);
                
                agent.executeWithJNLPLaunch(jnlpUrl, description, appName);
                System.out.println("✓ Test 3 completed\n");
            }
            
            System.out.println("Test 4: LLM Response Storage Analysis");
            System.out.println("Execution Statistics:");
            System.out.println(agent.getResponseStorage().generateExecutionSummary());
            System.out.println("✓ Test 4 completed\n");
            
            System.out.println("=== All Tests Completed ===");
            System.out.println("Check the following directories for generated artifacts:");
            System.out.println("• screenshots/ - Action screenshots with highlights");
            System.out.println("• generated_scripts/ - Reusable automation scripts");
            System.out.println("• execution_proof_*.gif - Animated proof of execution");
            System.out.println("• execution_history_*.json - Detailed execution logs");
            
        } catch (AWTException e) {
            System.err.println("Error initializing SwingAutomationAgent: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Test execution error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
