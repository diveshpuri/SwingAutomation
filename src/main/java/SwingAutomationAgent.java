import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
     * Execute individual action step
     */
    private boolean executeStep(ActionStep step) {
        try {
            switch (step.action) {
                case "CLICK":
                    return executeClick(step);
                case "DOUBLE_CLICK":
                    return executeDoubleClick(step);
                case "RIGHT_CLICK":
                    return executeRightClick(step);
                case "TYPE_TEXT":
                    return executeTypeText(step);
                case "PRESS_KEY":
                    return executePressKey(step);
                case "WAIT":
                    return executeWait(step);
                case "VERIFY_ELEMENT":
                    return executeVerifyElement(step);
                case "SCROLL":
                    return executeScroll(step);
                case "TAKE_SCREENSHOT":
                    return executeTakeScreenshot(step);
                default:
                    System.err.println("Unknown action: " + step.action);
                    return false;
            }
        } catch (Exception e) {
            System.err.println("Error executing step: " + e.getMessage());
            return false;
        }
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
            return ImageIO.write(screenshot, "png", outputFile);
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
