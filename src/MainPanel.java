import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

class MainPanel extends JPanel implements KeyListener {

    Font ubuntu_font;

    String keyboard_string = ""; // String the user has typed
    int char_max = 2; // Limit the amount of characters the user can type
    int total_marks = 0; // Points counting (marks)
    int question_value = 1; // Value of the question (total_marks += question_value)
    int marks_per_second = 0; // The idle addition variable
    int questions_solved = 0; // Amount of questions solved (used for progression)
    int values_max = 5; // Random values range
    int value1 = -1; // First value
    int value2 = -1; // Second value

    // Window buffers
    int top_window_buffer = 60;
    int left_window_buffer = 45;
    int right_window_buffer = GameWindow.window_width - 25;
    int bottom_window_buffer = GameWindow.window_height - 65;

    // Font sizes
    float question_fontsize = 72f;
    float marks_fontsize = 40f;
    float statistics_fontsize = 20f;

    // Upgrades
    int upgrade_ystart = 110;
    int upgrade_gap = 12;

    Upgrade[] upgrade = {
         new Upgrade(2),
         new Upgrade(4),
    };

    // Creation method
    public void init() {


        // Register and create the font and then store it as a variable Font class.
        try {
            ubuntu_font = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Ubuntu-R.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Ubuntu-R.ttf")));
        }
        catch (IOException | FontFormatException e) {
            System.out.println("can't process fonts");
        }



        // Setup the keyboard listener.
        addKeyListener(this);
        setFocusable(true);
        requestFocus();

        // Randomise values
        randomiseValues();



    }


    // Drawing on the panel
    public void paint(Graphics g) {
        // Paint "black box"
        paintComponent(g);

        // Activate anti-aliasing for smoother text display
        Graphics2D g2d = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP
        );
        g2d.setRenderingHints(rh);


        // Set the sizes of the fonts that are going to be used
        Font question_font = ubuntu_font.deriveFont(question_fontsize);
        Font marks_font = ubuntu_font.deriveFont(marks_fontsize);
        Font statistics_font = ubuntu_font.deriveFont(statistics_fontsize);



        /* Drawing text on the screen */
        g.setColor(Color.WHITE);
        // Total marks
        g.setFont(marks_font);
        String str = total_marks + " Marks";
        g.drawString(str, left_window_buffer, top_window_buffer + (getStringHeight(g, marks_font, str)/2));
        // Question value
        g.setFont(statistics_font);
        str = "Question Value: " + question_value;
        g.drawString(str, left_window_buffer, (top_window_buffer + (getStringHeight(g, statistics_font, str)/2)) + getStringHeight(g, marks_font, str)); // Draw under the marks
        // Marks per second
        str = "Marks Per Second: " + marks_per_second;
        g.drawString(str, left_window_buffer, (top_window_buffer + (getStringHeight(g, statistics_font, str)/2)) + getStringHeight(g, marks_font, str) + getStringHeight(g, statistics_font, str) + 10);
        // Question area
        g.setFont(question_font);
        str = value1 + " + " + value2;
        g.drawString(str, left_window_buffer, 300 + (getStringHeight(g, question_font, str)/2));
        // Answer area
        str = "= " + keyboard_string;
        g.drawString(str, left_window_buffer, 370 + (getStringHeight(g, question_font, str)/2));



        /* Draw all of the upgrades,
           it will only be displayed if upgrade.visible is true. */
        for (int i = 0; i < upgrade.length; i++) {
            upgrade[i].draw(g);
        }


        // Handle update to the paint cycle
        repaint();

    }


    /* this method is called when the user presses a key on the keyboard */
    public void keyPressed(KeyEvent e) {
        // Locals
        int keycode = e.getKeyCode();
        char keychar = e.getKeyChar();

        // If the user presses a numerical key. We need to add that key to the keyboard_string
        if (keychar >= '0' && keychar <= '9') {
            // Add the char to the keyboard string
            keyboard_string += KeyEvent.getKeyText(keycode);

            // Limit the keyboard string length to the char max
            if (keyboard_string.length() > char_max) {
                keyboard_string = keyboard_string.substring(0, char_max);
            }

            // Stop handling
            e.consume();
        }

        // Deleting a char from the keyboard string
        if (keycode == KeyEvent.VK_BACK_SPACE) {
            if (!keyboard_string.equals("")) {
                // Remove the last char in the string
                keyboard_string = keyboard_string.substring(0, keyboard_string.length() - 1);

                // Stop handling
                e.consume();
            }
        }

        // Closing the game
        if (keycode == KeyEvent.VK_ESCAPE) {
            System.exit(0); // Close game
        }

        // Submission of answer
        if (keycode == KeyEvent.VK_ENTER) {

            // If answer is correct
            if (Integer.parseInt(keyboard_string) == value1 + value2) {
                keyboard_string = ""; // Clear the keyboard string
                total_marks += question_value; // Add to the total_marks
                questions_solved++; // Increment the questions solved

                randomiseValues(); // Using this function we can get new random values to solve.
                createUpgrades(); // Run the function to check if we should show a new upgrade.

                e.consume(); // Stop handling

                System.out.println("User submitted the correct answer"); // Debug message
            }

        }
    }
    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}



    // Creating the upgrades
    public void createUpgrades() {
        // Loop through all of the upgrades:
        for (int i = 0; i < upgrade.length; i++) {


            // Displaying the upgrade if the player has progressed at or past the creation
            if (total_marks >= upgrade[i].creation) {
                int yy;

                if (i == 0) {
                    // First upgrade should be set to the initial y position.
                    yy = upgrade_ystart;
                }
                else {
                    // The upgrade should be under the one above it.
                    yy = upgrade[i-1].y + upgrade[i-1].button_height + upgrade_gap;
                }

                upgrade[i].visible = true; // Display the upgrade.
                upgrade[i].y = yy; // Apply the vertical position change


                System.out.println("Displayed an upgrade."); // Debug message
            }


        }


    }



    /* Returns a random integer between a specified maximum and minimum */
    public int intRandomRange(int min, int max) { return (int) ((Math.random() * (max - min)) + min); }

    /* Used to set the question values to randomised values */
    public void randomiseValues() {
        value1 = intRandomRange(1, values_max);
        value2 = intRandomRange(1, values_max);
    }

    // Return the height (pixels) of an inputted string.
    public static int getStringHeight(Graphics page, Font f, String s) {
        // Find the size of string s in the font of the Graphics context "page"
        FontMetrics fm = page.getFontMetrics(f);
        // Return the value.
        return fm.getAscent();
    }



}