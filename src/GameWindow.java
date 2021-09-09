import javax.swing.*;
import java.awt.*;

public class GameWindow {
    public static void main(String[] args) {
        // Window dimensions
        int window_width = 1280;
        int window_height = 720;

        // Background color of the canvas
        Color background_colour = new Color(28, 27, 31);

        // Create the interface panel
        MainPanel main_panel = new MainPanel();
        main_panel.init();
        main_panel.setBackground(background_colour); // Set the colour of the background

        // Create the window frame
        JFrame main_window = new JFrame("MadMath Idle");
        main_window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main_window.setBounds(0, 0, window_width, window_height); // Set the window dimensions
        main_window.setLocationRelativeTo(null); // Center the window in the middle of the screen
        main_window.getContentPane().add(main_panel); // Add the main panel inside the window
        main_window.setVisible(true); // Draw visible

        // Display all the fonts
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        for ( int i = 0; i < fonts.length; i++ ) {
            System.out.println(fonts[i]);
        }
    }
}


