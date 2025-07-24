# Java Swing Automation Framework with Screenshot Capture

A comprehensive framework for automating Java Swing applications using image recognition and screenshot capture tools.

## 🎯 Overview

This framework solves the challenge of automating Windows-only Java Swing applications by providing:
- **Screenshot capture tools** to build image libraries from manual actions
- **Java automation frameworks** using image recognition
- **Complete documentation** and setup guides
- **Ready-to-use scripts** for immediate deployment

## 🚀 Quick Start

### Prerequisites
- Java 17+ (OpenJDK or Oracle JDK)
- Windows OS with GUI display (for screenshot capture)
- Optional: Sikuli library for advanced features

### Setup
```bash
# Clone the repository
git clone https://github.com/diveshpuri/SwingAutomation.git
cd SwingAutomation

# Build the project
./build.sh
```

### Capture Screenshots
```bash
# Interactive mode (recommended)
./capture_script.sh interactive

# Or use GUI tool
java -cp build ScreenshotCapture

# Quick capture
./capture_script.sh capture
```

### Run Automation
```java
SwingImageAutomation automation = new SwingImageAutomation();
automation.clickImage("captured_images/button.png");
automation.typeText("Hello World");
```

## 📦 What's Included

### Core Screenshot Capture Tools
- **`ScreenshotCapture.java`** - GUI-based capture tool with interactive controls
- **`GlobalScreenshotCapture.java`** - Lightweight CLI capture utility  
- **`capture_script.sh`** - Easy shell script wrapper for screenshot operations

### Automation Framework
- **`SwingImageAutomation.java`** - Pure Java automation using Robot class
- **`SikuliExample.java`** - Advanced automation with Sikuli library
- **`build.sh`** - Project setup and compilation script

### Documentation
- **`README_SwingAutomation.md`** - Comprehensive automation framework documentation
- **`SCREENSHOT_CAPTURE_GUIDE.md`** - Complete guide for screenshot capture workflow
- **`PACKAGE_CONTENTS.md`** - Detailed package overview

## 🎯 Key Features

### Screenshot Capture
- ✅ **Automatic capture** on mouse clicks and key presses
- ✅ **Manual capture** with hotkeys (F12, Ctrl+Shift+F12)
- ✅ **Configurable delays** for UI settling
- ✅ **Organized naming** with timestamps and action types
- ✅ **Multiple interfaces** (GUI, CLI, shell script)

### Automation Framework
- ✅ **Pure Java solution** with no external dependencies
- ✅ **Advanced Sikuli integration** for robust image recognition
- ✅ **Template matching** with configurable similarity thresholds
- ✅ **Error handling** and debugging features
- ✅ **Cross-platform compatibility**

## 📋 Usage Examples

### Building Image Library
```bash
# Start interactive capture
./capture_script.sh interactive

# Perform actions on your Swing app:
# - Click buttons → Auto-captured as "click_X_Y.png"
# - Press keys → Auto-captured as "key_ENTER.png"  
# - Manual capture → Saved as "manual.png"
```

### Automation Workflow
```java
// Login workflow example
automation.waitForImage("images/login_dialog.png", 10);
automation.clickImage("images/username_field.png");
automation.typeText("myusername");
automation.clickImage("images/password_field.png");
automation.typeText("mypassword");
automation.clickImage("images/login_button.png");
```

## 📁 Project Structure

```
SwingAutomation/
├── build/                          # Compiled Java classes
├── captured_images/                # Screenshots from capture tools
├── images/                         # Organized automation templates
├── lib/                           # External libraries (Sikuli, etc.)
├── ScreenshotCapture.java         # GUI capture tool
├── GlobalScreenshotCapture.java   # CLI capture tool
├── SwingImageAutomation.java      # Pure Java automation
├── SikuliExample.java             # Sikuli automation
├── capture_script.sh              # Capture script wrapper
├── build.sh                       # Project build script
└── README.md                      # This file
```

## 🔧 Technical Details

### Supported Actions
- Mouse clicks and movements
- Keyboard input and shortcuts
- Image recognition and template matching
- Window and dialog handling
- Form filling and navigation

### File Formats
- **Screenshots**: PNG format with high quality
- **Templates**: PNG images for automation
- **Scripts**: Java source code and shell scripts

## 📖 Documentation

For detailed guides and documentation:
- **[Screenshot Capture Guide](SCREENSHOT_CAPTURE_GUIDE.md)** - Complete workflow for building image libraries
- **[Automation Framework Documentation](README_SwingAutomation.md)** - Comprehensive automation guide
- **[Package Contents](PACKAGE_CONTENTS.md)** - Detailed overview of all components

## 🎯 Solving the Windows Challenge

**Challenge**: Create Java Swing automation script but need Windows access for element capture

**Solution**: 
1. ✅ **Image recognition approach** - No need for direct element access
2. ✅ **Screenshot capture tools** - Build image library efficiently  
3. ✅ **Cross-platform development** - Develop on any OS, run on Windows
4. ✅ **Complete framework** - Ready-to-use automation solution

## 🏆 Success Metrics

This framework delivers:
- ✅ **Functional screenshot capture** with multiple interfaces
- ✅ **Automated image organization** with smart naming
- ✅ **Complete automation framework** ready for production
- ✅ **Comprehensive documentation** for immediate use
- ✅ **Solved Windows limitation** through image recognition approach

## 📞 Getting Started

1. **Clone this repository**
2. **Run `./build.sh` to set up the project**
3. **Use screenshot capture tools with your Swing application**
4. **Build your automation scripts using captured images**
5. **Deploy and test your automation workflows**

## 📄 License

This project is open source and available under the MIT License.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
