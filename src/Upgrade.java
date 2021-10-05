import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Graphics2D;

public class Upgrade extends JPanel implements MouseListener, MouseMotionListener {

    // Properties
    boolean displayed = false;
    int x = 990;
    int y = 110;
    int quantity = 0;

    // Images
    BufferedImage button_image;
    BufferedImage button_bright_image;
    BufferedImage button_white_image;
    BufferedImage button_displayed_image;
    int button_height;
    int button_width;
    Area button_area;
    float flash_alpha = 0f;

    BufferedImage icon_image;
    int icon_width;
    int icon_height;

    // Font
    Font ubuntu_font;
    Font label_font;
    float label_fontsize = 20f;
    // Attributes initialisation
    String name, desc, icon_path;
    int creation, mps, qv;
    long cost, base_cost;
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

            // Store the icon image in variable
            if (icon_image == null) icon_image = ImageIO.read(new File("images/" + icon_path));
            // Get height and width of the icon
            icon_height = icon_image.getHeight();
            icon_width = icon_image.getWidth();

            // Fonts
            if (ubuntu_font == null) ubuntu_font = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Ubuntu-R.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Ubuntu-R.ttf")));
        } catch (IOException | FontFormatException e) { }



        // Visible drawing elements
        if (displayed) {

            if (ubuntu_font != null) label_font = ubuntu_font.deriveFont(label_fontsize);

            // Draw the button
            g.drawImage(button_displayed_image, x, y,this);

            // Draw the icon
            g.drawImage(icon_image, x + 10, y + 10, this);

            // Draw the details
            if (ubuntu_font != null) {
                g.setFont(label_font);
                g.drawString(name + ": " + quantity, x + icon_width + 15, y + getStringHeight(g, label_font) + 10);
            }



            // Draw the flash image with alpha composite implemented
            // https://www.daniweb.com/programming/software-development/threads/358686/setting-image-opacity
            Composite default_composite = g.getComposite();
            AlphaComposite alpha_composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, flash_alpha);
            g.setComposite(alpha_composite);
            g.drawImage(button_white_image, x, y, this);
            g.setComposite(default_composite);



            // Linear interpolate alpha back to zero
            if (flash_alpha > 0) {
                flash_alpha = lerp(flash_alpha, 0f, 0.085f);
            } else {
                flash_alpha = 0;
            }
        }

    }

    private int getStringHeight(Graphics page, Font f) {
        FontMetrics fm = page.getFontMetrics(f);
        return fm.getAscent();
    }


    //region Mouse Interface
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
    public void mouseClicked(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {
        // Set the area of the button
        button_area = new Area(new Rectangle(x, y, button_width, button_height));
        // If the mouse is over the upgrade and displayed
        if (button_area.contains(e.getX(), e.getY()) && displayed) {
            // Change displayed button image to the bright version
            button_displayed_image = button_bright_image;
        }
        else {
            // Reset back to default image
            button_displayed_image = button_image;
        }
    }
    //endregion

    public float lerp(float a, float b, float f) {
        return a + f * (b - a);
    }
}
