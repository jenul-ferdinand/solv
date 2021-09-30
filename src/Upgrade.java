import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Upgrade extends JPanel {

    int x = 990;
    int y = 110;
    int button_height;

    Boolean visible = false;

    BufferedImage button_image;

    int creation;

    public Upgrade(int creation) {
        // This is how many marks the player should have to make the upgrade visible
        this.creation = creation;
    }

    // Drawing the upgrade and it's contents
    public void draw(Graphics g) {
        try {
            // Store the button image in variable
            button_image = ImageIO.read(new File("images/upgrade-button.png"));
            // Get height of the button
            button_height = button_image.getHeight();
        } catch (IOException e) { }

        // If classified as visible
        if (visible) {
            // Draw the image
            g.drawImage(button_image, x, y,this);
        }
    }
}
