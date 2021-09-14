import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
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

    // Init buffered image
    BufferedImage upgrade_button_image;

    // Creation method
    public void init() {


        // Create the font.
        try {
            ubuntu_font = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Ubuntu-R.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Ubuntu-R.ttf")));
        }
        catch (IOException | FontFormatException e) {
            System.out.println("can't process fonts");
        }

        // Randomise values
        randomiseValues();

        // Keyboard listener setup
        addKeyListener(this);
        setFocusable(true);
        requestFocus();


        // Creating the images
        try {
            upgrade_button_image = ImageIO.read(new File("images/upgrade-button.png"));
        } catch (IOException e) { }


    }


    // Drawing on the panel
    public void paint(Graphics g) {
        paintComponent(g);

        // Anti-aliasing
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

        // Set the colour to white
        g.setColor(Color.WHITE);

        // Total marks
        g.setFont(marks_font);
        String str = total_marks + " Marks";
        g.drawString(str, left_window_buffer, top_window_buffer + (getStringHeight(g, marks_font, str)/2));

        // Question value
        g.setFont(statistics_font);
        str = "Question Value: " + question_value;
        g.drawString(str, left_window_buffer, (top_window_buffer + (getStringHeight(g, statistics_font, str)/2))
                + getStringHeight(g, marks_font, str)); // Draw under the marks

        // Marks per second
        str = "Marks Per Second: " + marks_per_second;
        g.drawString(str, left_window_buffer, (top_window_buffer + (getStringHeight(g, statistics_font, str)/2))
                + getStringHeight(g, marks_font, str) + getStringHeight(g, statistics_font, str) + 10);


        // Question area
        g.setFont(question_font);
        str = value1 + " + " + value2;
        g.drawString(str, left_window_buffer, 300 + (getStringHeight(g, question_font, str)/2));
        // Answer area
        str = "= " + keyboard_string;
        g.drawString(str, left_window_buffer, 370 + (getStringHeight(g, question_font, str)/2));


        // Upgrades
        g.drawImage(upgrade_button_image, 0, 0, this);

        // Repaint
        repaint();
    }


    /* this method is called when the user presses a key on the keyboard */
    public void keyPressed(KeyEvent e) {

        // If the user presses a numerical key. We need to add that key to the keyboard_string
        if (e.getKeyChar() >= '0' && e.getKeyChar() <= '9') {

            // Add the char to the keyboard string
            keyboard_string += KeyEvent.getKeyText(e.getKeyCode());
            e.consume();

            // Limit the keyboard string length to the char max
            if (keyboard_string.length() > char_max) {
                keyboard_string = keyboard_string.substring(0, char_max);
            }

        }

        // Deleting a char from the keyboard string
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if (!keyboard_string.equals("")) {
                // Remove the last char in the string
                keyboard_string = keyboard_string.substring(0, keyboard_string.length() - 1);
            }
        }

        // Closing the game
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0); // Close game
        }

        // Submission of answer
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {

            // If answer is correct
            if (Integer.parseInt(keyboard_string) == value1 + value2) {
                // Add to the total_marks
                total_marks += question_value;

                // Clear the keyboard string
                keyboard_string = "";

                // Get new random values
                randomiseValues();

                // Increment the questions solved
                questions_solved++;

                // Print line
                System.out.println("user submitted answer and it was correct.");
            }
        }
    }
    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}



    /* This function returns a random integer between a specified maximum and minimum */
    public int intRandomRange(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    /* This function can be used to set the question values to randomised values */
    public void randomiseValues() {
        value1 = intRandomRange(1, values_max);
        value2 = intRandomRange(1, values_max);
    }

    // This function will return the height of a inputted string.
    public static int getStringHeight(Graphics page, Font f, String s) {
        // Find the size of string s in the font of the Graphics context "page"
        FontMetrics fm = page.getFontMetrics(f);
        // Return the value.
        return fm.getAscent();
    }
}