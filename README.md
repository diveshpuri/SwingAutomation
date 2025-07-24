# Java Swing Automation Framework

A comprehensive Java-based automation framework for Windows Swing applications, featuring intelligent natural language processing, screenshot capture, image recognition, and deterministic action execution with LLM integration.

## 🚀 Features

### 🤖 **Intelligent Automation Agent**
- **Natural Language Processing**: Parse human action descriptions into executable automation steps
- **LLM Integration**: Configurable support for OpenAI, Azure OpenAI, Anthropic, Google AI, Ollama, and custom APIs
- **Screenshot Analysis**: Analyze application screenshots to identify UI elements and recommend actions
- **Deterministic Execution**: Step-by-step action execution with visual feedback and error handling

### 📸 **Advanced Screenshot Capture**
- **Application Binding**: Focus capture on specific application windows
- **Visual Highlighting**: Red box overlays to mark action locations
- **Hotkey Support**: Global keyboard shortcuts for instant capture
- **Organized Output**: Automatic file naming and directory management

### 🎯 **Image Recognition & Automation**
- **Template Matching**: OpenCV and Sikuli-powered element detection
- **Cross-Platform Development**: Develop on any platform, deploy on Windows
- **Flexible Actions**: Click, type, keyboard shortcuts, verification, and more
- **Error Recovery**: Graceful fallbacks and detailed error reporting

### 🛠 **Development Tools**
- **Maven Integration**: Standardized build and dependency management
- **GUI Interfaces**: User-friendly tools for both capture and automation planning
- **Testing Framework**: Built-in validation and testing scenarios
- **Comprehensive Documentation**: Detailed guides and API reference

## 🏃 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- Windows OS (for target application automation)

### Installation & Setup
```bash
# Clone the repository
git clone https://github.com/diveshpuri/SwingAutomation.git
cd SwingAutomation

# Build the project
mvn clean install
```

### 🤖 Intelligent Agent Usage

#### 1. Natural Language Automation (CLI)
```bash
# Run agent with action description
java -cp target/classes SwingAutomationAgent "Click on File menu. Click on Open. Type 'document.txt'. Press Enter." "Notepad"

# Run with default test scenario
java -cp target/classes SwingAutomationAgent
```

#### 2. Action Planner GUI
```bash
# Launch the interactive planning interface
java -cp target/classes ActionPlannerGUI
```

#### 3. LLM Configuration
Create `llm.properties` file (copy from `llm.properties.example`):
```properties
# OpenAI Configuration
llm.provider=OPENAI
llm.api.url=https://api.openai.com/v1/chat/completions
llm.api.key=your_openai_api_key_here
llm.model=gpt-3.5-turbo

# Azure OpenAI Configuration
#llm.provider=AZURE_OPENAI
#llm.api.url=https://your-resource.openai.azure.com
#llm.api.key=your_azure_api_key_here
#llm.deployment.id=your_deployment_name
```

### 📸 Screenshot Capture Tools

#### GUI Screenshot Tool
```bash
java -cp target/classes ScreenshotCapture
```
Features:
- Application window binding
- Click-based capture with highlighting
- Configurable delays and output directories
- Real-time status feedback

#### CLI Screenshot Tool
```bash
# Basic capture
java -cp target/classes GlobalScreenshotCapture

# Application-specific capture
java -cp target/classes GlobalScreenshotCapture app:Notepad

# Capture with highlighting
java -cp target/classes GlobalScreenshotCapture highlight:100,200
```

### 🎯 Core Automation
```bash
# Run image automation example
java -cp target/classes SwingImageAutomation

# Test automation scenarios
java -cp target/classes AgentTestRunner
```

## 📁 Project Structure

```
SwingAutomation/
├── src/main/java/
│   ├── SwingAutomationAgent.java    # 🤖 Intelligent automation agent
│   ├── ActionPlannerGUI.java        # 🖥️ Agent planning interface
│   ├── LLMConfig.java               # ⚙️ LLM provider configuration
│   ├── LLMClient.java               # 🌐 Universal LLM API client
│   ├── SwingImageAutomation.java    # 🎯 Core automation engine
│   ├── ScreenshotCapture.java       # 📸 GUI screenshot tool
│   ├── GlobalScreenshotCapture.java # 📷 CLI screenshot tool
│   ├── AgentTestRunner.java         # 🧪 Testing framework
│   └── SikuliExample.java          # 📖 Sikuli integration example
├── README_AGENT.md                 # 🤖 Detailed agent documentation
├── llm.properties.example          # ⚙️ LLM configuration template
├── pom.xml                         # 📦 Maven configuration
└── README.md                       # 📋 This file
```

## 🎯 Supported Actions

### Natural Language Commands
The intelligent agent understands human action descriptions:

```
"Click on File menu. Click on Open option. Type 'document.txt' in filename field. Press Enter key."

"Double click on desktop icon. Wait 3 seconds. Verify window opened. Take screenshot."

"Right click at center of screen. Select Properties from menu. Press Escape key."
```

### Action Types
- **CLICK**: `Click on button name`, `Click on center of screen`
- **DOUBLE_CLICK**: `Double click on file icon`
- **RIGHT_CLICK**: `Right click on desktop`
- **TYPE_TEXT**: `Type "Hello World" in text field`
- **PRESS_KEY**: `Press Enter`, `Press Ctrl+C`, `Press F5`
- **WAIT**: `Wait 3 seconds`, `Pause for 1000 milliseconds`
- **VERIFY_ELEMENT**: `Verify login button is visible`
- **TAKE_SCREENSHOT**: `Take screenshot`, `Capture current state`
- **SCROLL**: `Scroll down`, `Scroll up in window`

