# SwingAutomationAgent - Intelligent Automation with Natural Language

The SwingAutomationAgent is an intelligent automation system that reads human descriptions of actions and executes them using the existing SwingImageAutomation framework. It provides natural language processing capabilities for UI automation with deterministic step-by-step execution.

## Features

- **Natural Language Processing**: Parse human action descriptions into executable automation steps
- **Image Recognition Integration**: Leverage existing SwingImageAutomation framework for element detection
- **Visual Feedback**: Red box highlighting for action locations using existing screenshot capture tools
- **Application Binding**: Focus automation on specific applications using existing window detection
- **Deterministic Execution**: Step-by-step action execution with detailed logging and error handling
- **GUI Interface**: User-friendly ActionPlannerGUI for interactive automation planning
- **CLI Interface**: Command-line interface for programmatic automation execution

## Quick Start

### Using the GUI Interface

```bash
# Compile the project
mvn clean compile

# Launch the GUI interface
java -cp target/classes ActionPlannerGUI
```

1. Enter your target application name (e.g., "Calculator", "Notepad")
2. Describe your actions in natural language in the text area
3. Click "Parse Description" to see the parsed action plan
4. Click "Execute Plan" to run the automation

### Using the CLI Interface

```bash
# Run agent with action description and target application
java -cp target/classes SwingAutomationAgent "Click on File menu. Click on Open option. Type \"test.txt\" in filename field." "MyApplication"
```

### Testing the Agent

```bash
# Run the test suite
java -cp target/classes AgentTestRunner
```

## Supported Actions

### Click Actions
- `Click on [element]` - Click on UI element (uses image recognition)
- `Click on center of screen` - Click at screen center
- `Click on [button/menu/field]` - Click on specific UI components

### Text Input
- `Type "[text]"` - Type specified text at current cursor position
- `Type "[text]" in [field]` - Type text in specific field (if found)

### Keyboard Actions
- `Press [key] key` - Press specific keys (Enter, Escape, Tab, Space, etc.)
- `Press Ctrl+[key]` - Keyboard shortcuts (Ctrl+C, Ctrl+V, etc.)

### Wait and Timing
- `Wait [number] seconds` - Pause execution for specified duration
- `Wait [number] second` - Single second wait

### Verification and Screenshots
- `Verify that [condition]` - Verify UI state or element presence
- `Take screenshot` - Capture current screen state
- `Take screenshot for verification` - Capture with verification context

## Example Action Descriptions

### File Operations
```
Click on File menu.
Click on Open option.
Type "document.txt" in filename field.
Press Enter key.
Wait 2 seconds.
Verify that file is opened.
Take screenshot for verification.
```

### Form Filling
```
Click on username field.
Type "john.doe@example.com".
Click on password field.
Type "mypassword123".
Click on login button.
Wait 3 seconds.
Verify that dashboard is loaded.
```

### Application Navigation
```
Click on Tools menu.
Click on Options submenu.
Click on Advanced tab.
Click on Enable feature checkbox.
Click on Apply button.
Click on OK button.
Take screenshot.
```

## Integration with Existing Framework

The agent seamlessly integrates with existing components:

### SwingImageAutomation Integration
- Uses `findImageLocation()` for element detection
- Leverages `clickAt()`, `typeText()`, and `pressKey()` for actions
- Utilizes `captureScreen()` for screenshot analysis

### Screenshot Capture Integration
- Inherits application binding from ScreenshotCapture
- Uses red box highlighting for action visualization
- Maintains compatibility with existing capture tools

### Application Binding
- Supports targeting specific applications by name
- Uses existing window detection mechanisms
- Filters actions to bound applications when specified

## Architecture

### Core Components

1. **SwingAutomationAgent.java**
   - Main agent class with natural language processing
   - Action parsing and execution engine
   - Integration with existing automation framework

2. **ActionPlannerGUI.java**
   - User-friendly GUI interface
   - Real-time action plan display
   - Execution progress tracking and logging

3. **AgentTestRunner.java**
   - Comprehensive test suite for agent validation
   - Sample scenarios and edge case testing
   - Integration verification with existing framework

### Action Processing Flow

1. **Parse Description**: Convert natural language to ActionStep objects
2. **Plan Display**: Show parsed actions for user review
3. **Execute Steps**: Run each action sequentially with visual feedback
4. **Error Handling**: Continue execution with detailed error reporting
5. **Screenshot Capture**: Document action locations with red box highlights
6. **Logging**: Provide comprehensive execution logs and statistics

### Error Handling and Fallbacks

- **Element Not Found**: Continues execution with detailed error messages
- **Image Recognition Failure**: Provides coordinate-based fallback options
- **Application Binding**: Graceful handling when target app is not active
- **Execution Interruption**: Safe cleanup and state preservation

## Configuration

### Image Recognition Settings
- Template matching threshold: Configurable in SwingImageAutomation
- Image directories: `captured_images/` and `images/` for templates
- Screenshot formats: PNG and JPG support

### Timing and Delays
- Default action delay: 500ms between actions
- Type delay: 100ms between characters
- Wait precision: 1-second granularity

### Application Binding
- Window title matching: Partial and exact matching support
- Cross-platform compatibility: Windows, Linux, macOS
- Real-time application state monitoring

## Advanced Usage

### Custom Action Patterns
The agent supports extensible action patterns through regex matching:

```java
// Add custom patterns in parseActionDescription()
if (description.matches(".*custom pattern.*")) {
    // Handle custom action
}
```

### Integration with External Tools
```bash
# Use with existing screenshot capture
java -cp target/classes ScreenshotCapture &
java -cp target/classes SwingAutomationAgent "Your actions here" "Target App"
```

### Batch Processing
```bash
# Process multiple action sequences
for action in "action1" "action2" "action3"; do
    java -cp target/classes SwingAutomationAgent "$action" "MyApp"
done
```

## Troubleshooting

### Common Issues

1. **Element Not Found**
   - Ensure target application is visible and active
   - Check that image templates exist in `captured_images/` or `images/`
   - Verify application binding is correctly configured

2. **Image Recognition Failures**
   - Capture fresh screenshots of UI elements
   - Adjust template matching threshold in SwingImageAutomation
   - Use coordinate-based fallbacks for problematic elements

3. **Application Binding Issues**
   - Verify application window title matches exactly
   - Check that target application is in foreground
   - Test window detection with existing ScreenshotCapture tools

### Debug Mode
Enable detailed logging by setting system properties:
```bash
java -Djava.util.logging.level=FINE -cp target/classes SwingAutomationAgent "actions" "app"
```

### Performance Optimization
- Use specific element descriptions for faster image matching
- Minimize wait times between actions
- Batch similar actions together for efficiency

## Contributing

The agent is designed to be extensible:

1. **Add New Action Types**: Extend ActionType enum and execution logic
2. **Improve NLP**: Enhance pattern matching in parseActionDescription()
3. **Custom UI Elements**: Add specialized element detection methods
4. **Platform Support**: Extend cross-platform compatibility

## License

This project is part of the SwingAutomation framework and follows the same licensing terms.
