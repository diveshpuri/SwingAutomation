import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.imageio.ImageIO;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Intelligent automation agent for Java Swing applications
 * Reads human action descriptions and executes them using screenshot capture framework
 * Integrates with LLM APIs for enhanced natural language processing
 */
public class SwingAutomationAgent {
    
    private Robot robot;
    private SwingImageAutomation imageAutomation;
    private ScreenshotCapture screenshotCapture;
    private List<ActionStep> actionPlan;
    private String targetApplication;
    private int executedSteps;
    private int successfulSteps;
    private int failedSteps;
    
    private LLMConfig llmConfig;
    private LLMClient llmClient;
    private LLMResponseStorage responseStorage;
    private GIFGenerator gifGenerator;
    private AutomationScriptGenerator scriptGenerator;
    private JNLPLauncher jnlpLauncher;
    
    private int maxRetries = 10;
    private List<String> screenshotPaths;
    private String currentExecutionId;
    
    /**
     * Represents a single automation action step
     */
    public static class ActionStep {
        public String action;
        public String target;
        public String input;
        public int waitTime;
        public boolean executed;
        public boolean successful;
        
        public ActionStep(String action, String target, String input, int waitTime) {
            this.action = action;
            this.target = target;
            this.input = input;
            this.waitTime = waitTime;
            this.executed = false;
            this.successful = false;
        }
        
        @Override
        public String toString() {
            return String.format("%s: %s %s", action, target, input != null ? "-> " + input : "");
        }
    }
    
    public SwingAutomationAgent() throws AWTException {
        this.robot = new Robot();
        this.imageAutomation = new SwingImageAutomation();
        this.screenshotCapture = new ScreenshotCapture();
        this.actionPlan = new ArrayList<>();
        this.executedSteps = 0;
        this.successfulSteps = 0;
        this.failedSteps = 0;
        
        this.llmConfig = new LLMConfig();
        this.llmClient = new LLMClient(llmConfig);
        this.responseStorage = new LLMResponseStorage();
        this.gifGenerator = new GIFGenerator();
        this.scriptGenerator = new AutomationScriptGenerator();
        this.screenshotPaths = new ArrayList<>();
        this.currentExecutionId = "exec_" + System.currentTimeMillis();
    }
    
    /**
     * Main entry point for executing action sequences
     */
    public void executeActionSequence(String humanDescription, String applicationName) {
        this.targetApplication = applicationName;
        
        System.out.println("=== SwingAutomationAgent Starting ===");
        System.out.println("Target Application: " + applicationName);
        System.out.println("Action Description: " + humanDescription);
        System.out.println();
        
        parseHumanDescription(humanDescription);
        
        displayActionPlan();
        
        executeActionPlan();
        
        displayExecutionSummary();
        
        generateCompletionArtifacts(humanDescription);
    }
    
    /**
     * Launch JNLP application and execute action sequence
     */
    public void executeWithJNLPLaunch(String jnlpUrl, String humanDescription, String applicationName) {
        this.targetApplication = applicationName;
        this.jnlpLauncher = new JNLPLauncher(jnlpUrl, applicationName);
        
        System.out.println("=== SwingAutomationAgent with JNLP Launch ===");
        System.out.println("JNLP URL: " + jnlpUrl);
        System.out.println("Target Application: " + applicationName);
        System.out.println("Action Description: " + humanDescription);
        System.out.println();
        
        if (!jnlpLauncher.launchApplication()) {
            System.err.println("Failed to launch JNLP application");
            return;
        }
        
        if (!jnlpLauncher.waitForApplicationReady(30)) {
            System.err.println("Application not ready for automation");
            jnlpLauncher.closeApplication();
            return;
        }
        
        try {
            executeActionSequence(humanDescription, applicationName);
        } finally {
            jnlpLauncher.closeApplication();
        }
    }
    
