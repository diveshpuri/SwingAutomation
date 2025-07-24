import java.awt.Desktop;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * JNLP Application Launcher for Java Swing applications
 * Handles launching desktop applications from JNLP links
 */
public class JNLPLauncher {
    
    private String jnlpUrl;
    private String applicationName;
    private Process launchedProcess;
    private boolean isLaunched = false;
    
    public JNLPLauncher(String jnlpUrl, String applicationName) {
        this.jnlpUrl = jnlpUrl;
        this.applicationName = applicationName;
    }
    
    /**
     * Launch the JNLP application
     */
    public boolean launchApplication() {
        try {
            System.out.println("Launching JNLP application: " + applicationName);
            System.out.println("JNLP URL: " + jnlpUrl);
            
            if (launchWithJavaWS()) {
                isLaunched = true;
                return true;
            }
            
            if (launchWithDownloadedJNLP()) {
                isLaunched = true;
                return true;
            }
            
            if (launchWithBrowser()) {
                isLaunched = true;
                return true;
            }
            
            System.err.println("Failed to launch JNLP application using all methods");
            return false;
            
        } catch (Exception e) {
            System.err.println("Error launching JNLP application: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Launch using javaws command directly
     */
    private boolean launchWithJavaWS() {
        try {
            ProcessBuilder pb = new ProcessBuilder("javaws", jnlpUrl);
            pb.redirectErrorStream(true);
            launchedProcess = pb.start();
            
            Thread.sleep(3000);
            
            if (launchedProcess.isAlive()) {
                System.out.println("Successfully launched with javaws");
                return true;
            }
            
        } catch (Exception e) {
            System.out.println("javaws launch failed: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Download JNLP file and launch it
     */
    private boolean launchWithDownloadedJNLP() {
        try {
            URL url = new URL(jnlpUrl);
            Path tempJnlp = Files.createTempFile("app", ".jnlp");
            
            try (InputStream in = url.openStream()) {
                Files.copy(in, tempJnlp, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
            
            ProcessBuilder pb = new ProcessBuilder("javaws", tempJnlp.toString());
            pb.redirectErrorStream(true);
            launchedProcess = pb.start();
            
            Thread.sleep(3000);
            
            if (launchedProcess.isAlive()) {
                System.out.println("Successfully launched with downloaded JNLP");
                return true;
            }
            
            Files.deleteIfExists(tempJnlp);
            
        } catch (Exception e) {
            System.out.println("Downloaded JNLP launch failed: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Launch using system default browser
     */
    private boolean launchWithBrowser() {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(new URI(jnlpUrl));
                    System.out.println("Opened JNLP URL in browser");
                    
                    Thread.sleep(5000);
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Browser launch failed: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Wait for application to be ready
     */
    public boolean waitForApplicationReady(int timeoutSeconds) {
        System.out.println("Waiting for application to be ready...");
        
        for (int i = 0; i < timeoutSeconds; i++) {
            try {
                Thread.sleep(1000);
                
                if (isApplicationWindowAvailable()) {
                    System.out.println("Application is ready!");
                    return true;
                }
                
                System.out.print(".");
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        
        System.out.println("\nTimeout waiting for application to be ready");
        return false;
    }
    
    /**
     * Check if application window is available
     */
    private boolean isApplicationWindowAvailable() {
        try {
            String activeWindow = getActiveWindowTitle();
            return activeWindow != null && 
                   (activeWindow.toLowerCase().contains(applicationName.toLowerCase()) ||
                    applicationName.toLowerCase().contains(activeWindow.toLowerCase()));
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get active window title (reused from ScreenshotCapture)
     */
    private String getActiveWindowTitle() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            Process process;
            
            if (os.contains("win")) {
                process = Runtime.getRuntime().exec("powershell.exe -Command \"Add-Type -AssemblyName System.Windows.Forms; [System.Windows.Forms.SystemInformation]::ComputerName; (Get-Process | Where-Object {$_.MainWindowTitle -ne ''} | Select-Object -First 1).MainWindowTitle\"");
            } else if (os.contains("mac")) {
                process = Runtime.getRuntime().exec("osascript -e 'tell application \"System Events\" to get name of first application process whose frontmost is true'");
            } else {
                process = Runtime.getRuntime().exec("xdotool getactivewindow getwindowname");
            }
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                return line != null ? line.trim() : null;
            }
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Close the launched application
     */
    public void closeApplication() {
        if (launchedProcess != null && launchedProcess.isAlive()) {
            launchedProcess.destroy();
            try {
                launchedProcess.waitFor(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                launchedProcess.destroyForcibly();
            }
        }
        isLaunched = false;
    }
    
    /**
     * Check if application is currently launched
     */
    public boolean isLaunched() {
        return isLaunched && (launchedProcess == null || launchedProcess.isAlive());
    }
    
    /**
     * Get application name
     */
    public String getApplicationName() {
        return applicationName;
    }
    
    /**
     * Get JNLP URL
     */
    public String getJnlpUrl() {
        return jnlpUrl;
    }
    
    /**
     * Test JNLP launcher with example
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java JNLPLauncher <jnlp_url> <application_name>");
            System.out.println("Example: java JNLPLauncher https://example.com/app.jnlp \"My Application\"");
            return;
        }
        
        String jnlpUrl = args[0];
        String appName = args[1];
        
        JNLPLauncher launcher = new JNLPLauncher(jnlpUrl, appName);
        
        if (launcher.launchApplication()) {
            System.out.println("Application launched successfully");
            
            if (launcher.waitForApplicationReady(30)) {
                System.out.println("Application is ready for automation");
            } else {
                System.out.println("Application may not be fully ready");
            }
            
            System.out.println("Press Enter to close application...");
            try {
                System.in.read();
            } catch (Exception e) {
            }
            
            launcher.closeApplication();
        } else {
            System.err.println("Failed to launch application");
        }
    }
}
