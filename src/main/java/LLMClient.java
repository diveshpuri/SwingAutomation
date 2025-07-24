import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Universal LLM client that supports multiple providers
 * Handles API communication for natural language processing
 */
public class LLMClient {
    
    private LLMConfig config;
    private ObjectMapper objectMapper;
    
    public LLMClient(LLMConfig config) {
        this.config = config;
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Analyze screenshot and provide action recommendations
     */
    public String analyzeScreenshotForActions(String screenshotPath, String userIntent) {
        if (!config.isConfigured()) {
            return "LLM not configured. Using fallback pattern matching.";
        }
        
        String prompt = buildScreenshotAnalysisPrompt(screenshotPath, userIntent);
        
        try {
            return callLLMAPI(prompt);
        } catch (Exception e) {
            System.err.println("LLM API call failed: " + e.getMessage());
            return "LLM analysis failed. Using fallback pattern matching.";
        }
    }
    
    /**
     * Parse natural language action description into structured steps
     */
    public String parseActionDescription(String description) {
        if (!config.isConfigured()) {
            return "LLM not configured. Using basic pattern matching.";
        }
        
        String prompt = buildActionParsingPrompt(description);
        
        try {
            return callLLMAPI(prompt);
        } catch (Exception e) {
            System.err.println("LLM API call failed: " + e.getMessage());
            return "LLM parsing failed. Using basic pattern matching.";
        }
    }
    
    /**
     * Get next action recommendation based on current state
     */
    public String getNextActionRecommendation(String currentState, String goal) {
        if (!config.isConfigured()) {
            return "Continue with next planned action.";
        }
        
        String prompt = buildNextActionPrompt(currentState, goal);
        
        try {
            return callLLMAPI(prompt);
        } catch (Exception e) {
            System.err.println("LLM API call failed: " + e.getMessage());
            return "Continue with next planned action.";
        }
    }
    
    private String buildScreenshotAnalysisPrompt(String screenshotPath, String userIntent) {
        return String.format(
            "Analyze this screenshot and provide specific UI automation actions to achieve: %s\n\n" +
            "Screenshot: %s\n\n" +
            "Provide a step-by-step action plan with specific element descriptions that can be used for image recognition. " +
            "Format each action as: ACTION_TYPE: element_description\n" +
            "Available actions: CLICK, TYPE_TEXT, PRESS_KEY, WAIT, VERIFY, TAKE_SCREENSHOT\n\n" +
            "Example format:\n" +
            "CLICK: login button\n" +
            "TYPE_TEXT: username field -> john.doe@example.com\n" +
            "PRESS_KEY: Enter\n",
            userIntent, screenshotPath
        );
    }
    
    private String buildActionParsingPrompt(String description) {
        return String.format(
            "Parse this natural language description into structured automation actions:\n\n" +
            "\"%s\"\n\n" +
            "Convert to structured format:\n" +
            "ACTION_TYPE: element_description [-> input_text]\n\n" +
            "Available actions:\n" +
            "- CLICK: element_name\n" +
            "- TYPE_TEXT: field_name -> text_to_type\n" +
            "- PRESS_KEY: key_name\n" +
            "- WAIT: duration_in_seconds\n" +
            "- VERIFY: element_to_verify\n" +
            "- TAKE_SCREENSHOT\n\n" +
            "Provide only the structured actions, one per line.",
            description
        );
    }
    
    private String buildNextActionPrompt(String currentState, String goal) {
        return String.format(
            "Current automation state: %s\n" +
            "Goal: %s\n\n" +
            "What should be the next action to progress toward the goal?\n" +
            "Provide a single specific action in the format: ACTION_TYPE: element_description",
            currentState, goal
        );
    }
    
    private String callLLMAPI(String prompt) throws Exception {
        switch (config.getProvider()) {
            case OPENAI:
                return callOpenAI(prompt);
            case AZURE_OPENAI:
                return callAzureOpenAI(prompt);
            case ANTHROPIC:
                return callAnthropic(prompt);
            case GOOGLE_AI:
                return callGoogleAI(prompt);
            case LOCAL_OLLAMA:
                return callOllama(prompt);
            case CUSTOM:
                return callCustomAPI(prompt);
            default:
                throw new UnsupportedOperationException("Provider not supported: " + config.getProvider());
        }
    }
    
    private String callOpenAI(String prompt) throws Exception {
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", config.getModel());
        requestBody.put("max_tokens", config.getMaxTokens());
        requestBody.put("temperature", config.getTemperature());
        
        ArrayNode messages = requestBody.putArray("messages");
        ObjectNode message = messages.addObject();
        message.put("role", "user");
        message.put("content", prompt);
        
        HttpURLConnection connection = createConnection(config.getApiUrl());
        connection.setRequestProperty("Authorization", "Bearer " + config.getApiKey());
        connection.setRequestProperty("Content-Type", "application/json");
        
        if (config.getOrganizationId() != null) {
            connection.setRequestProperty("OpenAI-Organization", config.getOrganizationId());
        }
        
        return sendRequest(connection, requestBody);
    }
    
    private String callAzureOpenAI(String prompt) throws Exception {
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("max_tokens", config.getMaxTokens());
        requestBody.put("temperature", config.getTemperature());
        
        ArrayNode messages = requestBody.putArray("messages");
        ObjectNode message = messages.addObject();
        message.put("role", "user");
        message.put("content", prompt);
        
        HttpURLConnection connection = createConnection(config.getApiUrl());
        connection.setRequestProperty("api-key", config.getApiKey());
        connection.setRequestProperty("Content-Type", "application/json");
        
        return sendRequest(connection, requestBody);
    }
    
    private String callAnthropic(String prompt) throws Exception {
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", config.getModel());
        requestBody.put("max_tokens", config.getMaxTokens());
        requestBody.put("temperature", config.getTemperature());
        
        ArrayNode messages = requestBody.putArray("messages");
        ObjectNode message = messages.addObject();
        message.put("role", "user");
        message.put("content", prompt);
        
        HttpURLConnection connection = createConnection(config.getApiUrl());
        connection.setRequestProperty("x-api-key", config.getApiKey());
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("anthropic-version", "2023-06-01");
        
        return sendRequest(connection, requestBody);
    }
    
    private String callGoogleAI(String prompt) throws Exception {
        ObjectNode requestBody = objectMapper.createObjectNode();
        
        ObjectNode generationConfig = requestBody.putObject("generationConfig");
        generationConfig.put("maxOutputTokens", config.getMaxTokens());
        generationConfig.put("temperature", config.getTemperature());
        
        ArrayNode contents = requestBody.putArray("contents");
        ObjectNode content = contents.addObject();
        ArrayNode parts = content.putArray("parts");
        ObjectNode part = parts.addObject();
        part.put("text", prompt);
        
        String urlWithKey = config.getApiUrl() + "?key=" + config.getApiKey();
        HttpURLConnection connection = createConnection(urlWithKey);
        connection.setRequestProperty("Content-Type", "application/json");
        
        return sendRequest(connection, requestBody);
    }
    
    private String callOllama(String prompt) throws Exception {
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", config.getModel());
        
        ArrayNode messages = requestBody.putArray("messages");
        ObjectNode message = messages.addObject();
        message.put("role", "user");
        message.put("content", prompt);
        
        HttpURLConnection connection = createConnection(config.getApiUrl());
        connection.setRequestProperty("Content-Type", "application/json");
        
        return sendRequest(connection, requestBody);
    }
    
    private String callCustomAPI(String prompt) throws Exception {
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", config.getModel());
        requestBody.put("prompt", prompt);
        requestBody.put("max_tokens", config.getMaxTokens());
        requestBody.put("temperature", config.getTemperature());
        
        HttpURLConnection connection = createConnection(config.getApiUrl());
        if (config.getApiKey() != null) {
            connection.setRequestProperty("Authorization", "Bearer " + config.getApiKey());
        }
        connection.setRequestProperty("Content-Type", "application/json");
        
        return sendRequest(connection, requestBody);
    }
    
    private HttpURLConnection createConnection(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(60000);
        return connection;
    }
    
    private String sendRequest(HttpURLConnection connection, ObjectNode requestBody) throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(requestBody);
        
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        
        int responseCode = connection.getResponseCode();
        
        InputStream inputStream = responseCode >= 200 && responseCode < 300 
            ? connection.getInputStream() 
            : connection.getErrorStream();
        
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }
        
        if (responseCode >= 200 && responseCode < 300) {
            return extractResponseContent(response.toString());
        } else {
            throw new RuntimeException("API call failed with code " + responseCode + ": " + response.toString());
        }
    }
    
    private String extractResponseContent(String jsonResponse) throws Exception {
        JsonNode responseNode = objectMapper.readTree(jsonResponse);
        
        switch (config.getProvider()) {
            case OPENAI:
            case AZURE_OPENAI:
            case CUSTOM:
                return responseNode.path("choices").get(0).path("message").path("content").asText();
            case ANTHROPIC:
                return responseNode.path("content").get(0).path("text").asText();
            case GOOGLE_AI:
                return responseNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
            case LOCAL_OLLAMA:
                return responseNode.path("message").path("content").asText();
            default:
                return jsonResponse;
        }
    }
}
