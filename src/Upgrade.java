import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Graphics2D;
import java.math.BigDecimal;

public class Upgrade extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {

    //region Variable Initialisation
    // Properties
    boolean displayed = false;
    int x = MainPanel.upgrade_x;
    int y = MainPanel.upgrade_ystart;
    int quantity = 0;

    // Images
    BufferedImage icon_image;
    int icon_width;
    int icon_height;
    BufferedImage button_image;
    BufferedImage button_bright_image;
    BufferedImage button_white_image;
    BufferedImage button_black_image;
    BufferedImage button_displayed_image;
    int button_height;
    int button_width;
    Area button_area;
    // Image alphas
    float flash_alpha = 0f;
    float default_dark_alpha = 0.35f;
    float dark_alpha = default_dark_alpha;

    // Font
    Font ubuntu_font;
    Font label_font;
    float label_fontsize = 20f;
    // Colours
    Color cost_colour_green = new Color(52, 237, 104);
    Color cost_colour_red = new Color(235, 64, 52);
    Color cost_colour = cost_colour_green;

    // Scrolling
    int scroll_speed = 36;
    boolean scroll_down_locked = true;
    boolean scroll_up_locked = true;

    // Attributes initialisation
    String name, desc, icon_path;
    int creation, mps, qv;
    long cost, base_cost;
    //endregion

    // Constructor
    public Upgrade(String name, String desc, long cost, int mps, int qv, int creation, String icon_path) {
        this.name = name;           // Name of the upgrade
        this.creation = creation;   // This is how many marks the player should have to make the upgrade visible
        this.cost = cost;           // Cost of the upgrade
        this.base_cost = cost;      // Base cost of the upgrade
        this.mps = mps;             // Marks per second benefit
        this.qv = qv;               // Question value benefit
        this.desc = desc;           // Description that will be on the upgrade's tooltip
        this.icon_path = icon_path; // The file path for the icon image
    }

