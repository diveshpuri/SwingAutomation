import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Java Swing Automation using Image Recognition
 * This example demonstrates how to automate Swing applications using screenshots and image matching
 */
public class SwingImageAutomation {
    
    private Robot robot;
    private static final int CLICK_DELAY = 500;
    private static final int TYPE_DELAY = 100;
    
    public SwingImageAutomation() throws AWTException {
        this.robot = new Robot();
        this.robot.setAutoDelay(CLICK_DELAY);
    }
    
    /**
     * Capture screenshot of the entire screen
     */
    public BufferedImage captureScreen() {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        return robot.createScreenCapture(screenRect);
    }
    
    /**
     * Find an image template within the screen capture
     * Returns the center point of the found image, or null if not found
     */
    public Point findImageOnScreen(String templateImagePath) throws IOException {
        BufferedImage screen = captureScreen();
        BufferedImage template = ImageIO.read(new File(templateImagePath));
        
        Point location = findImageInImage(screen, template);
        if (location != null) {
            return new Point(
                location.x + template.getWidth() / 2,
                location.y + template.getHeight() / 2
            );
        }
        return null;
    }
    
    /**
     * Find an image template within a provided screenshot
     * Returns the center point of the found image, or null if not found
     */
    public Point findImageLocation(BufferedImage screenshot, BufferedImage template) {
        Point location = findImageInImage(screenshot, template);
        if (location != null) {
            return new Point(
                location.x + template.getWidth() / 2,
                location.y + template.getHeight() / 2
            );
        }
        return null;
    }
    
    /**
     * Simple template matching algorithm
     * Returns top-left corner of best match, or null if no good match found
     */
    public Point findImageInImage(BufferedImage source, BufferedImage template) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();
        int templateWidth = template.getWidth();
        int templateHeight = template.getHeight();
        
        double bestMatch = Double.MAX_VALUE;
        Point bestLocation = null;
        double threshold = 0.1; // Adjust based on matching sensitivity needed
        
        for (int x = 0; x <= sourceWidth - templateWidth; x++) {
            for (int y = 0; y <= sourceHeight - templateHeight; y++) {
                double difference = calculateImageDifference(source, template, x, y);
                
                if (difference < bestMatch && difference < threshold) {
                    bestMatch = difference;
                    bestLocation = new Point(x, y);
                }
            }
        }
        
        return bestLocation;
    }
    
    /**
     * Calculate normalized difference between template and source region
     */
    private double calculateImageDifference(BufferedImage source, BufferedImage template, int offsetX, int offsetY) {
        long totalDifference = 0;
        int pixelCount = template.getWidth() * template.getHeight();
        
        for (int x = 0; x < template.getWidth(); x++) {
            for (int y = 0; y < template.getHeight(); y++) {
                int sourceRGB = source.getRGB(offsetX + x, offsetY + y);
                int templateRGB = template.getRGB(x, y);
                
                int rDiff = Math.abs(((sourceRGB >> 16) & 0xFF) - ((templateRGB >> 16) & 0xFF));
                int gDiff = Math.abs(((sourceRGB >> 8) & 0xFF) - ((templateRGB >> 8) & 0xFF));
                int bDiff = Math.abs((sourceRGB & 0xFF) - (templateRGB & 0xFF));
                
                totalDifference += rDiff + gDiff + bDiff;
            }
        }
        
        return (double) totalDifference / (pixelCount * 255 * 3);
    }
    
    /**
     * Click on an image if found on screen
     */
    public boolean clickImage(String imagePath) throws IOException {
        Point location = findImageOnScreen(imagePath);
        if (location != null) {
            robot.mouseMove(location.x, location.y);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            return true;
        }
        return false;
    }
    
    /**
     * Click at specific coordinates
     */
    public void clickAt(int x, int y) {
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
    
    /**
     * Press a key
     */
    public void pressKey(int keyCode) {
        robot.keyPress(keyCode);
        robot.keyRelease(keyCode);
    }
    
    /**
     * Get the Robot instance for advanced operations
     */
    public Robot getRobot() {
        return robot;
    }
    
    /**
     * Type text at current cursor position
     */
    public void typeText(String text) {
        for (char c : text.toCharArray()) {
            typeCharacter(c);
            robot.delay(TYPE_DELAY);
        }
    }
    
    /**
     * Type a single character
     */
    private void typeCharacter(char c) {
        if (Character.isUpperCase(c)) {
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(Character.toUpperCase(c));
            robot.keyRelease(Character.toUpperCase(c));
            robot.keyRelease(KeyEvent.VK_SHIFT);
        } else {
            robot.keyPress(Character.toUpperCase(c));
            robot.keyRelease(Character.toUpperCase(c));
        }
    }
    
    /**
     * Wait for an image to appear on screen (with timeout)
     */
    public boolean waitForImage(String imagePath, int timeoutSeconds) throws IOException {
        long startTime = System.currentTimeMillis();
        long timeout = timeoutSeconds * 1000;
        
        while (System.currentTimeMillis() - startTime < timeout) {
            if (findImageOnScreen(imagePath) != null) {
                return true;
            }
            robot.delay(1000); // Check every second
        }
        return false;
    }
    
    /**
     * Example automation workflow
     */
    public void exampleWorkflow() throws IOException {
        System.out.println("Starting Swing application automation...");
        
        if (waitForImage("images/app_loaded.png", 10)) {
            System.out.println("Application detected!");
            
            if (clickImage("images/login_button.png")) {
                System.out.println("Login button clicked");
                
                if (waitForImage("images/username_field.png", 5)) {
                    clickImage("images/username_field.png");
                    typeText("myusername");
                    
                    clickImage("images/password_field.png");
                    typeText("mypassword");
                    
                    clickImage("images/submit_button.png");
                    
                    System.out.println("Login completed");
                }
            }
        } else {
            System.out.println("Application not found within timeout");
        }
    }
    
    public static void main(String[] args) {
        try {
            SwingImageAutomation automation = new SwingImageAutomation();
            automation.exampleWorkflow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
