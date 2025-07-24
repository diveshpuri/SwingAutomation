import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Storage and analysis system for LLM responses
 * Maintains execution history for debugging and understanding
 */
public class LLMResponseStorage {
    
    private List<ExecutionStep> executionHistory;
    private ObjectMapper objectMapper;
    private int maxHistorySize;
    
    public LLMResponseStorage() {
        this(1000); // Default max history size
    }
    
    public LLMResponseStorage(int maxHistorySize) {
        this.executionHistory = new ArrayList<>();
        this.objectMapper = new ObjectMapper();
        this.maxHistorySize = maxHistorySize;
    }
    
    /**
     * Execution step data structure
     */
    public static class ExecutionStep {
        private String timestamp;
        private String stepId;
        private String humanInput;
        private String llmPrompt;
        private String llmResponse;
        private String parsedAction;
        private String executionResult;
        private String screenshotPath;
        private Map<String, Object> uiElements;
        private String previousStep;
        private String nextStep;
        private boolean success;
        private String errorMessage;
        private long executionTimeMs;
        
        public ExecutionStep(String stepId) {
            this.stepId = stepId;
            this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            this.uiElements = new HashMap<>();
        }
        
        public String getTimestamp() { return timestamp; }
        public String getStepId() { return stepId; }
        public String getHumanInput() { return humanInput; }
        public void setHumanInput(String humanInput) { this.humanInput = humanInput; }
        public String getLlmPrompt() { return llmPrompt; }
        public void setLlmPrompt(String llmPrompt) { this.llmPrompt = llmPrompt; }
        public String getLlmResponse() { return llmResponse; }
        public void setLlmResponse(String llmResponse) { this.llmResponse = llmResponse; }
        public String getParsedAction() { return parsedAction; }
        public void setParsedAction(String parsedAction) { this.parsedAction = parsedAction; }
        public String getExecutionResult() { return executionResult; }
        public void setExecutionResult(String executionResult) { this.executionResult = executionResult; }
        public String getScreenshotPath() { return screenshotPath; }
        public void setScreenshotPath(String screenshotPath) { this.screenshotPath = screenshotPath; }
        public Map<String, Object> getUiElements() { return uiElements; }
        public void setUiElements(Map<String, Object> uiElements) { this.uiElements = uiElements; }
        public String getPreviousStep() { return previousStep; }
        public void setPreviousStep(String previousStep) { this.previousStep = previousStep; }
        public String getNextStep() { return nextStep; }
        public void setNextStep(String nextStep) { this.nextStep = nextStep; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public long getExecutionTimeMs() { return executionTimeMs; }
        public void setExecutionTimeMs(long executionTimeMs) { this.executionTimeMs = executionTimeMs; }
        
        public void addUIElement(String elementName, Object elementData) {
            this.uiElements.put(elementName, elementData);
        }
        
        @Override
        public String toString() {
            return String.format("ExecutionStep{stepId='%s', timestamp='%s', success=%s, action='%s'}", 
                               stepId, timestamp, success, parsedAction);
        }
    }
    
    /**
     * Store a new execution step
     */
    public ExecutionStep createExecutionStep(String stepId) {
        ExecutionStep step = new ExecutionStep(stepId);
        
        if (!executionHistory.isEmpty()) {
            ExecutionStep previousStep = executionHistory.get(executionHistory.size() - 1);
            step.setPreviousStep(previousStep.getStepId());
            previousStep.setNextStep(stepId);
        }
        
        executionHistory.add(step);
        
        if (executionHistory.size() > maxHistorySize) {
            executionHistory.remove(0);
        }
        
        return step;
    }
    
    /**
     * Get execution step by ID
     */
    public ExecutionStep getExecutionStep(String stepId) {
        return executionHistory.stream()
                .filter(step -> step.getStepId().equals(stepId))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Get recent execution steps
     */
    public List<ExecutionStep> getRecentSteps(int count) {
        int size = executionHistory.size();
        int fromIndex = Math.max(0, size - count);
        return new ArrayList<>(executionHistory.subList(fromIndex, size));
    }
    
    /**
     * Get all execution steps
     */
    public List<ExecutionStep> getAllSteps() {
        return new ArrayList<>(executionHistory);
    }
    
    /**
     * Get execution statistics
     */
    public Map<String, Object> getExecutionStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        int totalSteps = executionHistory.size();
        long successfulSteps = executionHistory.stream().mapToLong(step -> step.isSuccess() ? 1 : 0).sum();
        long failedSteps = totalSteps - successfulSteps;
        
        double successRate = totalSteps > 0 ? (double) successfulSteps / totalSteps * 100 : 0;
        
        long totalExecutionTime = executionHistory.stream().mapToLong(ExecutionStep::getExecutionTimeMs).sum();
        double avgExecutionTime = totalSteps > 0 ? (double) totalExecutionTime / totalSteps : 0;
        
        stats.put("totalSteps", totalSteps);
        stats.put("successfulSteps", successfulSteps);
        stats.put("failedSteps", failedSteps);
        stats.put("successRate", String.format("%.2f%%", successRate));
        stats.put("totalExecutionTimeMs", totalExecutionTime);
        stats.put("avgExecutionTimeMs", String.format("%.2f", avgExecutionTime));
        
        return stats;
    }
    
