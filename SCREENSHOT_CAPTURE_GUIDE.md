# Screenshot Capture Tool for Java Swing Automation

## Overview
This comprehensive screenshot capture framework helps you build an image library for Java Swing automation by automatically capturing screenshots during manual desktop actions.

## What's Included

### 1. Core Screenshot Capture Tools
- **`ScreenshotCapture.java`** - GUI-based screenshot capture tool with interactive controls
- **`GlobalScreenshotCapture.java`** - Lightweight command-line screenshot capture utility
- **`capture_script.sh`** - Easy-to-use shell script wrapper for screenshot operations

### 2. Integration with Automation Framework
- **`SwingImageAutomation.java`** - Pure Java automation framework using captured images
- **`SikuliExample.java`** - Advanced automation using Sikuli library
- **`build.sh`** - Build script that sets up the complete project structure

### 3. Documentation and Setup
- **`README_SwingAutomation.md`** - Comprehensive automation framework documentation
- **`SCREENSHOT_CAPTURE_GUIDE.md`** - This guide for screenshot capture workflow

## Quick Start Guide

### Step 1: Setup on Windows
1. Copy all files to your Windows machine
2. Ensure Java 17+ is installed
3. Run the build script:
   ```bash
   ./build.sh
   ```

### Step 2: Choose Your Capture Method

#### Option A: Interactive GUI Tool (Recommended for Beginners)
```bash
java -cp build ScreenshotCapture
```
- User-friendly interface with buttons and options
- Configure capture triggers (mouse clicks, key presses)
- Set output directory and capture delays
- Real-time status updates

#### Option B: Command-Line Tool (Recommended for Power Users)
```bash
# Interactive mode
./capture_script.sh interactive

# Quick capture
./capture_script.sh capture

# Delayed capture (3 seconds)
./capture_script.sh delayed
```

#### Option C: Direct Java Commands
```bash
# Immediate screenshot
java -cp build GlobalScreenshotCapture capture

# Delayed screenshot
java -cp build GlobalScreenshotCapture delayed
```

### Step 3: Capture Your Swing Application Images

1. **Start your Java Swing application**
2. **Launch the screenshot capture tool**
3. **Perform manual actions** on your Swing application:
   - Click buttons
   - Open menus
   - Fill forms
   - Navigate dialogs
4. **Screenshots are automatically saved** to `captured_images/` directory

### Step 4: Organize Captured Images

Screenshots are automatically named with:
- Sequential numbers (001, 002, etc.)
- Timestamps (yyyyMMdd_HHmmss)
- Action types (click, key, manual, delayed)

Example filenames:
- `screenshot_001_20250724_153045_click_250_150.png`
- `screenshot_002_20250724_153102_manual.png`
- `screenshot_003_20250724_153120_delayed.png`

### Step 5: Use Images in Automation

Copy the captured images to your automation project's `images/` directory and reference them in your automation scripts:

```java
// Using the pure Java framework
SwingImageAutomation automation = new SwingImageAutomation();
automation.clickImage("images/login_button.png");
automation.typeText("username");
automation.clickImage("images/submit_button.png");

// Using Sikuli framework
Screen screen = new Screen();
screen.click("images/login_button.png");
screen.type("username");
screen.click("images/submit_button.png");
```

## Advanced Features

### Hotkeys (Global Screenshot Capture)
- **F12** - Capture screenshot immediately
- **Ctrl+Shift+F12** - Capture with 2-second delay
- **Ctrl+Alt+Q** - Quit capture application

### Capture Configuration Options
- **Capture Triggers**: Mouse clicks, key presses, or manual
- **Capture Delay**: Configurable delay before screenshot (0-5000ms)
- **Output Directory**: Customizable save location
- **File Naming**: Automatic timestamp and action-based naming

### Image Quality Tips
1. **High Resolution**: Use high-resolution displays for better image quality
2. **Consistent Environment**: Capture in the same environment where automation will run
3. **Minimal Context**: Focus on specific UI elements rather than full screen
4. **Multiple States**: Capture different states (normal, hover, pressed) of interactive elements

## Troubleshooting

### Common Issues

#### "Headless Environment" Error
- **Cause**: Running on a system without GUI display
- **Solution**: Run on Windows with proper display environment

#### Images Not Captured
- **Check**: Output directory permissions
- **Check**: Java Robot class permissions
- **Check**: Display environment is available

#### Poor Image Matching
- **Solution**: Capture images at same resolution as automation environment
- **Solution**: Ensure consistent UI themes and scaling
- **Solution**: Crop images to focus on specific elements

### Performance Optimization
- Use **regions** to limit search areas in automation scripts
- Set appropriate **similarity thresholds** for image matching
- Implement **timeouts** for image waiting operations

## Integration Workflow

### Complete Automation Development Process

1. **Capture Phase**:
   - Use screenshot tools to capture UI elements
   - Organize images by functionality (login, navigation, forms, etc.)
   - Name images descriptively (login_button.png, username_field.png)

2. **Development Phase**:
   - Use captured images in automation framework
   - Test image recognition accuracy
   - Adjust similarity thresholds as needed

3. **Testing Phase**:
   - Run automation scripts with captured images
   - Verify reliable element detection
   - Handle edge cases and error conditions

4. **Maintenance Phase**:
   - Update images when UI changes
   - Add new images for new features
   - Optimize image library for performance

## File Structure

```
project/
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
└── README_SwingAutomation.md      # Automation documentation
```

## Next Steps

1. **Copy this framework to your Windows machine**
2. **Test screenshot capture with your Swing application**
3. **Build your image library using the capture tools**
4. **Develop automation scripts using the captured images**
5. **Iterate and refine your automation workflows**

This framework provides everything you need to build robust Java Swing automation using image recognition, starting with efficient screenshot capture and ending with reliable automation execution.
