import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class MainPanel extends JPanel implements KeyListener {

    //region Variable Initialisation
    String keyboard_string = ""; // String the user has typed
    int char_max = 3; // Limit the amount of characters the user can type
    public static int total_marks = 0; // Points counting (marks) CHANGE THIS TO A DATA TYPE THAT CAN HOLD MORE LATER
    double display_marks = 0;
    double display_mps = 0;
    double display_qv = 0;
    public static int question_value = 1; // Value of the question (total_marks += question_value)
    public static int marks_per_second = 0; // The idle addition variable
    long questions_solved = 0; // Amount of questions solved (used for progression)
    int values_max = 5; // Random values range
    int value1 = -1; // First value
    int value2 = -1; // Second value

    // Window buffers
    int top_window_buffer = 60;
    int left_window_buffer = 45;
    int right_window_buffer = GameWindow.window_width - 25;
    int bottom_window_buffer = GameWindow.window_height - 65;

    int question_area_ypos = 295;
    int answer_area_ypos = 375;

    // Fonts
    Font ubuntu_font;
    float question_fontsize = 72f;
    float marks_fontsize = 40f;
    float statistics_fontsize = 20f;

    // Images
    BufferedImage backdrop_image1;
    BufferedImage backdrop_image2;
    BufferedImage backdrop_image3;
    BufferedImage backdrop_display_image = backdrop_image1;

    // Upgrades
    public static int upgrade_ystart = 80;
    public static int upgrade_x = 990;
    public static int upgrade_gap = 12;

    //region Upgrade array containing all the class types.
    public static Upgrade[] upgrade = {
            new Upgrade("Pencil",                   "To do mathematics, you need something to write with.",             15L,            0,          1, 1,   "pencil.png"),
            new Upgrade("Mathematician",            "Mathematicians do maths for a living.",                            100L,           1,          0, 4,   "mathematician.png"),
            new Upgrade("Trigonometry",             "Trigonometry is all about triangles",                              500L,           4,          0, 6,   "trigonometry.png"),
            new Upgrade("Amphetamine",              "A drug that increases focus and concentration.",                   3000L,          10,         0, 8,   "amphetamine.png"),
            new Upgrade("Artificial Intelligence",  "Humans played god and made A.I",                                   10000L,         40,         0, 12,  "artificial_intelligence.png"),
            new Upgrade("Quantum Computing",        "Powerful machines",                                                40000L,         100,        0, 16,  "quantum_computing.png"),
            new Upgrade("Space Travel",             "Maybe we can find Aliens to help solve the math questions",        200000L,        400,        0, 24,  "space_travel.png"),
            new Upgrade("Time Travel",              "Travelling into the future to find the answer to our questions",   1500000L,       6666,       0, 32,  "time_travel.png"),
            new Upgrade("Animal Sacrifice",         "A ritiual sacrificing an animal to solve maths",                   123666444L,     98765,      0, 64,  "animal_sacrifice.png"),
            new Upgrade("Undead Experiment",        "Bringing the dead back to life",                                   3999999999L,    999999,     0, 76,  "undead_experiments.png"),
            new Upgrade("Nuclear Warfare",          "This isn't about solving maths anymore",                           75000000000L,   10000000,   0, 102, "nuclear_warfare.png"),
    }; //endregion

    // Debug
    boolean developer_mode = true;
    //endregion

    // Constructor
    MainPanel() {
        try {
            // Register and load main font
            if (ubuntu_font == null) ubuntu_font = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Ubuntu-R.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Ubuntu-R.ttf")));
        } catch (IOException | FontFormatException e) {}

        // Setup the keyboard listener.
        addKeyListener(this);
        setFocusable(true);
        requestFocus();

        // Randomise values
        randomiseValues();

        // Add a mouse listeners to each of the upgrades
        for (int i = 0; i < upgrade.length; i++) {
            addMouseListener(upgrade[i]);
            addMouseMotionListener(upgrade[i]);
            addMouseWheelListener(upgrade[i]);
            System.out.println("added mouse listener to upgrade " + i);
        }

        // Runnable thread every second
        // https://stackoverflow.com/questions/12908412/print-hello-world-every-x-seconds
        Runnable everySecond = () -> { total_marks += marks_per_second; };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(everySecond, 0, 1, TimeUnit.SECONDS);
    }

    // Drawing on the panel
    public void paint(Graphics g) {
        paintComponent(g);

        // Anti-aliasing for smoother text display
        Graphics2D g2d = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g2d.setRenderingHints(rh);

        // Draw all the upgrades
        // It will only be drawn if the upgrade object is flagged as displayed.
        for (int i = 0; i < upgrade.length; i++) {
            upgrade[i].draw((Graphics2D) g);
        }

        // Loading images
        try {
            // Backdrops
            if (backdrop_image1 == null) backdrop_image1 = ImageIO.read(new File("images/backdrop-level1.png"));
            if (backdrop_image2 == null) backdrop_image2 = ImageIO.read(new File("images/backdrop-level2.png"));
            if (backdrop_image3 == null) backdrop_image3 = ImageIO.read(new File("images/backdrop-level3.png"));
        } catch (IOException e) {}
        // Switching between the different backdrop images for different string lengths
        switch (keyboard_string.length()) {
            case 0:
            case 1:
                backdrop_display_image = backdrop_image1;
                break;
            case 2:
                backdrop_display_image = backdrop_image2;
                break;
            case 3:
                backdrop_display_image = backdrop_image3;
                break;
        }
        // Draw the backdrop under the answer area
        g.drawImage(backdrop_display_image, left_window_buffer - 5, 380, this);

        // Set the sizes of the fonts that are going to be used
        Font question_font = ubuntu_font.deriveFont(question_fontsize);
        Font marks_font = ubuntu_font.deriveFont(marks_fontsize);
        Font statistics_font = ubuntu_font.deriveFont(statistics_fontsize);

        // Drawing text
        g.setColor(Color.WHITE);

        // Incrementally adding the values
        display_marks = lerpDisplayed(display_marks, total_marks, 0.3);
        display_mps = lerpDisplayed(display_mps, marks_per_second, 0.3);
        display_qv = lerpDisplayed(display_qv, question_value, 0.3);

        // Total marks
        g.setFont(marks_font);
        g.drawString(stringLargeNumber(new BigDecimal(Math.round(display_marks))) + " marks", left_window_buffer, top_window_buffer + (getStringHeight(g, marks_font)));
        // Question value
        g.setFont(statistics_font);
        g.drawString("question value: " + new BigDecimal(Math.round(display_qv)), left_window_buffer, (top_window_buffer + (getStringHeight(g, statistics_font))) + getStringHeight(g, marks_font) + 10);
        // Marks per second
        g.drawString("marks per second: " + new BigDecimal(Math.round(display_mps)), left_window_buffer, (top_window_buffer + (getStringHeight(g, statistics_font))) + getStringHeight(g, marks_font) + getStringHeight(g, statistics_font) + 20);
        // Question area
        g.setFont(question_font);
        g.drawString(value1 + " + " + value2, left_window_buffer, question_area_ypos + (getStringHeight(g, question_font)));
        // Answer area
        g.drawString("= " + keyboard_string, left_window_buffer, answer_area_ypos + (getStringHeight(g, question_font)));

        repaint();
    }

    //region Key Listener Interface Methods
    public void keyPressed(KeyEvent e) {
        // If the user presses a numerical key. We need to add that key to the keyboard_string
        if (e.getKeyChar() >= '0' && e.getKeyChar() <= '9') {
            // Add the char to the keyboard string
            keyboard_string += KeyEvent.getKeyText(e.getKeyCode());

            // Limit the keyboard string length to the char max
            if (keyboard_string.length() > char_max) {
                keyboard_string = keyboard_string.substring(0, char_max);
            }

            e.consume();
        }

        // Deleting characters
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if (!keyboard_string.equals("")) {
                // Remove the last char in the string
                keyboard_string = keyboard_string.substring(0, keyboard_string.length() - 1);

                e.consume();
            }
        }

        // Parsing
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            // There must be text typed in
            if (!keyboard_string.equals("")) {
                // If answer is correct
                if (Integer.parseInt(keyboard_string) == value1 + value2) {
                    keyboard_string = ""; // Clear the keyboard string
                    total_marks += question_value; // Add to the total_marks
                    questions_solved++; // Increment the questions solved

                    randomiseValues(); // Using this function we can get new random values to solve.
                    createUpgrades(); // Run the function to check if we should show a new upgrade.

                    e.consume(); // Stop handling

                    System.out.println("Correct");
                } else {
                    System.out.println("Wrong");
                }
            } else {
                System.out.println("No text");
            }
        }

        // Closing the game
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.out.println("Closing game");
            System.exit(0);
        }

        // Developer mode cheats
        if (developer_mode && e.getKeyCode() == KeyEvent.VK_EQUALS) {
            total_marks += question_value * 1000;
            createUpgrades();
        }
    }
    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
    //endregion


    // Process of creating the upgrades and setting their positions on the interface
    public void createUpgrades() {
        // Loop through all the upgrades:
        for (int i = 0; i < upgrade.length; i++) {

            // Displaying the upgrade if the player has progressed at or past the creation
            if (total_marks >= upgrade[i].creation && !upgrade[i].displayed)  {
                int yy;

                if (i == 0) {
                    // First upgrade should be set to the initial y position.
                    yy = upgrade_ystart;
                }
                else {
                    // The upgrade should be under the one above it.
                    yy = upgrade[i-1].y + upgrade[i-1].button_height + upgrade_gap;
                }

                upgrade[i].displayed = true; // Display the upgrade.
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
    public static int getStringHeight(Graphics page, Font f) {
        FontMetrics fm = page.getFontMetrics(f);
        return fm.getAscent();
    }

    // Convert large numbers to abbreviated version
    public static String stringLargeNumber(BigDecimal number) {
        //region Arrays
        BigDecimal[] large_numbers = {
            new BigDecimal("1000000"),
            new BigDecimal("1000000000"),
            new BigDecimal("1000000000000"),
            new BigDecimal("1000000000000000"),
            new BigDecimal("1000000000000000000"),
            new BigDecimal("1000000000000000000000"),
            new BigDecimal("1000000000000000000000000"),
            new BigDecimal("1000000000000000000000000000"),
            new BigDecimal("1000000000000000000000000000000"),
            new BigDecimal("1000000000000000000000000000000000"),
            new BigDecimal("1000000000000000000000000000000000000"),
            new BigDecimal("1000000000000000000000000000000000000000"),
            new BigDecimal("1000000000000000000000000000000000000000000"),
            new BigDecimal("1000000000000000000000000000000000000000000000"),
            new BigDecimal("1000000000000000000000000000000000000000000000000"),
            new BigDecimal("1000000000000000000000000000000000000000000000000000"),
            new BigDecimal("1000000000000000000000000000000000000000000000000000000"),
            new BigDecimal("1000000000000000000000000000000000000000000000000000000000"),
            new BigDecimal("1000000000000000000000000000000000000000000000000000000000000"),
            new BigDecimal("1000000000000000000000000000000000000000000000000000000000000000")
        };
        String[] abbreviations = {
            "Million",
            "Billion",
            "Trillion",
            "Quadrillion",
            "Quintillion",
            "Sextillion",
            "Septillion",
            "Octillion",
            "Nonillion",
            "Decillion",
            "Undecillion",
            "Duodecillion",
            "Tredecillion",
            "Quattuordecillion",
            "Quindecillion",
            "Sexdecillion",
            "Septendecillion",
            "Octodecillion",
            "Novemdecillion",
            "Vigintillion",
        }; // endregion

        BigDecimal number_prefix;
        String string_prefix = "";
        String string_suffix = "";

        for (int i = 0; i < large_numbers.length; i++) {
            BigDecimal lower = large_numbers[i].subtract(BigDecimal.ONE);
            BigDecimal upper;

            // Final index buffer
            if (i == large_numbers.length-1) { upper = new BigDecimal(large_numbers[large_numbers.length-1] + "000"); }
            else { upper = large_numbers[i+1]; }

            // Conversion of the number to abbreviated form.
            if (number.compareTo(lower) > 0 && number.compareTo(upper) < 0) {
                number_prefix = number.divide(large_numbers[i]);
                string_prefix = String.format(java.util.Locale.US,"%.3f", number_prefix); // Format to 3 decimal places.
                string_suffix = abbreviations[i];
            }
            // If the number is bigger than the largest listed value, flag it as "Infinity".
            else if (number.compareTo(large_numbers[large_numbers.length-1]) > 0) {
                string_prefix = "Infinity";
                string_suffix = "";
            }
            // If the number is less than one million, just leave it as it is.
            else if (number.compareTo(large_numbers[0]) < 0) {
                string_prefix = number.toString();
                string_suffix = "";
            }
        }

        return string_prefix + " " + string_suffix.toLowerCase();
    }

    public double lerpDisplayed(double value, int target, double lerp) {
        if (value < target) {
            value = value + lerp * (target - value);
        } else {
            value = target;
        }

        return value;
    }

}