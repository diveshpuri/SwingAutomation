import org.sikuli.script.*;
import java.util.Iterator;

/**
 * Example using Sikuli library for Java Swing automation
 * Sikuli provides more advanced image recognition capabilities
 * 
 * To use this, you would need to add Sikuli dependency:
 * Maven: <dependency>
 *          <groupId>com.sikulix</groupId>
 *          <artifactId>sikulixapi</artifactId>
 *          <version>2.0.5</version>
 *        </dependency>
 */
public class SikuliExample {
    
    private Screen screen;
    
    public SikuliExample() {
        this.screen = new Screen();
    }
    
    /**
     * Example Sikuli automation workflow
     */
    public void automateSwingApp() throws FindFailed {
        try {
            screen.wait("images/app_window.png", 10);
            System.out.println("Application window found!");
            
            screen.click("images/file_menu.png");
            screen.click("images/open_option.png");
            
            if (screen.exists("images/file_dialog.png") != null) {
                screen.type("images/filename_field.png", "myfile.txt");
                screen.click("images/open_button.png");
            }
            
            screen.click("images/text_area.png");
            screen.type("Hello, this is automated text input!");
            
            screen.type(Key.CTRL + "s");
            
            if (screen.exists("images/save_dialog.png") != null) {
                screen.type("images/save_filename.png", "automated_output.txt");
                screen.click("images/save_button.png");
            }
            
            System.out.println("Automation completed successfully!");
            
        } catch (FindFailed e) {
            System.err.println("Image not found: " + e.getMessage());
            screen.capture().save("debug_screenshot.png");
        }
    }
    
    /**
     * Advanced pattern matching with similarity threshold
     */
    public void advancedImageMatching() throws FindFailed {
        Pattern buttonPattern = new Pattern("images/submit_button.png").similar(0.7);
        
        if (screen.exists(buttonPattern) != null) {
            screen.click(buttonPattern);
        }
        
        Region topHalf = new Region(0, 0, screen.getW(), screen.getH()/2);
        topHalf.click("images/menu_item.png");
    }
    
    /**
     * Handle multiple similar elements
     */
    public void handleMultipleElements() throws FindFailed {
        Iterator<Match> buttons = screen.findAll("images/generic_button.png");
        
        int buttonIndex = 0;
        while (buttons.hasNext()) {
            Match button = buttons.next();
            System.out.println("Found button at: " + button.getCenter());
            
            if (buttonIndex == 1) {
                screen.click(button);
                break;
            }
            buttonIndex++;
        }
    }
    
    public static void main(String[] args) {
        try {
            SikuliExample automation = new SikuliExample();
            automation.automateSwingApp();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
