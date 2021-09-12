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

    // Creation method
    public void init() {
        // Create the font.
        try {
            ubuntu_font = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Ubuntu-R.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Ubuntu-R.ttf")));
        }
        catch (IOException | FontFormatException e) {

        }

        // Randomise values
        randomiseValues(values_max);

        // Keyboard listener setup
        addKeyListener(this);
        setFocusable(true);
        requestFocus();


    }

    // Drawing on the panel
    public void paint(Graphics g) {
        paintComponent(g);

        // Anti-aliasing
        Graphics2D g2d = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g2d.setRenderingHints(rh);


        // Set the sizes of the fonts that are going to be used
        Font question_font = ubuntu_font.deriveFont(question_fontsize);
        Font marks_font = ubuntu_font.deriveFont(marks_fontsize);

        // Set the colour to white
        g.setColor(Color.WHITE);
        // Total marks
        g.setFont(marks_font);
        String str = total_marks + " Marks";
        g.drawString(str, left_window_buffer, top_window_buffer + (getStringHeight(g, marks_font, str)/2));

        // Question area
        g.setFont(question_font);
        str = value1 + " + " + value2;
        g.drawString(str, left_window_buffer, 300 + (getStringHeight(g, question_font, str)/2));
        // Answer area
        str = "= " + keyboard_string;
        g.drawString(str, left_window_buffer, 370 + (getStringHeight(g, question_font, str)/2));


        // Repaint
        repaint();
    }

    /* This function returns a random integer between a specified maximum and minimum */
    public int randomRange(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
    /* This function can be used to set the question values to randomised values */
    public void randomiseValues(int max) {
        value1 = randomRange(1, values_max);
        value2 = randomRange(1, values_max);
    }

    public static int getStringHeight(Graphics page, Font f, String s) {
        // Find the size of string s in the font of the Graphics context "page"
        FontMetrics fm = page.getFontMetrics(f);
        return fm.getAscent();
    }

    // Key listener methods
    public void keyPressed(KeyEvent e) {
        // Keyboard string control
        char c = e.getKeyChar();
        if (c >= '0' && c <= '9') {
            keyboard_string += KeyEvent.getKeyText(e.getKeyCode());
            e.consume();
        }

        // Delete a char from the keyboard string
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if (!keyboard_string.equals("")) {
                keyboard_string = keyboard_string.substring(0, keyboard_string.length() - 1);
            }
        }

        // Close game when pressed escape key
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
            System.out.println("you pressed a key");
        }

        // Submission of answer
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            System.out.println("Tried to submit");

            // If answer is correct
            total_marks++;
        }
    }
    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
}