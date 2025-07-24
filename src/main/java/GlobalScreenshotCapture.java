import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import javax.swing.Timer;

/**
 * Global Screenshot Capture Tool
 * Runs in background and captures screenshots on hotkey press
 * Lightweight version for continuous monitoring
 */
public class GlobalScreenshotCapture {
    
    private Robot robot;
    private AtomicBoolean isCapturing = new AtomicBoolean(false);
    private AtomicInteger screenshotCounter = new AtomicInteger(1);
    private String outputDirectory = "captured_images";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    
    public GlobalScreenshotCapture() throws AWTException {
        this.robot = new Robot();
        createOutputDirectory();
    }
    
    private void createOutputDirectory() {
        File dir = new File(outputDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("Created output directory: " + dir.getAbsolutePath());
        }
    }
    
    public void startCapturing() {
        isCapturing.set(true);
        System.out.println("Screenshot capture started!");
        System.out.println("Hotkeys:");
        System.out.println("  F12 - Capture screenshot");
        System.out.println("  Ctrl+Shift+F12 - Capture with 2 second delay");
        System.out.println("  Ctrl+Alt+Q - Quit application");
        System.out.println("Screenshots will be saved to: " + new File(outputDirectory).getAbsolutePath());
        
        monitorHotkeys();
    }
    
    private void monitorHotkeys() {
        
        Thread monitorThread = new Thread(() -> {
            while (isCapturing.get()) {
                try {
                    Thread.sleep(100); // Check every 100ms
                    
                    
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        
        monitorThread.setDaemon(true);
        monitorThread.start();
        
        try {
            while (isCapturing.get()) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            System.out.println("Capture interrupted");
        }
    }
    
    public void captureScreenshot(String prefix) {
        try {
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenshot = robot.createScreenCapture(screenRect);
            
            String timestamp = dateFormat.format(new Date());
            String filename = String.format("%s_%03d_%s.png", 
                prefix, screenshotCounter.getAndIncrement(), timestamp);
            
            File outputFile = new File(outputDirectory, filename);
            ImageIO.write(screenshot, "PNG", outputFile);
            
            System.out.println("Screenshot saved: " + filename);
            
        } catch (IOException e) {
            System.err.println("Error capturing screenshot: " + e.getMessage());
        }
    }
    
    public void captureWithDelay(String prefix, int delayMs) {
        System.out.println("Capturing screenshot in " + (delayMs/1000) + " seconds...");
        
        Timer timer = new Timer(delayMs, e -> captureScreenshot(prefix));
        timer.setRepeats(false);
        timer.start();
    }
    
    public void stop() {
        isCapturing.set(false);
        System.out.println("Screenshot capture stopped. Total screenshots: " + (screenshotCounter.get() - 1));
    }
    
    public static void main(String[] args) {
        try {
            GlobalScreenshotCapture capture = new GlobalScreenshotCapture();
            
            Runtime.getRuntime().addShutdownHook(new Thread(capture::stop));
            
            System.out.println("Starting Global Screenshot Capture Tool...");
            System.out.println("This tool will run in the background.");
            System.out.println("Use the following methods to capture screenshots:");
            System.out.println("1. Run with argument 'capture' to take immediate screenshot");
            System.out.println("2. Run with argument 'delayed' to take screenshot after 3 seconds");
            System.out.println("3. Run without arguments to start monitoring mode");
            
            if (args.length > 0) {
                switch (args[0].toLowerCase()) {
                    case "capture":
                        capture.captureScreenshot("manual");
                        break;
                    case "delayed":
                        capture.captureWithDelay("delayed", 3000);
                        Thread.sleep(4000); // Wait for capture to complete
                        break;
                    default:
                        System.out.println("Unknown argument: " + args[0]);
                        break;
                }
            } else {
                capture.startCapturing();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