## 🌐 LLM Provider Support

### Supported APIs
✅ **OpenAI** (GPT-3.5, GPT-4)  
✅ **Azure OpenAI** (with deployment support)  
✅ **Anthropic Claude** (Claude-3 models)  
✅ **Google AI** (Gemini Pro)  
✅ **Local Ollama** (for offline usage)  
✅ **Custom APIs** (flexible endpoint configuration)

### Configuration Examples

#### OpenAI
```properties
llm.provider=OPENAI
llm.api.url=https://api.openai.com/v1/chat/completions
llm.api.key=your_api_key
llm.model=gpt-3.5-turbo
```

#### Azure OpenAI
```properties
llm.provider=AZURE_OPENAI
llm.api.url=https://your-resource.openai.azure.com
llm.api.key=your_api_key
llm.deployment.id=your_deployment_name
```

#### Local Ollama
```properties
llm.provider=LOCAL_OLLAMA
llm.api.url=http://localhost:11434
llm.model=llama2
```

## 📦 Dependencies

### Core Libraries
- **OpenNLP**: Natural language processing for action parsing
- **Sikuli**: GUI automation and image recognition
- **OpenCV**: Computer vision and image processing
- **Jackson**: JSON processing for LLM API communication

### Development & Testing
- **Apache Commons**: Utility libraries
- **SLF4J + Logback**: Logging framework
- **JUnit 5**: Testing framework
- **Mockito**: Mocking for unit tests
- **AssertJ**: Fluent assertions

## 🔧 Usage Examples

### Basic Image Recognition
```java
SwingImageAutomation automation = new SwingImageAutomation();
automation.clickOnImage("button.png");
automation.typeText("Hello World");
automation.pressKey(KeyEvent.VK_ENTER);
```

### Intelligent Agent Integration
```java
SwingAutomationAgent agent = new SwingAutomationAgent();
List<ActionStep> plan = agent.parseHumanDescription("Click login button and enter credentials");
agent.executeActionPlan(plan);
```

### Screenshot Capture with Highlighting
```java
ScreenshotCapture capture = new ScreenshotCapture();
capture.captureScreenshotWithHighlight("login_action", new Point(100, 200));
```

### LLM-Enhanced Analysis
```java
LLMConfig config = new LLMConfig();
config.configureForOpenAI("your-api-key", "gpt-3.5-turbo");
LLMClient client = new LLMClient(config);
String analysis = client.analyzeScreenshotForActions("screenshot.png", "Login to application");
```

## 🧪 Testing & Validation

### Run Test Scenarios
```bash
# Test agent functionality
java -cp target/classes AgentTestRunner

# Test with specific scenarios
java -cp target/classes SwingAutomationAgent "test description" "target app"

# Test GUI interfaces
java -cp target/classes ActionPlannerGUI
java -cp target/classes ScreenshotCapture
```

### Integration Testing
```bash
# Test screenshot capture integration
java -cp target/classes ScreenshotCapture

# Test image automation integration
java -cp target/classes SwingImageAutomation

# Validate Maven build
mvn clean test
```

## 🚀 Advanced Features

### Application Window Binding
- Automatically focus on specific application windows
- Filter screenshots to target applications only
- Cross-platform window detection (Windows, Linux, macOS)

### Visual Action Highlighting
- Red transparent overlays mark action locations
- Configurable highlight duration and appearance
- Integration with screenshot capture for training data

### Error Handling & Recovery
- Graceful degradation when LLM APIs are unavailable
- Coordinate-based fallbacks for image recognition failures
- Detailed error logging and recovery suggestions

### Deterministic Execution
- Step-by-step action plan display and confirmation
- Progress tracking with real-time status updates
- Execution summary with success/failure statistics

## 📚 Documentation

- **[Agent Documentation](README_AGENT.md)**: Comprehensive guide for the intelligent automation agent
- **[Original Automation Guide](README_SwingAutomation.md)**: Core automation framework documentation
- **[LLM Configuration](llm.properties.example)**: Template for LLM provider setup

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Add tests for new functionality
5. Commit your changes (`git commit -m 'feat(area): add amazing feature'`)
6. Push to the branch (`git push origin feature/amazing-feature`)
7. Open a Pull Request

### Commit Convention
This project follows conventional commit patterns:
- `feat(area): description` - New features
- `fix(area): description` - Bug fixes
- `docs(area): description` - Documentation updates
- `test(area): description` - Test additions/updates

## 🐛 Troubleshooting

### Common Issues

#### LLM API Errors
- Check API key configuration in `llm.properties`
- Verify network connectivity to LLM provider
- Review API rate limits and quotas

#### Image Recognition Failures
- Ensure target application is visible and active
- Check image template quality and resolution
- Verify application window binding configuration

#### Headless Environment
- GUI components require display server (Windows/X11)
- Use CLI tools for headless automation scenarios
- Consider remote desktop or virtual display solutions

### Debug Mode
Enable detailed logging:
```bash
java -Djava.util.logging.level=FINE -cp target/classes SwingAutomationAgent
```

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- **OpenCV** for computer vision capabilities
- **Sikuli** for GUI automation framework
- **OpenNLP** for natural language processing
- **Jackson** for JSON processing
- **Maven** for build and dependency management

---

**Link to Devin run**: https://app.devin.ai/sessions/b9a9faeb62e241dfa4027d5639c7d721