    // Drawing the upgrade and it's contents
    public void draw(Graphics2D g) {

        // Image & font management
        try {
            // Store the button image in variable
            if (button_image == null) button_image = ImageIO.read(new File("images/upgrade-button.png"));
            if (button_bright_image == null) button_bright_image = ImageIO.read(new File("images/upgrade-button-bright.png"));
            if (button_white_image == null) button_white_image = ImageIO.read(new File("images/upgrade-button-white.png"));
            // Get height and width of the button
        button_height = button_image.getHeight();
            button_width = button_image.getWidth();
        button_white_image = getImage(button_white_image, "upgrade-button-white.png");
        button_black_image = getImage(button_black_image, "upgrade-button-black.png");

            // Store the icon image in variable
            if (icon_image == null) icon_image = ImageIO.read(new File("images/" + icon_path));
        // Get height and width of the icon
        icon_height = icon_image.getHeight();
        icon_width = icon_image.getWidth();

        // Image & font management
        try {
            // Fonts
            if (ubuntu_font == null) ubuntu_font = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Ubuntu-R.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Ubuntu-R.ttf")));
        } catch (IOException | FontFormatException e) { }

        // Visible drawing elements
        if (displayed) {
            if (button_displayed_image == null) button_displayed_image = button_image; // Set first image
            if (ubuntu_font != null) label_font = ubuntu_font.deriveFont(label_fontsize); // Font resizing

            g.drawImage(button_displayed_image, x, y,this); // Draw the button
            g.drawImage(icon_image, x + 10, y + 10, this); // Draw the icon

            // Draw the details
            if (ubuntu_font != null) {
                // Name
                g.setColor(Color.WHITE);
                g.setFont(label_font);
                g.drawString(name + ": " + quantity, x + icon_width + 15, (y + getStringHeight(g, label_font)) + 10);

                // Cost
                g.setColor(cost_colour);
                g.drawString("M:" + MainPanel.stringLargeNumber(new BigDecimal(cost)), x + icon_width + 15, (y + getStringHeight(g, label_font)) + button_height - 30);

                // Check affordability to change settings
                if (MainPanel.total_marks >= cost) {
                    cost_colour = cost_colour_green;
                    dark_alpha = 0f;
                } else {
                    cost_colour = cost_colour_red;
                    dark_alpha = default_dark_alpha;
                }
            }

            // Draw the flash image with alpha composite implemented
            // https://www.daniweb.com/programming/software-development/threads/358686/setting-image-opacity
            Composite default_composite = g.getComposite(); // Get the original composite
            AlphaComposite flash_alpha_composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, flash_alpha); // Create the alpha composite
            g.setComposite(flash_alpha_composite); // Set to the alpha composite
            g.drawImage(button_white_image, x, y, this); // Draw the white flash overlay
            g.setComposite(default_composite); // Reset back to the original composite
            // Dark overlay alpha & drawing
            AlphaComposite dark_alpha_composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, dark_alpha);
            g.setComposite(dark_alpha_composite);
            g.drawImage(button_black_image, x, y, this);
            g.setComposite(default_composite);

            // Linear interpolate alpha back to zero
            if (flash_alpha > 0) { flash_alpha = lerp(flash_alpha, 0f, 0.085f); } else { flash_alpha = 0; }
        }
    }

    public void mousePressed(MouseEvent e) {
        if (displayed) {
            // Set the area of the button
            button_area = new Area(new Rectangle(x, y, button_width, button_height));
            // If the mouse is over the upgrade
            if (button_area.contains(e.getX(), e.getY())) {
                // If the user has enough to purchase
                if  (MainPanel.total_marks >= cost) {

                    MainPanel.total_marks -= cost;
                    MainPanel.marks_per_second += mps;
                    MainPanel.question_value += qv;
                    quantity++;
                    flash_alpha = 1f;

                    // Increase the cost using cookie clicker formula, this is fundamental for difficulty.
                    // https://gamedevelopment.tutsplus.com/articles/numbers-getting-bigger-the-design-and-math-of-incremental-games--cms-24023
                    double formula = base_cost * Math.pow(1.15, quantity);
                    cost = (long) formula;

                    // Debug
                    System.out.println(name + " upgrade purchased!");
                }
            }
        }
    }
    public void mouseWheelMoved(MouseWheelEvent e) {
        // Must be displayed
        if (displayed) {
            // Scrolling
            int scroll = e.getWheelRotation();
            if (scroll < 0 && !scroll_up_locked) {
                y += scroll_speed;
            }
            if (scroll > 0 && !scroll_down_locked) {
                y -= scroll_speed;
            }

            // Locking scrolling
            Upgrade first_upgrade = MainPanel.upgrade[0];
            scroll_up_locked = first_upgrade.y >= MainPanel.upgrade_ystart;
            scroll_down_locked = first_upgrade.y <= MainPanel.upgrade_ystart - scroll_speed * 19;

            hoveringOver(e);
        }

        //System.out.println("" + y);
    }
    public void mouseMoved(MouseEvent e) {
        if (displayed) {
            hoveringOver(e);
        }
    }

    // Call this to do whatever when hovering over the upgrade
    public void hoveringOver(MouseEvent e) {
        // Set the area of the button
        button_area = new Area(new Rectangle(x, y, button_width, button_height));
        // If the mouse is over the upgrade and displayed
        if (button_area.contains(e.getX(), e.getY()) && displayed && MainPanel.total_marks >= cost) {
            // Change displayed button image to the bright version
            button_displayed_image = button_bright_image;
        }
        else {
            // Reset back to default image
            button_displayed_image = button_image;
        }
    }

    // Getting the height of a string
    public int getStringHeight(Graphics page, Font f) {
        FontMetrics fm = page.getFontMetrics(f);
        return fm.getAscent();
    }

    // Linear interpolation method
    public float lerp(float a, float b, float f) {
        return a + f * (b - a);
    }

    //region Non-functioning mouse interface methods
    public void mouseClicked(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    //endregion
}