    /**
     * Generate execution summary for debugging
     */
    public String generateExecutionSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("=== EXECUTION SUMMARY ===\n");
        
        Map<String, Object> stats = getExecutionStatistics();
        summary.append(String.format("Total Steps: %s\n", stats.get("totalSteps")));
        summary.append(String.format("Success Rate: %s\n", stats.get("successRate")));
        summary.append(String.format("Average Execution Time: %s ms\n", stats.get("avgExecutionTimeMs")));
        summary.append("\n");
        
        summary.append("=== RECENT STEPS ===\n");
        List<ExecutionStep> recentSteps = getRecentSteps(10);
        for (ExecutionStep step : recentSteps) {
            summary.append(String.format("[%s] %s - %s - %s\n", 
                step.getTimestamp(), 
                step.getStepId(), 
                step.isSuccess() ? "SUCCESS" : "FAILED",
                step.getParsedAction()));
            
            if (!step.isSuccess() && step.getErrorMessage() != null) {
                summary.append(String.format("  Error: %s\n", step.getErrorMessage()));
            }
        }
        
        return summary.toString();
    }
    
    /**
     * Export execution history to JSON
     */
    public String exportToJson() {
        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("exportTimestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            root.set("statistics", objectMapper.valueToTree(getExecutionStatistics()));
            root.set("executionHistory", objectMapper.valueToTree(executionHistory));
            
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        } catch (Exception e) {
            return "Error exporting to JSON: " + e.getMessage();
        }
    }
    
    /**
     * Find steps with similar patterns for analysis
     */
    public List<ExecutionStep> findSimilarSteps(String action, boolean successOnly) {
        return executionHistory.stream()
                .filter(step -> step.getParsedAction() != null && 
                               step.getParsedAction().toLowerCase().contains(action.toLowerCase()))
                .filter(step -> !successOnly || step.isSuccess())
                .collect(ArrayList::new, (list, step) -> list.add(step), ArrayList::addAll);
    }
    
    /**
     * Get failure analysis
     */
    public Map<String, Object> getFailureAnalysis() {
        Map<String, Object> analysis = new HashMap<>();
        
        List<ExecutionStep> failedSteps = executionHistory.stream()
                .filter(step -> !step.isSuccess())
                .collect(ArrayList::new, (list, step) -> list.add(step), ArrayList::addAll);
        
        Map<String, Long> errorCounts = new HashMap<>();
        for (ExecutionStep step : failedSteps) {
            String error = step.getErrorMessage() != null ? step.getErrorMessage() : "Unknown error";
            errorCounts.put(error, errorCounts.getOrDefault(error, 0L) + 1);
        }
        
        analysis.put("totalFailures", failedSteps.size());
        analysis.put("errorCounts", errorCounts);
        analysis.put("recentFailures", failedSteps.stream()
                .skip(Math.max(0, failedSteps.size() - 5))
                .collect(ArrayList::new, (list, step) -> list.add(step), ArrayList::addAll));
        
        return analysis;
    }
    
    /**
     * Clear execution history
     */
    public void clearHistory() {
        executionHistory.clear();
    }
    
    /**
     * Get execution context for LLM prompting
     */
    public String getExecutionContext(int recentStepsCount) {
        List<ExecutionStep> recentSteps = getRecentSteps(recentStepsCount);
        
        StringBuilder context = new StringBuilder();
        context.append("Recent execution context:\n");
        
        for (ExecutionStep step : recentSteps) {
            context.append(String.format("- Step %s: %s (%s)\n", 
                step.getStepId(), 
                step.getParsedAction(), 
                step.isSuccess() ? "SUCCESS" : "FAILED"));
            
            if (step.getUiElements() != null && !step.getUiElements().isEmpty()) {
                context.append("  UI Elements detected: ");
                context.append(step.getUiElements().keySet().toString());
                context.append("\n");
            }
        }
        
        return context.toString();
    }
}
