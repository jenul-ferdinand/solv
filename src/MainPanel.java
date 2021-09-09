import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

class MainPanel extends JPanel {
    Font test_font = new Font("Calibri", Font.PLAIN, 30);
    JLabel test_label = new JLabel("Lorem Ipsum, The quick brown fox jumped over the lazy dog.");
    Font new_font;

    public void init() {
        // Create the font.
        try {
            new_font = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Ubuntu-R.ttf")).deriveFont(60f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Ubuntu-R.ttf")));
        }
        catch (IOException | FontFormatException e) {

        }

        test_label.setForeground(Color.WHITE);
        test_label.setFont(new_font);
        add(test_label);
    }
}