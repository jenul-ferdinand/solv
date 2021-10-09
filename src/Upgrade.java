import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.math.BigDecimal;

public class Upgrade extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, CommonMethods {

    //region Variable Initialisation
    // Properties
    boolean displayed = false;
    int x = MainPanel.upgrade_x;
    int y = MainPanel.upgrade_ystart;
    int quantity = 0;

    // Tooltip
    int tooltip_counter = 0;
    int tooltip_time = 26;
    int tooltip_x = x - 20;
    int tooltip_y = y;
    int tooltip_width = 250;
    int tooltip_height = 200;
    int tooltip_left = tooltip_x - tooltip_width;
    int tooltip_bottom = tooltip_y + tooltip_height;
    int tooltip_buffer = 10;
    Color tooltip_colour = new Color(44, 43, 47);
    Font tooltip_font;
    float tooltip_fontsize = 16f;

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
    float flash_max_alpha = 0.85f;
    float flash_lerp = 0.1f;
    float default_dark_alpha = 0.35f;
    float dark_alpha = default_dark_alpha;

    // Font
    Font ubuntu_font;
    Font ubuntu_mono_font;
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
    //endregion

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
        // Button
        button_image = getImage(button_image, "upgrade-button.png");
        button_width = button_image.getWidth();
        button_height = button_image.getHeight();
        button_bright_image = getImage(button_bright_image, "upgrade-button-bright.png");
        button_white_image = getImage(button_white_image, "upgrade-button-white.png");
        button_black_image = getImage(button_black_image, "upgrade-button-black.png");
        // Icon
        icon_image = getImage(icon_image, icon_path);
        icon_height = icon_image.getHeight();
        icon_width = icon_image.getWidth();

        // Font
        ubuntu_font = getFont(ubuntu_font, "Ubuntu-R.ttf");
        ubuntu_mono_font = getFont(ubuntu_mono_font, "UbuntuMono-R.ttf");

        // Visible elements
        if (displayed) {
            if (button_displayed_image == null) button_displayed_image = button_image; // Set first image
            // Font resizing
            if (ubuntu_font != null) label_font = ubuntu_font.deriveFont(label_fontsize);
            if (ubuntu_mono_font != null) tooltip_font = ubuntu_mono_font.deriveFont(tooltip_fontsize);

            g.drawImage(button_displayed_image, x, y,this); // Draw the button
            g.drawImage(icon_image, x + 10, y + 10, this); // Draw the icon

            // Draw the details
            if (ubuntu_font != null) {
                // Name
                g.setColor(Color.WHITE);
                g.setFont(label_font);
                g.drawString(name + ": " + stringLargeNumber(new BigDecimal(quantity)), x + icon_width + 15, (y + getStringHeight(g, label_font)) + 10);

                // Cost
                g.setColor(cost_colour);
                g.drawString("M:" + stringLargeNumber(new BigDecimal(cost)), x + icon_width + 15, (y + getStringHeight(g, label_font)) + button_height - 30);

                // Check affordability, to change settings.
                if (MainPanel.total_marks >= cost) {
                    cost_colour = cost_colour_green;
                    dark_alpha = 0f;
                } else {
                    cost_colour = cost_colour_red;
                    dark_alpha = default_dark_alpha;
                }

                // Tooltips
                if (button_displayed_image == button_bright_image) {
                    // Increment the counter
                    tooltip_counter++;
                    // Check if we have elapsed the time so we can draw the tooltip
                    if (tooltip_counter >= tooltip_time) {
                        // Background of tooltip
                        g.setColor(tooltip_colour);
                        g.fillRoundRect(tooltip_left, tooltip_y, tooltip_width, tooltip_height, 15,  15);
                        // Name and description
                        g.setColor(Color.WHITE);
                        g.setFont(tooltip_font);
                        int font_height = getStringHeight(g, tooltip_font);
                        int line_width = tooltip_width-tooltip_buffer*2;
                        int x_pos = tooltip_left+tooltip_buffer;
                        drawStringMultiLine(g, "["+name+"]", line_width, x_pos, tooltip_y+tooltip_buffer+font_height);
                        drawStringMultiLine(g, desc, line_width, x_pos, (tooltip_y+tooltip_buffer+font_height) + font_height*3);
                        // Proper english
                        String plural;
                        String str_quantity = ""+quantity;
                        if (quantity > 1) { plural = "s"; }
                        else if (quantity == 0) {
                            plural = "s";
                            str_quantity = "No";
                        } else { plural = ""; }
                        // Statistics
                        if (name.equals("Pencil")) {
                            drawStringMultiLine(g, "" + str_quantity + " " + name+plural + " giving " + qv*quantity + " question value", line_width, x_pos, (tooltip_y+tooltip_buffer+font_height) + font_height*9); // Stats
                        } else {
                            drawStringMultiLine(g, "" + str_quantity + " " + name+plural + " getting " + mps*quantity + " marks per second", line_width, x_pos, (tooltip_y+tooltip_buffer+font_height) + font_height*9);
                        }
                    }
                } else {
                    // Reset counter since we are not hovering over the button no more
                    tooltip_counter = 0;
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
            if (flash_alpha > 0) { flash_alpha = lerp(flash_alpha, 0f, flash_lerp); } else { flash_alpha = 0; }
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
                    flash_alpha = flash_max_alpha;

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
        if (button_area.contains(e.getX(), e.getY()) && displayed) {
            if (MainPanel.total_marks >= cost) {
                // Change displayed button image to the bright version
                button_displayed_image = button_bright_image;
            }

            // Get the mouse y position for the tooltip y position
            tooltip_y = e.getY();
        }
        else {
            // Reset back to default image
            button_displayed_image = button_image;
        }
    }

    //region Not used mouse interface methods
    public void mouseClicked(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    //endregion
}
