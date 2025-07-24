import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Configuration class for LLM API integration
 * Supports multiple LLM providers like OpenAI, Azure OpenAI, etc.
 */
public class LLMConfig {
    
    public enum LLMProvider {
        OPENAI,
        AZURE_OPENAI,
        ANTHROPIC,
        GOOGLE_AI,
        LOCAL_OLLAMA,
        CUSTOM
    }
    
    private LLMProvider provider;
    private String apiUrl;
    private String apiKey;
    private String model;
    private int maxTokens;
    private double temperature;
    private String organizationId;
    private String deploymentId;
    
    public LLMConfig() {
        setDefaults();
        loadFromProperties();
        loadFromEnvironment();
    }
    
    private void setDefaults() {
        this.provider = LLMProvider.OPENAI;
        this.apiUrl = "https://api.openai.com/v1/chat/completions";
        this.model = "gpt-3.5-turbo";
        this.maxTokens = 1000;
        this.temperature = 0.7;
    }
    
    private void loadFromProperties() {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("llm.properties"));
            
            String providerStr = props.getProperty("llm.provider");
            if (providerStr != null) {
                this.provider = LLMProvider.valueOf(providerStr.toUpperCase());
            }
            
            this.apiUrl = props.getProperty("llm.api.url", this.apiUrl);
            this.apiKey = props.getProperty("llm.api.key", this.apiKey);
            this.model = props.getProperty("llm.model", this.model);
            this.organizationId = props.getProperty("llm.organization.id");
            this.deploymentId = props.getProperty("llm.deployment.id");
            
            String maxTokensStr = props.getProperty("llm.max.tokens");
            if (maxTokensStr != null) {
                this.maxTokens = Integer.parseInt(maxTokensStr);
            }
            
            String temperatureStr = props.getProperty("llm.temperature");
            if (temperatureStr != null) {
                this.temperature = Double.parseDouble(temperatureStr);
            }
            
        } catch (IOException | NumberFormatException e) {
            System.out.println("No llm.properties file found or invalid format, using defaults");
        }
    }
    
    private void loadFromEnvironment() {
        String envProvider = System.getenv("LLM_PROVIDER");
        if (envProvider != null) {
            this.provider = LLMProvider.valueOf(envProvider.toUpperCase());
        }
        
        String envApiUrl = System.getenv("LLM_API_URL");
        if (envApiUrl != null) {
            this.apiUrl = envApiUrl;
        }
        
        String envApiKey = System.getenv("LLM_API_KEY");
        if (envApiKey != null) {
            this.apiKey = envApiKey;
        }
        
        String envModel = System.getenv("LLM_MODEL");
        if (envModel != null) {
            this.model = envModel;
        }
        
        String envOrgId = System.getenv("LLM_ORGANIZATION_ID");
        if (envOrgId != null) {
            this.organizationId = envOrgId;
        }
        
        String envDeploymentId = System.getenv("LLM_DEPLOYMENT_ID");
        if (envDeploymentId != null) {
            this.deploymentId = envDeploymentId;
        }
        
        String envMaxTokens = System.getenv("LLM_MAX_TOKENS");
        if (envMaxTokens != null) {
            try {
                this.maxTokens = Integer.parseInt(envMaxTokens);
            } catch (NumberFormatException e) {
                System.err.println("Invalid LLM_MAX_TOKENS value: " + envMaxTokens);
            }
        }
        
        String envTemperature = System.getenv("LLM_TEMPERATURE");
        if (envTemperature != null) {
            try {
                this.temperature = Double.parseDouble(envTemperature);
            } catch (NumberFormatException e) {
                System.err.println("Invalid LLM_TEMPERATURE value: " + envTemperature);
            }
        }
    }
    
    public void configureForOpenAI(String apiKey, String model) {
        this.provider = LLMProvider.OPENAI;
        this.apiUrl = "https://api.openai.com/v1/chat/completions";
        this.apiKey = apiKey;
        this.model = model != null ? model : "gpt-3.5-turbo";
    }
    
    public void configureForAzureOpenAI(String endpoint, String apiKey, String deploymentId) {
        this.provider = LLMProvider.AZURE_OPENAI;
        this.apiUrl = endpoint + "/openai/deployments/" + deploymentId + "/chat/completions?api-version=2023-12-01-preview";
        this.apiKey = apiKey;
        this.deploymentId = deploymentId;
        this.model = deploymentId;
    }
    
    public void configureForAnthropic(String apiKey, String model) {
        this.provider = LLMProvider.ANTHROPIC;
        this.apiUrl = "https://api.anthropic.com/v1/messages";
        this.apiKey = apiKey;
        this.model = model != null ? model : "claude-3-sonnet-20240229";
    }
    
    public void configureForGoogleAI(String apiKey, String model) {
        this.provider = LLMProvider.GOOGLE_AI;
        this.apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent";
        this.apiKey = apiKey;
        this.model = model != null ? model : "gemini-pro";
    }
    
    public void configureForOllama(String baseUrl, String model) {
        this.provider = LLMProvider.LOCAL_OLLAMA;
        this.apiUrl = baseUrl + "/api/chat";
        this.model = model != null ? model : "llama2";
        this.apiKey = null;
    }
    
    public void configureCustom(String apiUrl, String apiKey, String model) {
        this.provider = LLMProvider.CUSTOM;
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.model = model;
    }
    
    public LLMProvider getProvider() { return provider; }
    public String getApiUrl() { return apiUrl; }
    public String getApiKey() { return apiKey; }
    public String getModel() { return model; }
    public int getMaxTokens() { return maxTokens; }
    public double getTemperature() { return temperature; }
    public String getOrganizationId() { return organizationId; }
    public String getDeploymentId() { return deploymentId; }
    
    public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    
    public boolean isConfigured() {
        return apiUrl != null && (apiKey != null || provider == LLMProvider.LOCAL_OLLAMA);
    }
    
    @Override
    public String toString() {
        return String.format("LLMConfig{provider=%s, model=%s, apiUrl=%s, configured=%s}", 
                           provider, model, apiUrl, isConfigured());
    }
}
