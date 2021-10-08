import javax.swing.*;
import java.awt.*;

public class GameWindow {
    // Window dimensions
    public static int window_width = 1366;
    public static int window_height = 768;

    // Background color of the canvas
    public static Color background_colour = new Color(28, 27, 31);

    public static void main(String[] args) {
        // Create the interface panel
        MainPanel main_panel = new MainPanel();
        main_panel.setBackground(background_colour); // Set the colour of the background

        // Create the window frame
        JFrame main_window = new JFrame("MadMath Idle");
        main_window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main_window.setBounds(0, 0, window_width, window_height); // Set the window dimensions
        main_window.setLocationRelativeTo(null); // Center the window in the middle of the screen
        main_window.getContentPane().add(main_panel); // Add the main panel inside the window
        main_window.setVisible(true); // Draw visible
        main_window.setResizable(false); // Not allowed to resize the window

        // Replace the default jar coffee icon
        Image icon = Toolkit.getDefaultToolkit().getImage("images/windows-icon.png");
        main_window.setIconImage(icon);
    }
}


