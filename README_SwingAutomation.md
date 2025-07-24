# Java Swing Automation with Image Recognition

This repository contains examples of automating Java Swing applications using image recognition techniques. Since the target applications only run on Windows, this approach allows automation without needing direct access to UI element properties.

## Approaches Included

### 1. Pure Java Implementation (`SwingImageAutomation.java`)
- Uses Java's built-in `Robot` class for mouse/keyboard control
- Implements basic template matching for image recognition
- No external dependencies required
- Good for simple automation tasks

**Pros:**
- No external libraries needed
- Full control over matching algorithm
- Lightweight solution

**Cons:**
- Basic image matching capabilities
- Requires manual implementation of advanced features
- Less robust than specialized libraries

### 2. Sikuli Integration (`SikuliExample.java`)
- Uses Sikuli library for advanced image recognition
- More robust pattern matching
- Built-in debugging features

**Pros:**
- Advanced image recognition capabilities
- Built-in wait functions and error handling
- Excellent debugging tools (automatic screenshots on failure)
- Region-based searching for better performance

**Cons:**
- External dependency required
- Larger footprint
- May require additional setup

## Setup Instructions

### For Pure Java Approach:
1. Compile: `javac SwingImageAutomation.java`
2. Create an `images/` directory
3. Add screenshot images of UI elements you want to interact with
4. Run: `java SwingImageAutomation`

### For Sikuli Approach:
1. Add Sikuli dependency to your project:
   ```xml
   <dependency>
       <groupId>com.sikulix</groupId>
       <artifactId>sikulixapi</artifactId>
       <version>2.0.5</version>
   </dependency>
   ```
2. Create an `images/` directory
3. Add screenshot images of UI elements
4. Compile and run

## Image Preparation Tips

1. **Capture Clear Screenshots**: Use high-resolution screenshots of UI elements
2. **Consistent Environment**: Capture images in the same environment where automation will run
3. **Minimal Context**: Crop images to include just the element you want to find
4. **Handle Variations**: Consider different states (hover, pressed, disabled) of elements
5. **Test Matching**: Verify image matching works reliably before full automation

## Best Practices

1. **Use Regions**: Limit search areas to improve performance and accuracy
2. **Set Appropriate Timeouts**: Allow sufficient time for UI elements to appear
3. **Error Handling**: Always handle cases where images aren't found
4. **Debugging**: Save screenshots when automation fails for troubleshooting
5. **Similarity Thresholds**: Adjust matching sensitivity based on your needs

## Workflow Example

```java
// 1. Wait for application to load
waitForImage("app_loaded.png", 10);

// 2. Navigate through menus
clickImage("file_menu.png");
clickImage("open_option.png");

// 3. Handle dialogs
if (findImageOnScreen("dialog.png") != null) {
    typeText("filename.txt");
    clickImage("ok_button.png");
}

// 4. Perform main actions
clickImage("text_area.png");
typeText("Automated content");

// 5. Save and exit
robot.key(Key.CTRL + "s");
clickImage("save_button.png");
```

## Limitations and Considerations

1. **Screen Resolution**: Images may not match if screen resolution changes
2. **Theme Changes**: UI theme changes can break image matching
3. **Performance**: Image recognition is slower than direct element access
4. **Maintenance**: UI changes require updating screenshot images
5. **Reliability**: Less reliable than element-based automation

## Alternative Libraries

- **OpenCV**: More advanced computer vision capabilities
- **Marvin Framework**: Pure Java image processing
- **Java AWT Robot**: Built-in but basic functionality
- **TestComplete**: Commercial solution with advanced features

## Next Steps

1. Choose the approach that best fits your needs
2. Capture screenshots of your target Swing application's UI elements
3. Adapt the example code to your specific automation requirements
4. Test thoroughly in your target environment
5. Consider hybrid approaches combining multiple techniques
