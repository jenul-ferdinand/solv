import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Upgrade extends JPanel implements MouseListener {

    boolean displayed = false;
    int x = 990;
    int y = 110;

    BufferedImage button_image;
    int button_height;
    int button_width;

    BufferedImage icon_image;
    int icon_width;
    int icon_height;

    String name;
    int creation;
    long cost;
    String desc;
    String icon_path;

    public Upgrade(String name, String desc, long cost, int mps, int qv, int creation, String icon_path) {
        this.name = name; // Name of the upgrade
        this.creation = creation; // This is how many marks the player should have to make the upgrade visible
        this.cost = cost; // Cost of the upgrade
        this.desc = desc; // Description that will be on the upgrade's tooltip
        this.icon_path = icon_path; // The file path for the icon image
    }

    // Drawing the upgrade and it's contents
    public void draw(Graphics g) {

        // Image management
        try {

            // Store the button image in variable
            button_image = ImageIO.read(new File("images/upgrade-button.png"));
            // Get height and width of the button
            button_height = button_image.getHeight();
            button_width = button_image.getWidth();

            // Store the icon image in variable
            icon_image = ImageIO.read(new File("images/" + icon_path));
            // Get height and width of the icon
            icon_height = icon_image.getHeight();
            icon_width = icon_image.getWidth();

        } catch (IOException e) {
            // Debug
            System.out.println("Image loading failed.");
        }



        // If this upgrade has been displayed on the screen
        if (displayed) {
            // Draw the button
            g.drawImage(button_image, x, y,this);

            // Draw the icon
            g.drawImage(icon_image, x, y, this);
        }


    }



    public void mousePressed(MouseEvent e) {

        if (displayed) {

            // Area checking vars
            Rectangle temp_rect = new Rectangle(x, y, button_width, button_height);
            Area button_area = new Area(temp_rect);

            // If the mouse was over the upgrade
            if (button_area.contains(e.getX(), e.getY())) {

                // Do total marks checking here


                // Debug
                System.out.println("upgrade y: " + y);
                MainPanel.increaseTotalMarks(20);
            }

        }

    }
    public void mouseClicked(MouseEvent e) {

    }
    public void mouseReleased(MouseEvent e) {

    }
    public void mouseEntered(MouseEvent e) {
        // Lighten the button image
    }
    public void mouseExited(MouseEvent e) {
        // Reset the button image
    }
}
