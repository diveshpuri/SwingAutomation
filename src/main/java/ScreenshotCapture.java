import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.UIManager;

/**
 * Screenshot Capture Tool for Building Automation Image Library
 * Captures screenshots on mouse clicks and key presses to build image templates
 */
public class ScreenshotCapture extends JFrame implements MouseListener, KeyListener {
    
    private Robot robot;
    private boolean isCapturing = false;
    private String outputDirectory = "captured_images";
    private int screenshotCounter = 1;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    
    private JButton startStopButton;
    private JLabel statusLabel;
    private JTextField directoryField;
    private JCheckBox captureOnClickBox;
    private JCheckBox captureOnKeyBox;
    private JSpinner delaySpinner;
    private JCheckBox bindToAppCheckbox;
    private JTextField appNameField;
    private String targetAppName = "";
    private JCheckBox highlightActionBox;
    private boolean showActionHighlight = true;
    private JWindow overlayWindow;
    
    public ScreenshotCapture() throws AWTException {
        this.robot = new Robot();
        initializeUI();
        setupGlobalListeners();
        createOutputDirectory();
    }
    
    private void initializeUI() {
        setTitle("Screenshot Capture Tool - Image Library Builder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        controlPanel.add(new JLabel("Output Directory:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        directoryField = new JTextField(outputDirectory, 20);
        controlPanel.add(directoryField, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(e -> browseDirectory());
        controlPanel.add(browseButton, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        captureOnClickBox = new JCheckBox("Capture on Mouse Click", true);
        controlPanel.add(captureOnClickBox, gbc);
        
        gbc.gridy = 2;
        captureOnKeyBox = new JCheckBox("Capture on Key Press", false);
        controlPanel.add(captureOnKeyBox, gbc);
        
        gbc.gridy = 3; gbc.gridwidth = 1;
        controlPanel.add(new JLabel("Capture Delay (ms):"), gbc);
        
        gbc.gridx = 1;
        delaySpinner = new JSpinner(new SpinnerNumberModel(500, 0, 5000, 100));
        controlPanel.add(delaySpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        bindToAppCheckbox = new JCheckBox("Bind to specific application", false);
        controlPanel.add(bindToAppCheckbox, gbc);
        
        gbc.gridy = 5; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        controlPanel.add(new JLabel("App Name:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        appNameField = new JTextField(20);
        appNameField.setToolTipText("Enter application window title (partial match)");
        appNameField.setEnabled(false);
        controlPanel.add(appNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        highlightActionBox = new JCheckBox("Highlight action location with red box", true);
        highlightActionBox.setToolTipText("Shows red transparent box at click/action location in screenshots");
        controlPanel.add(highlightActionBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        startStopButton = new JButton("Start Capturing");
        startStopButton.addActionListener(e -> toggleCapturing());
        controlPanel.add(startStopButton, gbc);
        
        gbc.gridy = 8;
        statusLabel = new JLabel("Ready to capture screenshots");
        statusLabel.setForeground(Color.BLUE);
        controlPanel.add(statusLabel, gbc);
        
        bindToAppCheckbox.addActionListener(e -> {
            boolean enabled = bindToAppCheckbox.isSelected();
            appNameField.setEnabled(enabled);
            if (enabled) {
                targetAppName = appNameField.getText().trim();
            } else {
                targetAppName = "";
            }
        });
        
        appNameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (bindToAppCheckbox.isSelected()) {
                    targetAppName = appNameField.getText().trim();
                }
            }
        });
        
        highlightActionBox.addActionListener(e -> {
            showActionHighlight = highlightActionBox.isSelected();
        });
        
        add(controlPanel, BorderLayout.CENTER);
        
        JTextArea instructions = new JTextArea(
            "Instructions:\n" +
            "1. Set output directory for captured images\n" +
            "2. Choose capture triggers (mouse clicks and/or key presses)\n" +
            "3. Optional: Enable application binding and enter app name\n" +
            "4. Click 'Start Capturing'\n" +
            "5. Perform actions on your target application\n" +
            "6. Screenshots will be automatically saved\n" +
            "7. Use Ctrl+Shift+S to manually capture current screen\n" +
            "8. Use Ctrl+Shift+Q to stop capturing\n\n" +
            "Application Binding:\n" +
            "- Enter partial window title (e.g., 'Calculator', 'Notepad')\n" +
            "- Screenshots only captured when target app is active\n" +
            "- Images prefixed with application name for organization\n\n" +
            "Action Highlighting:\n" +
            "- Red transparent box shows exact click/action location\n" +
            "- Helps automation framework identify action coordinates\n" +
            "- Can be disabled if not needed for your workflow\n\n" +
            "Tips:\n" +
            "- Captured images will be named with timestamp and action type\n" +
            "- Use these images as templates in your automation scripts\n" +
            "- Red highlights show precise action locations for automation"
        );
        instructions.setEditable(false);
        instructions.setBackground(getBackground());
        instructions.setBorder(BorderFactory.createTitledBorder("How to Use"));
        add(instructions, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void setupGlobalListeners() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if (e.isControlDown() && e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_S) {
                        captureScreenshot("manual");
                        return true;
                    }
                    if (e.isControlDown() && e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_Q) {
                        if (isCapturing) {
                            toggleCapturing();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }
    
    private void browseDirectory() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setCurrentDirectory(new File(outputDirectory));
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            outputDirectory = chooser.getSelectedFile().getAbsolutePath();
            directoryField.setText(outputDirectory);
            createOutputDirectory();
        }
    }
    
    private void createOutputDirectory() {
        File dir = new File(outputDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    private void toggleCapturing() {
        isCapturing = !isCapturing;
        
        if (isCapturing) {
            outputDirectory = directoryField.getText();
            createOutputDirectory();
            startStopButton.setText("Stop Capturing");
            statusLabel.setText("Capturing screenshots... Press Ctrl+Shift+Q to stop");
            statusLabel.setForeground(Color.RED);
            
            startGlobalMouseListener();
            
        } else {
            startStopButton.setText("Start Capturing");
            statusLabel.setText("Capture stopped. " + (screenshotCounter - 1) + " screenshots saved.");
            statusLabel.setForeground(Color.BLUE);
            
            stopGlobalMouseListener();
        }
    }
    
    private void startGlobalMouseListener() {
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                if (isCapturing && event instanceof MouseEvent) {
                    MouseEvent mouseEvent = (MouseEvent) event;
                    if (mouseEvent.getID() == MouseEvent.MOUSE_CLICKED && captureOnClickBox.isSelected()) {
                        Point clickLocation = new Point(mouseEvent.getXOnScreen(), mouseEvent.getYOnScreen());
                        Timer timer = new Timer((Integer) delaySpinner.getValue(), e -> {
                            captureScreenshotWithHighlight("click_" + mouseEvent.getX() + "_" + mouseEvent.getY(), clickLocation);
                        });
                        timer.setRepeats(false);
                        timer.start();
                    }
                }
            }
        }, AWTEvent.MOUSE_EVENT_MASK);
    }
    
    private void stopGlobalMouseListener() {
    }
    
    private void captureScreenshot(String actionType) {
        try {
            if (bindToAppCheckbox.isSelected() && !isTargetApplicationActive()) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Waiting for target application: " + targetAppName);
                });
                return;
            }
            
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenshot = robot.createScreenCapture(screenRect);
            
            String timestamp = dateFormat.format(new Date());
            String appPrefix = targetAppName.isEmpty() ? "" : sanitizeAppName(targetAppName) + "_";
            String filename = String.format("%sscreenshot_%03d_%s_%s.png", 
                appPrefix, screenshotCounter++, timestamp, actionType);
            
            File outputFile = new File(outputDirectory, filename);
            ImageIO.write(screenshot, "PNG", outputFile);
            
            SwingUtilities.invokeLater(() -> {
                String statusMsg = targetAppName.isEmpty() ? 
                    "Captured: " + filename + " (Total: " + (screenshotCounter - 1) + ")" :
                    "Captured for " + targetAppName + ": " + filename + " (Total: " + (screenshotCounter - 1) + ")";
                statusLabel.setText(statusMsg);
            });
            
            System.out.println("Screenshot saved: " + outputFile.getAbsolutePath());
            
        } catch (IOException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText("Error capturing screenshot: " + e.getMessage());
                statusLabel.setForeground(Color.RED);
            });
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
    
    private void captureScreenshotWithHighlight(String actionType, Point location) {
        if (showActionHighlight && location != null) {
            showActionHighlight(location);
            Timer highlightTimer = new Timer(200, e -> {
                hideActionHighlight();
                Timer captureTimer = new Timer(100, evt -> captureScreenshot(actionType));
                captureTimer.setRepeats(false);
                captureTimer.start();
            });
            highlightTimer.setRepeats(false);
            highlightTimer.start();
        } else {
            captureScreenshot(actionType);
        }
    }
    
    private void showActionHighlight(Point location) {
        if (overlayWindow != null) {
            hideActionHighlight();
        }
        
        overlayWindow = new JWindow();
        overlayWindow.setAlwaysOnTop(true);
        overlayWindow.setBackground(new Color(0, 0, 0, 0));
        
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(255, 0, 0, 100));
                g2d.setStroke(new BasicStroke(3.0f));
                
                int boxSize = 40;
                int x = boxSize / 2;
                int y = boxSize / 2;
                g2d.drawRect(x - boxSize/2, y - boxSize/2, boxSize, boxSize);
                
                g2d.fillRect(x - boxSize/2, y - boxSize/2, boxSize, boxSize);
                
                g2d.setColor(new Color(255, 0, 0, 200));
                g2d.setStroke(new BasicStroke(2.0f));
                g2d.drawRect(x - boxSize/2, y - boxSize/2, boxSize, boxSize);
                
                g2d.dispose();
            }
        };
        
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(40, 40));
        
        overlayWindow.add(panel);
        overlayWindow.pack();
        overlayWindow.setLocation(location.x - 20, location.y - 20);
        overlayWindow.setVisible(true);
    }
    
    private void hideActionHighlight() {
        if (overlayWindow != null) {
            overlayWindow.setVisible(false);
            overlayWindow.dispose();
            overlayWindow = null;
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if (isCapturing && captureOnClickBox.isSelected()) {
            Point clickLocation = new Point(e.getXOnScreen(), e.getYOnScreen());
            captureScreenshotWithHighlight("click", clickLocation);
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (isCapturing && captureOnKeyBox.isSelected()) {
            captureScreenshot("key_" + KeyEvent.getKeyText(e.getKeyCode()));
        }
    }
    
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            SwingUtilities.invokeLater(() -> {
                try {
                    new ScreenshotCapture().setVisible(true);
                } catch (AWTException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, 
                        "Failed to initialize Robot class: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
