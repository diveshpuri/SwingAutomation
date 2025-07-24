import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Generates reusable automation scripts from LLM responses
 * Reduces LLM API costs by creating deterministic scripts
 */
public class AutomationScriptGenerator {
    
    private String outputDirectory = "generated_scripts";
    private Map<String, String> scriptTemplates;
    
    public AutomationScriptGenerator() {
        this.scriptTemplates = new HashMap<>();
        initializeTemplates();
        createOutputDirectory();
    }
    
    private void initializeTemplates() {
        scriptTemplates.put("JAVA_CLASS", 
            "import java.awt.*;\n" +
            "import java.awt.event.*;\n" +
            "import java.awt.image.BufferedImage;\n" +
            "import javax.imageio.ImageIO;\n" +
            "import java.io.File;\n\n" +
            "/**\n" +
            " * Generated automation script: {SCRIPT_NAME}\n" +
            " * Generated on: {TIMESTAMP}\n" +
            " * Original description: {DESCRIPTION}\n" +
            " */\n" +
            "public class {CLASS_NAME} {\n" +
            "    private Robot robot;\n" +
            "    private SwingImageAutomation imageAutomation;\n\n" +
            "    public {CLASS_NAME}() throws AWTException {\n" +
            "        this.robot = new Robot();\n" +
            "        this.imageAutomation = new SwingImageAutomation();\n" +
            "    }\n\n" +
            "    public boolean execute() {\n" +
            "        try {\n" +
            "{ACTIONS}" +
            "            return true;\n" +
            "        } catch (Exception e) {\n" +
            "            System.err.println(\"Execution failed: \" + e.getMessage());\n" +
            "            return false;\n" +
            "        }\n" +
            "    }\n\n" +
            "    public static void main(String[] args) {\n" +
            "        try {\n" +
            "            {CLASS_NAME} script = new {CLASS_NAME}();\n" +
            "            boolean success = script.execute();\n" +
            "            System.out.println(\"Script execution: \" + (success ? \"SUCCESS\" : \"FAILED\"));\n" +
            "        } catch (Exception e) {\n" +
            "            System.err.println(\"Error: \" + e.getMessage());\n" +
            "        }\n" +
            "    }\n" +
            "}\n"
        );
    }
    
    /**
     * Generate automation script from LLM response and action plan
     */
    public String generateScript(String scriptName, String description, 
                               List<SwingAutomationAgent.ActionStep> actionPlan, 
                               String llmResponse) {
        try {
            String className = sanitizeClassName(scriptName);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            
            StringBuilder actions = new StringBuilder();
            int stepNumber = 1;
            
            for (SwingAutomationAgent.ActionStep step : actionPlan) {
                actions.append(generateActionCode(step, stepNumber++));
            }
            
            String script = scriptTemplates.get("JAVA_CLASS")
                .replace("{SCRIPT_NAME}", scriptName)
                .replace("{CLASS_NAME}", className)
                .replace("{TIMESTAMP}", timestamp)
                .replace("{DESCRIPTION}", description.replace("\"", "\\\""))
                .replace("{ACTIONS}", actions.toString());
            
            String filename = className + ".java";
            String filepath = outputDirectory + "/" + filename;
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
                writer.print(script);
            }
            
            generateMetadata(className, description, llmResponse, actionPlan);
            
            System.out.println("Generated automation script: " + filepath);
            return filepath;
            
        } catch (Exception e) {
            System.err.println("Error generating script: " + e.getMessage());
            return null;
        }
    }
    
    private String generateActionCode(SwingAutomationAgent.ActionStep step, int stepNumber) {
        StringBuilder code = new StringBuilder();
        code.append("            // Step ").append(stepNumber).append(": ").append(step.toString()).append("\n");
        
        switch (step.action) {
            case "CLICK":
                code.append("            Point location").append(stepNumber).append(" = findElementLocation(\"").append(step.target).append("\");\n");
                code.append("            if (location").append(stepNumber).append(" != null) {\n");
                code.append("                robot.mouseMove(location").append(stepNumber).append(".x, location").append(stepNumber).append(".y);\n");
                code.append("                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);\n");
                code.append("                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);\n");
                code.append("            }\n");
                break;
            case "TYPE_TEXT":
                code.append("            typeText(\"").append(step.input != null ? step.input : "").append("\");\n");
                break;
            case "PRESS_KEY":
                code.append("            robot.keyPress(KeyEvent.VK_").append(step.target.toUpperCase()).append(");\n");
                code.append("            robot.keyRelease(KeyEvent.VK_").append(step.target.toUpperCase()).append(");\n");
                break;
            case "WAIT":
                code.append("            Thread.sleep(").append(step.waitTime * 1000).append(");\n");
                break;
            case "TAKE_SCREENSHOT":
                code.append("            takeScreenshot(\"").append(step.target).append("\");\n");
                break;
        }
        
        code.append("            robot.delay(500);\n\n");
        return code.toString();
    }
    
    private void generateMetadata(String className, String description, String llmResponse, 
                                List<SwingAutomationAgent.ActionStep> actionPlan) {
        try {
            String metadataFile = outputDirectory + "/" + className + "_metadata.json";
            
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"scriptName\": \"").append(className).append("\",\n");
            json.append("  \"description\": \"").append(description.replace("\"", "\\\"")).append("\",\n");
            json.append("  \"generatedAt\": \"").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\",\n");
            json.append("  \"llmResponse\": \"").append(llmResponse.replace("\"", "\\\"")).append("\",\n");
            json.append("  \"actionCount\": ").append(actionPlan.size()).append(",\n");
            json.append("  \"actions\": [\n");
            
            for (int i = 0; i < actionPlan.size(); i++) {
                SwingAutomationAgent.ActionStep step = actionPlan.get(i);
                json.append("    {\n");
                json.append("      \"action\": \"").append(step.action).append("\",\n");
                json.append("      \"target\": \"").append(step.target).append("\",\n");
                json.append("      \"input\": \"").append(step.input != null ? step.input : "").append("\"\n");
                json.append("    }").append(i < actionPlan.size() - 1 ? "," : "").append("\n");
            }
            
            json.append("  ]\n");
            json.append("}\n");
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(metadataFile))) {
                writer.print(json.toString());
            }
            
        } catch (Exception e) {
            System.err.println("Error generating metadata: " + e.getMessage());
        }
    }
    
    private String sanitizeClassName(String name) {
        return name.replaceAll("[^a-zA-Z0-9]", "").replaceAll("^[0-9]", "Script");
    }
    
    private void createOutputDirectory() {
        File dir = new File(outputDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
