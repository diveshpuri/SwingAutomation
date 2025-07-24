import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
        startStopButton = new JButton("Start Capturing");
        startStopButton.addActionListener(e -> toggleCapturing());
        controlPanel.add(startStopButton, gbc);
        
        gbc.gridy = 5;
        statusLabel = new JLabel("Ready to capture screenshots");
        statusLabel.setForeground(Color.BLUE);
        controlPanel.add(statusLabel, gbc);
        
        add(controlPanel, BorderLayout.CENTER);
        
        JTextArea instructions = new JTextArea(
            "Instructions:\n" +
            "1. Set output directory for captured images\n" +
            "2. Choose capture triggers (mouse clicks and/or key presses)\n" +
            "3. Click 'Start Capturing'\n" +
            "4. Perform actions on your Swing application\n" +
            "5. Screenshots will be automatically saved\n" +
            "6. Use Ctrl+Shift+S to manually capture current screen\n" +
            "7. Use Ctrl+Shift+Q to stop capturing\n\n" +
            "Tips:\n" +
            "- Captured images will be named with timestamp and action type\n" +
            "- Use these images as templates in your automation scripts\n" +
            "- Crop images to focus on specific UI elements after capture"
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
                        Timer timer = new Timer((Integer) delaySpinner.getValue(), e -> {
                            captureScreenshot("click_" + mouseEvent.getX() + "_" + mouseEvent.getY());
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
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenshot = robot.createScreenCapture(screenRect);
            
            String timestamp = dateFormat.format(new Date());
            String filename = String.format("screenshot_%03d_%s_%s.png", 
                screenshotCounter++, timestamp, actionType);
            
            File outputFile = new File(outputDirectory, filename);
            ImageIO.write(screenshot, "PNG", outputFile);
            
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText("Captured: " + filename + " (Total: " + (screenshotCounter - 1) + ")");
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
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if (isCapturing && captureOnClickBox.isSelected()) {
            captureScreenshot("click");
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
