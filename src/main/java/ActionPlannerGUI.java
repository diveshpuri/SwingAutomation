import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

/**
 * GUI for the Swing Automation Agent
 * 
 * Provides a user-friendly interface for creating and executing
 * automation action plans based on human descriptions.
 */
public class ActionPlannerGUI extends JFrame {
    
    private SwingAutomationAgent agent;
    private JTextArea descriptionArea;
    private JTextField appNameField;
    private JTextArea actionPlanArea;
    private JTextArea executionLogArea;
    private JButton parseButton;
    private JButton executeButton;
    private JButton clearButton;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    
    public ActionPlannerGUI() throws AWTException {
        this.agent = new SwingAutomationAgent();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Swing Automation Agent - Action Planner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        JPanel inputPanel = createInputPanel();
        JPanel planPanel = createPlanPanel();
        JPanel logPanel = createLogPanel();
        JPanel controlPanel = createControlPanel();
        
        JSplitPane topSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inputPanel, planPanel);
        topSplit.setDividerLocation(400);
        
        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplit, logPanel);
        mainSplit.setDividerLocation(300);
        
        add(mainSplit, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        addExampleText();
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Human Action Description"));
        
        JPanel appPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        appPanel.add(new JLabel("Target Application:"));
        appNameField = new JTextField("Calculator", 15);
        appPanel.add(appNameField);
        
        descriptionArea = new JTextArea(10, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        
        panel.add(appPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPlanPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Parsed Action Plan"));
        
        actionPlanArea = new JTextArea(10, 30);
        actionPlanArea.setEditable(false);
        actionPlanArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        actionPlanArea.setBackground(new Color(248, 248, 248));
        
        JScrollPane scrollPane = new JScrollPane(actionPlanArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Execution Log"));
        
        executionLogArea = new JTextArea(8, 50);
        executionLogArea.setEditable(false);
        executionLogArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        executionLogArea.setBackground(Color.BLACK);
        executionLogArea.setForeground(Color.GREEN);
        
        JScrollPane scrollPane = new JScrollPane(executionLogArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        parseButton = new JButton("Parse Description");
        parseButton.addActionListener(new ParseActionListener());
        
        executeButton = new JButton("Execute Plan");
        executeButton.addActionListener(new ExecuteActionListener());
        executeButton.setEnabled(false);
        
        clearButton = new JButton("Clear All");
        clearButton.addActionListener(e -> clearAll());
        
        buttonPanel.add(parseButton);
        buttonPanel.add(executeButton);
        buttonPanel.add(clearButton);
        
        JPanel statusPanel = new JPanel(new BorderLayout());
        
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("Ready");
        
        statusLabel = new JLabel("Ready to parse action descriptions");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        statusPanel.add(progressBar, BorderLayout.CENTER);
        statusPanel.add(statusLabel, BorderLayout.SOUTH);
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(statusPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void addExampleText() {
        String example = "Click on File menu. " +
                        "Click on Open option. " +
                        "Type \"test.txt\" in the filename field. " +
                        "Press Enter key. " +
                        "Wait 2 seconds. " +
                        "Verify that the file is opened. " +
                        "Take screenshot for verification.";
        
        descriptionArea.setText(example);
    }
    
    private class ParseActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String description = descriptionArea.getText().trim();
            String appName = appNameField.getText().trim();
            
            if (description.isEmpty()) {
                JOptionPane.showMessageDialog(ActionPlannerGUI.this, 
                    "Please enter an action description", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (appName.isEmpty()) {
                JOptionPane.showMessageDialog(ActionPlannerGUI.this, 
                    "Please enter the target application name", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            parseButton.setEnabled(false);
            statusLabel.setText("Parsing action description...");
            progressBar.setIndeterminate(true);
            
            SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        agent.executeActionSequence(description, appName);
                        
                        publish("Parsing completed successfully");
                        
                    } catch (Exception ex) {
                        publish("Error parsing description: " + ex.getMessage());
                    }
                    return null;
                }
                
                @Override
                protected void process(List<String> chunks) {
                    for (String message : chunks) {
                        appendToLog(message);
                    }
                }
                
                @Override
                protected void done() {
                    parseButton.setEnabled(true);
                    executeButton.setEnabled(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(0);
                    statusLabel.setText("Ready to execute action plan");
                    
                    displayActionPlan();
                }
            };
            
            worker.execute();
        }
    }
    
    private class ExecuteActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            executeButton.setEnabled(false);
            parseButton.setEnabled(false);
            statusLabel.setText("Executing action plan...");
            progressBar.setValue(0);
            
            SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        String description = descriptionArea.getText().trim();
                        String appName = appNameField.getText().trim();
                        
                        publish("Starting execution of action plan...");
                        
                        agent.executeActionSequence(description, appName);
                        
                        publish("Execution completed");
                        
                    } catch (Exception ex) {
                        publish("Error during execution: " + ex.getMessage());
                    }
                    return null;
                }
                
                @Override
                protected void process(List<String> chunks) {
                    for (String message : chunks) {
                        appendToLog(message);
                    }
                }
                
                @Override
                protected void done() {
                    executeButton.setEnabled(true);
                    parseButton.setEnabled(true);
                    progressBar.setValue(100);
                    progressBar.setString("Execution Complete");
                    statusLabel.setText("Action plan execution finished");
                }
            };
            
            worker.execute();
        }
    }
    
    private void displayActionPlan() {
        try {
            String description = descriptionArea.getText().trim();
            List<SwingAutomationAgent.ActionStep> steps = agent.parseHumanDescription(description);
            
            StringBuilder planText = new StringBuilder();
            planText.append("Parsed Action Plan:\n");
            planText.append("==================\n\n");
            
            for (int i = 0; i < steps.size(); i++) {
                SwingAutomationAgent.ActionStep step = steps.get(i);
                planText.append(String.format("%d. %s", i + 1, formatActionStep(step)));
                planText.append("\n");
            }
            
            planText.append("\nTotal Steps: ").append(steps.size());
            planText.append("\n\nClick 'Execute Plan' to run the automation.");
            
            actionPlanArea.setText(planText.toString());
            
        } catch (Exception e) {
            actionPlanArea.setText("Error parsing action plan:\n" + e.getMessage() + 
                                  "\n\nPlease check your action description and try again.");
        }
    }
    
    private String formatActionStep(SwingAutomationAgent.ActionStep step) {
        return step.toString();
    }
    
    private void appendToLog(String message) {
        SwingUtilities.invokeLater(() -> {
            executionLogArea.append("[" + java.time.LocalTime.now().toString() + "] " + message + "\n");
            executionLogArea.setCaretPosition(executionLogArea.getDocument().getLength());
        });
    }
    
    private void clearAll() {
        descriptionArea.setText("");
        actionPlanArea.setText("");
        executionLogArea.setText("");
        appNameField.setText("");
        progressBar.setValue(0);
        progressBar.setString("Ready");
        statusLabel.setText("Ready to parse action descriptions");
        executeButton.setEnabled(false);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new ActionPlannerGUI().setVisible(true);
            } catch (Exception e) {
                System.err.println("Error starting ActionPlannerGUI: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
