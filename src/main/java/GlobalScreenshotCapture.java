import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
    private String targetAppName = "";
    
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
            if (!targetAppName.isEmpty() && !isTargetApplicationActive()) {
                System.out.println("Waiting for target application: " + targetAppName);
                return;
            }
            
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenshot = robot.createScreenCapture(screenRect);
            
            String timestamp = dateFormat.format(new Date());
            String appPrefix = targetAppName.isEmpty() ? "" : sanitizeAppName(targetAppName) + "_";
            String filename = String.format("%s%s_%03d_%s.png", 
                appPrefix, prefix, screenshotCounter.getAndIncrement(), timestamp);
            
            File outputFile = new File(outputDirectory, filename);
            ImageIO.write(screenshot, "PNG", outputFile);
            
            String statusMsg = targetAppName.isEmpty() ? 
                "Screenshot saved: " + filename :
                "Screenshot saved for " + targetAppName + ": " + filename;
            System.out.println(statusMsg);
            
        } catch (IOException e) {
            System.err.println("Error capturing screenshot: " + e.getMessage());
        }
    }
    
    private boolean isTargetApplicationActive() {
        if (targetAppName.isEmpty()) {
            return true;
        }
        
        try {
            String activeWindowTitle = getActiveWindowTitle();
            if (activeWindowTitle != null) {
                return activeWindowTitle.toLowerCase().contains(targetAppName.toLowerCase());
            }
        } catch (Exception e) {
            System.err.println("Error checking active window: " + e.getMessage());
        }
        return false;
    }
    
    private String getActiveWindowTitle() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            
            if (os.contains("windows")) {
                Process process = Runtime.getRuntime().exec(new String[]{
                    "powershell.exe", "-Command",
                    "Add-Type -AssemblyName Microsoft.VisualBasic; " +
                    "[Microsoft.VisualBasic.Interaction]::AppActivate((Get-Process | Where-Object {$_.MainWindowTitle -ne ''} | Sort-Object CPU -Descending | Select-Object -First 1).ProcessName); " +
                    "(Get-Process | Where-Object {$_.MainWindowTitle -ne ''} | Sort-Object CPU -Descending | Select-Object -First 1).MainWindowTitle"
                });
                
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
                return reader.readLine();
                
            } else if (os.contains("linux")) {
                Process process = Runtime.getRuntime().exec("xdotool getactivewindow getwindowname");
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
                return reader.readLine();
                
            } else if (os.contains("mac")) {
                Process process = Runtime.getRuntime().exec(new String[]{
                    "osascript", "-e",
                    "tell application \"System Events\" to get name of first application process whose frontmost is true"
                });
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
                return reader.readLine();
            }
        } catch (Exception e) {
            System.err.println("Error getting active window title: " + e.getMessage());
        }
        return null;
    }
    
    private String sanitizeAppName(String appName) {
        return appName.replaceAll("[^a-zA-Z0-9_-]", "_").toLowerCase();
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
            System.out.println("3. Run with argument 'app:<appname>' to bind to specific application");
            System.out.println("4. Run without arguments to start monitoring mode");
            
            if (args.length > 0) {
                String arg = args[0].toLowerCase();
                if (arg.startsWith("app:")) {
                    capture.targetAppName = args[0].substring(4);
                    System.out.println("Binding to application: " + capture.targetAppName);
                    capture.startCapturing();
                } else {
                    switch (arg) {
                        case "capture":
                            capture.captureScreenshot("manual");
                            break;
                        case "delayed":
                            capture.captureWithDelay("delayed", 3000);
                            Thread.sleep(4000);
                            break;
                        default:
                            System.out.println("Unknown argument: " + args[0]);
                            break;
                    }
                }
            } else {
                capture.startCapturing();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
