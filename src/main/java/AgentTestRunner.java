import java.awt.AWTException;

/**
 * Test runner for SwingAutomationAgent
 * Validates basic functionality with sample scenarios
 */
public class AgentTestRunner {
    
    public static void main(String[] args) {
        try {
            SwingAutomationAgent agent = new SwingAutomationAgent();
            
            System.out.println("=== SwingAutomationAgent Test Runner ===");
            System.out.println("Testing basic agent functionality...\n");
            
            System.out.println("Test 1: Basic Action Parsing");
            String testDescription1 = "Click on center of screen. Wait 2 seconds. Take screenshot.";
            System.out.println("Input: " + testDescription1);
            
            try {
                agent.executeActionSequence(testDescription1, "Test Application");
                System.out.println("✓ Test 1 completed successfully\n");
            } catch (Exception e) {
                System.err.println("✗ Test 1 failed: " + e.getMessage() + "\n");
            }
            
            System.out.println("Test 2: Text Input and Key Press");
            String testDescription2 = "Type \"Hello World\". Press Enter key. Wait 1 second.";
            System.out.println("Input: " + testDescription2);
            
            try {
                agent.executeActionSequence(testDescription2, "Test Application");
                System.out.println("✓ Test 2 completed successfully\n");
            } catch (Exception e) {
                System.err.println("✗ Test 2 failed: " + e.getMessage() + "\n");
            }
            
            System.out.println("Test 3: Complex Workflow");
            String testDescription3 = "Click on File menu. Wait 1 second. " +
                                    "Type \"test.txt\" in filename field. " +
                                    "Press Enter key. " +
                                    "Verify that file is opened. " +
                                    "Take screenshot for verification.";
            System.out.println("Input: " + testDescription3);
            
            try {
                agent.executeActionSequence(testDescription3, "Test Application");
                System.out.println("✓ Test 3 completed successfully\n");
            } catch (Exception e) {
                System.err.println("✗ Test 3 failed: " + e.getMessage() + "\n");
            }
            
            System.out.println("=== Test Runner Completed ===");
            System.out.println("Check captured_images/ directory for screenshots");
            System.out.println("Review console output for detailed execution logs");
            
        } catch (AWTException e) {
            System.err.println("Error initializing agent: " + e.getMessage());
            System.err.println("Make sure you have proper display access and permissions");
        }
    }
}
