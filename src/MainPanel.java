import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class MainPanel extends JPanel implements KeyListener, CommonMethods {

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

    // Fonts
    Font ubuntu_font;
    float question_fontsize = 72f;
    float marks_fontsize = 40f;
    float statistics_fontsize = 20f;
    float upgrades_heading_fontsize = 27f;

    // Images
    BufferedImage backdrop_image1;
    BufferedImage backdrop_image2;
    BufferedImage backdrop_image3;
    BufferedImage backdrop_display_image = backdrop_image1;

    // Upgrades
    public static int upgrade_ystart = 80;
    public static int upgrade_x = 990;
    public static int upgrade_gap = 12;

    // Q&A area
    int question_area_ypos = 295;
    int answer_area_ypos = 375;
    // Separator
    int shop_background_x = upgrade_x - 30;
    int shop_background_width = 6;
    Color shop_background_colour = new Color(34, 33, 38);

    //region Upgrade array containing all the class types.
    public static Upgrade[] upgrade = {
            new Upgrade("Pencil",                   "To do mathematics, you need something to write with.",             15L,            0,          10,1,   "pencil.png"),
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
        // Load the font
        ubuntu_font = getFont(ubuntu_font, "Ubuntu-Medium.ttf");

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

        // Set the sizes of the fonts that are going to be used
        Font question_font = ubuntu_font.deriveFont(question_fontsize);
        Font marks_font = ubuntu_font.deriveFont(marks_fontsize);
        Font statistics_font = ubuntu_font.deriveFont(statistics_fontsize);
        Font upgrades_heading_font = ubuntu_font.deriveFont(upgrades_heading_fontsize);

        // Anti-aliasing for smoother text display
        Graphics2D g2d = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g2d.setRenderingHints(rh);

        // Loading images
        backdrop_image1 = getImage(backdrop_image1, "backdrop-level1.png");
        backdrop_image2 = getImage(backdrop_image2, "backdrop-level2.png");
        backdrop_image3 = getImage(backdrop_image3, "backdrop-level3.png");
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
        // backdrop under the answer area
        g.drawImage(backdrop_display_image, left_window_buffer - 5, 380, this);

        // Shop background
        g.setColor(shop_background_colour);
        g.fillRect(shop_background_x, 0, shop_background_x + shop_background_width, GameWindow.window_height);

        // Draw all the upgrades
        // It will only be drawn if the upgrade object is flagged as displayed.
        for (int i = 0; i < upgrade.length; i++) {
            upgrade[i].draw((Graphics2D) g);
            //upgrade[i].button_displayed_image = upgrade[i].button_image;
        }

        // Draw a rectangle to cover the upgrades when they are scrolled up
        g.setColor(shop_background_colour);
        g.fillRect(upgrade_x, 0, GameWindow.window_width - shop_background_x, upgrade_ystart);
        g.setColor(Color.WHITE);

        // Upgrades heading
        g.setFont(upgrades_heading_font);
        String text = "Upgrades";
        FontMetrics fm = g2d.getFontMetrics();
        int string_width = getStringWidth(text, g, statistics_font);
        g.drawString(text, shop_background_x + (GameWindow.window_width - shop_background_x)/2 - (string_width/2) - 5, upgrade_ystart/2 + 5);


        // Incrementally adding the values
        display_marks = lerpDisplayed(display_marks, total_marks, 0.3);
        display_mps = lerpDisplayed(display_mps, marks_per_second, 0.3);
        display_qv = lerpDisplayed(display_qv, question_value, 0.3);

        //region TEXT
        // Total marks
        g.setFont(marks_font);
        g.drawString(stringLargeNumber(new BigDecimal(Math.round(display_marks))) + " marks", left_window_buffer, top_window_buffer);
        // Question value
        g.setFont(statistics_font);
        g.drawString("question value: " + stringLargeNumber(new BigDecimal(Math.round(display_qv))), left_window_buffer, (top_window_buffer + (getStringHeight(g, statistics_font))) + 10);
        // Marks per second
        g.drawString("marks per second: " + stringLargeNumber(new BigDecimal(Math.round(display_mps))), left_window_buffer, (top_window_buffer + (getStringHeight(g, statistics_font))) + getStringHeight(g, statistics_font) + 20);
        // Question area
        g.setFont(question_font);
        g.drawString(value1 + " + " + value2, left_window_buffer, question_area_ypos + (getStringHeight(g, question_font)));
        // Answer area
        g.drawString("= " + keyboard_string, left_window_buffer, answer_area_ypos + (getStringHeight(g, question_font)));
        //endregion

        // Handle the paint event
        repaint();
    }

    //region Key Listener Interface Methods
    public void keyPressed(KeyEvent e) {
        // If the user presses a numerical key. We need to add that key to the keyboard_string
        if (e.getKeyChar() >= '0' && e.getKeyChar() <= '9' || e.getKeyChar() == '-') {
            // Add the char to the keyboard string
            keyboard_string += e.getKeyChar();

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

    /* Used to set the question values to randomised values */
    public void randomiseValues() {
        value1 = intRandomRange(1, values_max);
        value2 = intRandomRange(1, values_max);
    }
}