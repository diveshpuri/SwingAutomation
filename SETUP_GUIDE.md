# Java Swing Automation Framework - Setup Guide

This guide provides detailed instructions for setting up the Java Swing Automation Framework on Windows, macOS, and Linux platforms.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Windows Setup](#windows-setup)
- [macOS Setup](#macos-setup)
- [Linux Setup](#linux-setup)
- [Configuration](#configuration)
- [Troubleshooting](#troubleshooting)

## Prerequisites

The following prerequisites are required for all platforms:

- **Java Development Kit (JDK)** - Version 17 or higher
- **Maven** - Version 3.8.0 or higher
- **Git** - For cloning the repository

## Windows Setup

### Step 1: Install Java JDK

1. Download the latest JDK from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://adoptium.net/)
2. Run the installer and follow the installation wizard
3. Set the `JAVA_HOME` environment variable:
   - Right-click on "This PC" and select "Properties"
   - Click on "Advanced system settings"
   - Click on "Environment Variables"
   - Under "System variables", click "New"
   - Variable name: `JAVA_HOME`
   - Variable value: Path to your JDK installation (e.g., `C:\Program Files\Java\jdk-17`)
4. Add Java to your PATH:
   - Edit the `Path` system variable
   - Add `%JAVA_HOME%\bin`
5. Verify installation by opening Command Prompt and typing:
   ```
   java -version
   ```

### Step 2: Install Maven

1. Download Maven from [Apache Maven website](https://maven.apache.org/download.cgi)
2. Extract the archive to a directory of your choice (e.g., `C:\Program Files\Maven`)
3. Set the `M2_HOME` environment variable:
   - Variable name: `M2_HOME`
   - Variable value: Path to your Maven installation (e.g., `C:\Program Files\Maven`)
4. Add Maven to your PATH:
   - Edit the `Path` system variable
   - Add `%M2_HOME%\bin`
5. Verify installation:
   ```
   mvn -version
   ```

### Step 3: Clone and Build the Framework

1. Open Command Prompt
2. Clone the repository:
   ```
   git clone https://github.com/diveshpuri/SwingAutomation.git
   cd SwingAutomation
   ```
3. Build the project:
   ```
   mvn clean install
   ```

### Step 4: Windows-Specific Setup

1. Install [JNA](https://github.com/java-native-access/jna) for native Windows API access:
   ```
   mvn dependency:get -Dartifact=net.java.dev.jna:jna-platform:5.12.1
   ```
2. For JNLP application launching, ensure you have Java Web Start installed or use [IcedTea-Web](https://github.com/AdoptOpenJDK/IcedTea-Web)

## macOS Setup

### Step 1: Install Java JDK

1. Using Homebrew (recommended):
   ```
   brew install --cask temurin17
   ```
   Or download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://adoptium.net/)
2. Set the `JAVA_HOME` environment variable:
   ```
   echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
   echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.zshrc
   source ~/.zshrc
   ```
3. Verify installation:
   ```
   java -version
   ```

### Step 2: Install Maven

1. Using Homebrew:
   ```
   brew install maven
   ```
2. Verify installation:
   ```
   mvn -version
   ```

### Step 3: Clone and Build the Framework

1. Open Terminal
2. Clone the repository:
   ```
   git clone https://github.com/diveshpuri/SwingAutomation.git
   cd SwingAutomation
   ```
3. Build the project:
   ```
   mvn clean install
   ```

### Step 4: macOS-Specific Setup

1. Grant screen recording permissions:
   - Go to System Preferences > Security & Privacy > Privacy > Screen Recording
   - Add your Java/Terminal application to the list
2. For JNLP application launching, install [OpenWebStart](https://openwebstart.com/)
3. For keyboard control, grant Accessibility permissions:
   - Go to System Preferences > Security & Privacy > Privacy > Accessibility
   - Add your Java/Terminal application to the list

## Linux Setup

### Step 1: Install Java JDK

1. For Ubuntu/Debian:
   ```
   sudo apt update
   sudo apt install openjdk-17-jdk
   ```
   For Fedora/RHEL:
   ```
   sudo dnf install java-17-openjdk-devel
   ```
2. Set the `JAVA_HOME` environment variable:
   ```
   echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
   echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
   source ~/.bashrc
   ```
   Note: The exact path may vary based on your distribution
3. Verify installation:
   ```
   java -version
   ```

### Step 2: Install Maven

1. For Ubuntu/Debian:
   ```
   sudo apt install maven
   ```
   For Fedora/RHEL:
   ```
   sudo dnf install maven
   ```
2. Verify installation:
   ```
   mvn -version
   ```

### Step 3: Clone and Build the Framework

1. Open Terminal
2. Clone the repository:
   ```
   git clone https://github.com/diveshpuri/SwingAutomation.git
   cd SwingAutomation
   ```
3. Build the project:
   ```
   mvn clean install
   ```

### Step 4: Linux-Specific Setup

1. Install X11 and related packages:
   ```
   # For Ubuntu/Debian
   sudo apt install xorg-dev libxtst-dev
   
   # For Fedora/RHEL
   sudo dnf install libX11-devel libXtst-devel
   ```
2. For screenshot capture, install xdotool:
   ```
   # For Ubuntu/Debian
   sudo apt install xdotool
   
   # For Fedora/RHEL
   sudo dnf install xdotool
   ```
3. For JNLP application launching, install [IcedTea-Web](https://github.com/AdoptOpenJDK/IcedTea-Web):
   ```
   # For Ubuntu/Debian
   sudo apt install icedtea-netx
   
   # For Fedora/RHEL
   sudo dnf install icedtea-web
   ```
4. If running in a headless environment, you'll need a virtual display:
   ```
   sudo apt install xvfb
   ```
   And run your application with:
   ```
   xvfb-run java -cp target/classes YourMainClass
   ```

## Configuration

### LLM Configuration

1. Copy the example configuration file:
   ```
   cp llm.properties.example llm.properties
   ```
2. Edit `llm.properties` with your preferred LLM provider settings:
   ```
   # OpenAI Configuration
   llm.provider=openai
   llm.api.key=your_api_key_here
   llm.model=gpt-4
   
   # Azure OpenAI Configuration
   #llm.provider=azure
   #llm.api.key=your_api_key_here
   #llm.api.endpoint=https://your-resource-name.openai.azure.com/
   #llm.api.deployment=your_deployment_name
   #llm.model=gpt-4
   
   # Anthropic Configuration
   #llm.provider=anthropic
   #llm.api.key=your_api_key_here
   #llm.model=claude-3-opus-20240229
   
   # Custom API Configuration
   #llm.provider=custom
   #llm.api.endpoint=http://your-custom-endpoint/v1/chat/completions
   #llm.api.key=your_api_key_here
   #llm.model=your_model_name
   ```

### Screenshot Directory Configuration

1. Create the necessary directories:
   ```
   mkdir -p screenshots captured_images generated_scripts
   ```
2. For application-specific image libraries:
   ```
   mkdir -p images/app_name
   ```

## Troubleshooting

### Common Issues

#### "Headless environment" Error

**Problem**: `java.awt.AWTException: headless environment`

**Solution**:
- Ensure you're running in a graphical environment
- For servers or CI/CD environments, use Xvfb:
  ```
  xvfb-run java -cp target/classes YourMainClass
  ```

#### Screenshot Capture Not Working

**Problem**: Unable to capture screenshots or access screen

**Solution**:
- Check screen recording/accessibility permissions (macOS)
- Verify X11 is properly installed (Linux)
- Run as administrator (Windows)
- Ensure proper display environment variables are set (Linux):
  ```
  export DISPLAY=:0
  ```

#### JNLP Launcher Issues

**Problem**: Unable to launch JNLP applications

**Solution**:
- Verify Java Web Start or alternative is installed
- Check JNLP URL is accessible
- For security restrictions, add exceptions in Java Control Panel

#### LLM Integration Not Working

**Problem**: LLM API calls failing

**Solution**:
- Verify API keys in `llm.properties`
- Check network connectivity to LLM provider
- Ensure proper model names are specified
- Check for rate limiting or quota issues

### Getting Help

If you encounter issues not covered in this guide:

1. Check the [GitHub Issues](https://github.com/diveshpuri/SwingAutomation/issues) for similar problems
2. Create a new issue with detailed information:
   - Operating system and version
   - Java version (`java -version`)
   - Maven version (`mvn -version`)
   - Complete error message and stack trace
   - Steps to reproduce the issue

## Running Tests

To verify your setup is working correctly:

```
# Basic functionality test
java -cp target/classes AgentTestRunner

# JNLP application test (if applicable)
java -cp target/classes AgentTestRunner jnlp "https://your-app.jnlp" "App Name" "Test description"
```

## Next Steps

After successful setup, refer to the main [README.md](README.md) for usage instructions and examples.