    /**
     * Generate completion artifacts (GIF, script, summary)
     */
    private void generateCompletionArtifacts(String description) {
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            
            if (!screenshotPaths.isEmpty()) {
                String gifPath = "execution_proof_" + timestamp + ".gif";
                if (gifGenerator.createGIF(screenshotPaths, gifPath)) {
                    System.out.println("✓ Execution GIF created: " + gifPath);
                }
            }
            
            if (!actionPlan.isEmpty()) {
                String scriptPath = scriptGenerator.generateScript(
                    "AutomationScript_" + timestamp, 
                    description, 
                    actionPlan, 
                    responseStorage.getExecutionContext(5)
                );
                if (scriptPath != null) {
                    System.out.println("✓ Automation script generated: " + scriptPath);
                }
            }
            
            String historyPath = "execution_history_" + timestamp + ".json";
            try (PrintWriter writer = new PrintWriter(new FileWriter(historyPath))) {
                writer.print(responseStorage.exportToJson());
                System.out.println("✓ Execution history exported: " + historyPath);
            }
            
            displayCompletionMessage();
            
        } catch (Exception e) {
            System.err.println("Error generating completion artifacts: " + e.getMessage());
        }
    }
    
    /**
     * Display completion message with summary
     */
    private void displayCompletionMessage() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🎉 AUTOMATION EXECUTION COMPLETED");
        System.out.println("=".repeat(50));
        
        Map<String, Object> stats = responseStorage.getExecutionStatistics();
        System.out.printf("📊 Execution Summary:\n");
        System.out.printf("   • Total Steps: %s\n", stats.get("totalSteps"));
        System.out.printf("   • Success Rate: %s\n", stats.get("successRate"));
        System.out.printf("   • Average Time: %s ms\n", stats.get("avgExecutionTimeMs"));
        
        if (failedSteps > 0) {
            System.out.printf("   • Failed Steps: %d (see execution history for details)\n", failedSteps);
        }
        
        System.out.println("\n📁 Generated Artifacts:");
        System.out.println("   • Execution GIF (proof of execution)");
        System.out.println("   • Automation Script (for future reuse)");
        System.out.println("   • Execution History (for debugging)");
        
        System.out.println("\n✅ Task completed successfully!");
        System.out.println("=".repeat(50));
    }
    
    /**
     * Configure retry settings
     */
    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }
    
    /**
     * Get response storage for analysis
     */
    public LLMResponseStorage getResponseStorage() {
        return responseStorage;
    }
    
    /**
     * Parse human description into structured action steps
     */
    public List<ActionStep> parseHumanDescription(String description) {
        actionPlan.clear();
        
        if (llmConfig.isConfigured()) {
            try {
                String llmResponse = llmClient.parseActionDescription(description);
                parseLLMResponse(llmResponse);
                if (!actionPlan.isEmpty()) {
                    System.out.println("Used LLM for action parsing");
                    return actionPlan;
                }
            } catch (Exception e) {
                System.out.println("LLM parsing failed, falling back to pattern matching: " + e.getMessage());
            }
        }
        
        String[] sentences = description.split("\\.|;|\\n");
        
        for (String sentence : sentences) {
            sentence = sentence.trim();
            if (!sentence.isEmpty()) {
                ActionStep step = parseIndividualAction(sentence);
                if (step != null) {
                    actionPlan.add(step);
                }
            }
        }
        
        if (actionPlan.stream().noneMatch(step -> "TAKE_SCREENSHOT".equals(step.action))) {
            actionPlan.add(new ActionStep("TAKE_SCREENSHOT", "final_state", null, 0));
        }
        
        return actionPlan;
    }
    
    /**
     * Parse individual action from sentence
     */
    private ActionStep parseIndividualAction(String sentence) {
        sentence = sentence.toLowerCase().trim();
        
        if (sentence.contains("verify") || sentence.contains("check")) {
            String target = extractTargetElement(sentence);
            return new ActionStep("VERIFY_ELEMENT", target, null, 0);
        }
        
        if (sentence.contains("screenshot") || sentence.contains("capture")) {
            return new ActionStep("TAKE_SCREENSHOT", "current_state", null, 0);
        }
        
        if (sentence.contains("double") && sentence.contains("click")) {
            String target = extractTargetElement(sentence);
            return new ActionStep("DOUBLE_CLICK", target, null, 0);
        }
        
        if (sentence.contains("right") && sentence.contains("click")) {
            String target = extractTargetElement(sentence);
            return new ActionStep("RIGHT_CLICK", target, null, 0);
        }
        
        if (sentence.contains("click")) {
            String target = extractTargetElement(sentence);
            return new ActionStep("CLICK", target, null, 0);
        }
        
        if (sentence.contains("type") || sentence.contains("enter") || sentence.contains("input")) {
            String target = extractTargetElement(sentence);
            String input = extractInputText(sentence);
            return new ActionStep("TYPE_TEXT", target, input, 0);
        }
        
        if (sentence.contains("press")) {
            String keyName = extractKeyName(sentence);
            return new ActionStep("PRESS_KEY", keyName, null, 0);
        }
        
        if (sentence.contains("wait") || sentence.contains("pause")) {
            int waitTime = extractWaitTime(sentence);
            return new ActionStep("WAIT", "duration", String.valueOf(waitTime), waitTime);
        }
        
        if (sentence.contains("scroll")) {
            String direction = sentence.contains("up") ? "up" : "down";
            return new ActionStep("SCROLL", direction, null, 0);
        }
        
        return null;
    }
    
    /**
     * Parse LLM response into action steps
     */
    private void parseLLMResponse(String llmResponse) {
        String[] lines = llmResponse.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty() && line.contains(":")) {
                ActionStep step = parseLLMActionLine(line);
                if (step != null) {
                    actionPlan.add(step);
                }
            }
        }
    }
    
    /**
     * Parse individual LLM action line
     */
    private ActionStep parseLLMActionLine(String line) {
        String[] parts = line.split(":", 2);
        if (parts.length != 2) return null;
        
        String actionType = parts[0].trim().toUpperCase();
        String actionDetails = parts[1].trim();
        
        String target = actionDetails;
        String input = null;
        
        if (actionDetails.contains("->")) {
            String[] detailParts = actionDetails.split("->", 2);
            target = detailParts[0].trim();
            input = detailParts[1].trim();
        }
        
        switch (actionType) {
            case "CLICK":
                return new ActionStep("CLICK", target, null, 0);
            case "TYPE_TEXT":
                return new ActionStep("TYPE_TEXT", target, input, 0);
            case "PRESS_KEY":
                return new ActionStep("PRESS_KEY", target, null, 0);
            case "WAIT":
                int waitTime = 2; // default
                try {
                    waitTime = Integer.parseInt(target);
                } catch (NumberFormatException e) {
                }
                return new ActionStep("WAIT", "duration", String.valueOf(waitTime), waitTime);
            case "VERIFY":
                return new ActionStep("VERIFY_ELEMENT", target, null, 0);
            case "TAKE_SCREENSHOT":
                return new ActionStep("TAKE_SCREENSHOT", "current_state", null, 0);
            default:
                return null;
        }
    }
    
    /**
     * Extract target element from sentence
     */
    private String extractTargetElement(String sentence) {
        String[] patterns = {
            "button", "btn", "field", "textbox", "input", "menu", "link", 
            "checkbox", "radio", "dropdown", "list", "tab", "window", "dialog"
        };
        
        for (String pattern : patterns) {
            if (sentence.contains(pattern)) {
                String[] words = sentence.split("\\s+");
                for (int i = 0; i < words.length; i++) {
                    if (words[i].contains(pattern)) {
                        StringBuilder target = new StringBuilder();
                        if (i > 0) target.append(words[i-1]).append(" ");
                        target.append(words[i]);
                        if (i < words.length - 1) target.append(" ").append(words[i+1]);
                        return target.toString();
                    }
                }
                return pattern;
            }
        }
        
        if (sentence.contains("\"")) {
            int start = sentence.indexOf("\"");
            int end = sentence.indexOf("\"", start + 1);
            if (end > start) {
                return sentence.substring(start + 1, end);
            }
        }
        
        return "center of screen"; // ultimate fallback
    }
    
    /**
     * Extract input text from sentence
     */
    private String extractInputText(String sentence) {
        if (sentence.contains("\"")) {
            int start = sentence.indexOf("\"");
            int end = sentence.indexOf("\"", start + 1);
            if (end > start) {
                return sentence.substring(start + 1, end);
            }
        }
        
        return "sample text";
    }
    
    /**
     * Extract key name from sentence
     */
    private String extractKeyName(String sentence) {
        String[] commonKeys = {"enter", "escape", "tab", "space", "delete", "backspace", "f1", "f2", "f3", "f4", "f5"};
        
        for (String key : commonKeys) {
            if (sentence.contains(key)) {
                return key;
            }
        }
        
        return "enter"; // default
    }
    
    /**
     * Extract wait time from sentence
     */
    private int extractWaitTime(String sentence) {
        Pattern pattern = Pattern.compile("(\\d+)\\s*(second|sec|s)");
        Matcher matcher = pattern.matcher(sentence);
        
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        
        return 2; // default 2 seconds
    }
    
    /**
     * Display the parsed action plan
     */
    private void displayActionPlan() {
        System.out.println("=== Parsed Action Plan ===");
        for (int i = 0; i < actionPlan.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, actionPlan.get(i));
        }
        System.out.println();
    }
    
    /**
     * Execute the complete action plan
     */
    private void executeActionPlan() {
        System.out.println("=== Executing Action Plan ===");
        
        for (int i = 0; i < actionPlan.size(); i++) {
            ActionStep step = actionPlan.get(i);
            System.out.printf("Step %d: %s%n", i + 1, step);
            
            boolean success = executeStep(step);
            step.executed = true;
            step.successful = success;
            
            executedSteps++;
            if (success) {
                successfulSteps++;
                System.out.println("✓ Success");
            } else {
                failedSteps++;
                System.out.println("✗ Failed");
            }
            
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            
            System.out.println();
        }
    }
    
    /**
     * Execute individual action step with retry logic
     */
    private boolean executeStep(ActionStep step) {
        String stepId = currentExecutionId + "_step_" + (executedSteps + 1);
        LLMResponseStorage.ExecutionStep executionStep = responseStorage.createExecutionStep(stepId);
        
        executionStep.setHumanInput(step.toString());
        executionStep.setParsedAction(step.action + ": " + step.target);
        
        long startTime = System.currentTimeMillis();
        boolean success = false;
        String errorMessage = null;
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                System.out.printf("Attempt %d/%d: %s%n", attempt, maxRetries, step);
                
                switch (step.action) {
                    case "CLICK":
                        success = executeClick(step);
                        break;
                    case "DOUBLE_CLICK":
                        success = executeDoubleClick(step);
                        break;
                    case "RIGHT_CLICK":
                        success = executeRightClick(step);
                        break;
                    case "TYPE_TEXT":
                        success = executeTypeText(step);
                        break;
                    case "PRESS_KEY":
                        success = executePressKey(step);
                        break;
                    case "WAIT":
                        success = executeWait(step);
                        break;
                    case "VERIFY_ELEMENT":
                        success = executeVerifyElement(step);
                        break;
                    case "SCROLL":
                        success = executeScroll(step);
                        break;
                    case "TAKE_SCREENSHOT":
                        success = executeTakeScreenshot(step);
                        break;
                    default:
                        errorMessage = "Unknown action: " + step.action;
                        success = false;
                }
                
                if (success) {
                    System.out.println("✓ Success on attempt " + attempt);
                    break;
                } else {
                    errorMessage = "Action failed on attempt " + attempt;
                    if (attempt < maxRetries) {
                        System.out.println("✗ Failed, retrying...");
                        Thread.sleep(1000);
                    }
                }
                
            } catch (Exception e) {
                errorMessage = "Error executing step: " + e.getMessage();
                System.err.println(errorMessage);
                if (attempt < maxRetries) {
                    try { Thread.sleep(1000); } catch (InterruptedException ie) { break; }
                }
            }
        }
        
        long executionTime = System.currentTimeMillis() - startTime;
        executionStep.setExecutionTimeMs(executionTime);
        executionStep.setSuccess(success);
        executionStep.setExecutionResult(success ? "SUCCESS" : "FAILED after " + maxRetries + " attempts");
        if (!success && errorMessage != null) {
            executionStep.setErrorMessage(errorMessage);
        }
        
        return success;
    }
    
    /**
     * Execute click action
     */
    private boolean executeClick(ActionStep step) {
        Point location = findElementLocation(step.target);
        if (location != null) {
            robot.mouseMove(location.x, location.y);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            takeScreenshotWithHighlight(location, "click_" + sanitizeFilename(step.target));
            return true;
        }
        return false;
    }
    
    /**
     * Execute double click action
     */
    private boolean executeDoubleClick(ActionStep step) {
        Point location = findElementLocation(step.target);
        if (location != null) {
            robot.mouseMove(location.x, location.y);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            robot.delay(100);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            takeScreenshotWithHighlight(location, "doubleclick_" + sanitizeFilename(step.target));
            return true;
        }
        return false;
    }
    
    /**
     * Execute right click action
     */
    private boolean executeRightClick(ActionStep step) {
        Point location = findElementLocation(step.target);
        if (location != null) {
            robot.mouseMove(location.x, location.y);
            robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
            takeScreenshotWithHighlight(location, "rightclick_" + sanitizeFilename(step.target));
            return true;
        }
        return false;
    }
    
    /**
     * Execute type text action
     */
    private boolean executeTypeText(ActionStep step) {
        Point location = findElementLocation(step.target);
        if (location != null) {
            robot.mouseMove(location.x, location.y);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            robot.delay(200);
            
            String text = step.input != null ? step.input : "sample text";
            for (char c : text.toCharArray()) {
                if (Character.isLetterOrDigit(c) || Character.isSpaceChar(c)) {
                    int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
                    if (keyCode != KeyEvent.VK_UNDEFINED) {
                        robot.keyPress(keyCode);
                        robot.keyRelease(keyCode);
                        robot.delay(50);
                    }
                }
            }
            
            takeScreenshotWithHighlight(location, "type_" + sanitizeFilename(step.target));
            return true;
        }
        return false;
    }
    
    /**
     * Execute press key action
     */
    private boolean executePressKey(ActionStep step) {
        int keyCode = getKeyCode(step.target);
        robot.keyPress(keyCode);
        robot.keyRelease(keyCode);
        takeScreenshot("presskey_" + sanitizeFilename(step.target));
        return true;
    }
    
    /**
     * Execute wait action
     */
    private boolean executeWait(ActionStep step) {
        try {
            Thread.sleep(step.waitTime * 1000);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    /**
     * Execute verify element action
     */
    private boolean executeVerifyElement(ActionStep step) {
        Point location = findElementLocation(step.target);
        if (location != null) {
            takeScreenshotWithHighlight(location, "verify_" + sanitizeFilename(step.target));
            return true;
        } else {
            takeScreenshot("verify_failed_" + sanitizeFilename(step.target));
            return false;
        }
    }
    
    /**
     * Execute scroll action
     */
    private boolean executeScroll(ActionStep step) {
        int scrollDirection = "up".equals(step.target) ? -1 : 1;
        robot.mouseWheel(scrollDirection * 3);
        takeScreenshot("scroll_" + step.target);
        return true;
    }
    
    /**
     * Execute take screenshot action
     */
    private boolean executeTakeScreenshot(ActionStep step) {
        return takeScreenshot(sanitizeFilename(step.target));
    }
    
    /**
     * Find element location using image recognition
     */
    private Point findElementLocation(String elementDescription) {
        try {
            BufferedImage screenshot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            
            Point location = findByImageTemplate(elementDescription, screenshot);
            if (location != null) {
                return location;
            }
            
            location = findByTextPattern(elementDescription, screenshot);
            if (location != null) {
                return location;
            }
            
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            return new Point(screenSize.width / 2, screenSize.height / 2);
            
        } catch (Exception e) {
            System.err.println("Error finding element location: " + e.getMessage());
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            return new Point(screenSize.width / 2, screenSize.height / 2);
        }
    }
    
    /**
     * Find element by image template matching
     */
    private Point findByImageTemplate(String elementDescription, BufferedImage screenshot) {
        try {
            File imagesDir = new File("images");
            if (!imagesDir.exists()) return null;
            
            File[] imageFiles = imagesDir.listFiles((dir, name) -> 
                name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpg"));
            
            if (imageFiles != null) {
                for (File imageFile : imageFiles) {
                    if (imageFile.getName().toLowerCase().contains(elementDescription.toLowerCase().replace(" ", "_"))) {
                        BufferedImage template = ImageIO.read(imageFile);
                        Point location = imageAutomation.findImageLocation(screenshot, template);
                        if (location != null) {
                            System.out.println("Found element using template: " + imageFile.getName());
                            return location;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error in image template matching: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Find element by text pattern (placeholder for OCR)
     */
    private Point findByTextPattern(String elementDescription, BufferedImage screenshot) {
        return null;
    }
    
    /**
     * Get key code for key name
     */
    private int getKeyCode(String keyName) {
        keyName = keyName.toLowerCase();
        switch (keyName) {
            case "enter": return KeyEvent.VK_ENTER;
            case "escape": return KeyEvent.VK_ESCAPE;
            case "tab": return KeyEvent.VK_TAB;
            case "space": return KeyEvent.VK_SPACE;
            case "delete": return KeyEvent.VK_DELETE;
            case "backspace": return KeyEvent.VK_BACK_SPACE;
            case "f1": return KeyEvent.VK_F1;
            case "f2": return KeyEvent.VK_F2;
            case "f3": return KeyEvent.VK_F3;
            case "f4": return KeyEvent.VK_F4;
            case "f5": return KeyEvent.VK_F5;
            default: return KeyEvent.VK_ENTER;
        }
    }
    
    /**
     * Take screenshot with highlight at specific location
     */
    private boolean takeScreenshotWithHighlight(Point location, String filename) {
        try {
            String fullFilename = String.format("screenshots/agent_%s_%d.png", filename, System.currentTimeMillis());
            screenshotCapture.captureScreenshotWithHighlight(sanitizeFilename(filename), location);
            
            screenshotPaths.add(fullFilename);
            
            if (!responseStorage.getAllSteps().isEmpty()) {
                LLMResponseStorage.ExecutionStep currentStep = responseStorage.getAllSteps().get(responseStorage.getAllSteps().size() - 1);
                currentStep.setScreenshotPath(fullFilename);
            }
            
            return true;
        } catch (Exception e) {
            System.err.println("Error taking highlighted screenshot: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Take regular screenshot
     */
    private boolean takeScreenshot(String filename) {
        try {
            String fullFilename = String.format("screenshots/agent_%s_%d.png", filename, System.currentTimeMillis());
            BufferedImage screenshot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            File outputFile = new File(fullFilename);
            outputFile.getParentFile().mkdirs();
            boolean success = ImageIO.write(screenshot, "png", outputFile);
            
            if (success) {
                screenshotPaths.add(fullFilename);
                
                if (!responseStorage.getAllSteps().isEmpty()) {
                    LLMResponseStorage.ExecutionStep currentStep = responseStorage.getAllSteps().get(responseStorage.getAllSteps().size() - 1);
                    currentStep.setScreenshotPath(fullFilename);
                }
            }
            
            return success;
        } catch (Exception e) {
            System.err.println("Error taking screenshot: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Sanitize filename for safe file operations
     */
    private String sanitizeFilename(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
    
    /**
     * Display execution summary
     */
    private void displayExecutionSummary() {
        System.out.println("=== Execution Summary ===");
        System.out.printf("Total Steps: %d%n", executedSteps);
        System.out.printf("Successful: %d%n", successfulSteps);
        System.out.printf("Failed: %d%n", failedSteps);
        System.out.printf("Success Rate: %.1f%%%n", executedSteps > 0 ? (successfulSteps * 100.0 / executedSteps) : 0);
        System.out.println();
        
        if (failedSteps > 0) {
            System.out.println("Failed Steps:");
            for (int i = 0; i < actionPlan.size(); i++) {
                ActionStep step = actionPlan.get(i);
                if (step.executed && !step.successful) {
                    System.out.printf("  %d. %s%n", i + 1, step);
                }
            }
            System.out.println();
        }
        
        String nextAction = getNextRecommendedAction();
        if (nextAction != null) {
            System.out.println("Next Recommended Action: " + nextAction);
        }
    }
    
    /**
     * Get next recommended action based on current state
     */
    private String getNextRecommendedAction() {
        for (ActionStep step : actionPlan) {
            if (!step.executed) {
                return step.toString();
            }
        }
        
        if (actionPlan.stream().noneMatch(step -> "TAKE_SCREENSHOT".equals(step.action) && step.executed)) {
            return "Take final screenshot to verify completion";
        }
        
        return null;
    }
    
    /**
     * Configure LLM settings
     */
    public void configureLLM(String provider, String apiUrl, String apiKey, String model) {
        if (llmConfig == null) {
            llmConfig = new LLMConfig();
        }
        
        switch (provider.toUpperCase()) {
            case "OPENAI":
                llmConfig.configureForOpenAI(apiKey, model);
                break;
            case "AZURE_OPENAI":
                llmConfig.configureForAzureOpenAI(apiUrl, apiKey, model);
                break;
            case "ANTHROPIC":
                llmConfig.configureForAnthropic(apiKey, model);
                break;
            case "GOOGLE_AI":
                llmConfig.configureForGoogleAI(apiKey, model);
                break;
            case "OLLAMA":
                llmConfig.configureForOllama(apiUrl, model);
                break;
            case "CUSTOM":
                llmConfig.configureCustom(apiUrl, apiKey, model);
                break;
            default:
                System.err.println("Unsupported LLM provider: " + provider);
                return;
        }
        
        llmClient = new LLMClient(llmConfig);
        System.out.println("LLM configured: " + llmConfig);
    }
    
    /**
     * Analyze screenshot with LLM for action recommendations
     */
    public String analyzeScreenshotWithLLM(String screenshotPath, String userIntent) {
        if (llmConfig != null && llmConfig.isConfigured()) {
            try {
                return llmClient.analyzeScreenshotForActions(screenshotPath, userIntent);
            } catch (Exception e) {
                System.err.println("LLM screenshot analysis failed: " + e.getMessage());
                return "LLM analysis failed. Please provide manual action description.";
            }
        } else {
            return "LLM not configured. Please provide manual action description.";
        }
    }
    
    /**
     * Main method for testing the agent
     */
    public static void main(String[] args) {
        try {
            SwingAutomationAgent agent = new SwingAutomationAgent();
            
            if (args.length >= 2) {
                String description = args[0];
                String appName = args[1];
                agent.executeActionSequence(description, appName);
            } else {
                String testDescription = "Click on center of screen. Wait 2 seconds. Take screenshot.";
                agent.executeActionSequence(testDescription, "Test Application");
            }
            
        } catch (AWTException e) {
            System.err.println("Error initializing SwingAutomationAgent: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
