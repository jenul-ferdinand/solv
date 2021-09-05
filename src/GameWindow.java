import javax.swing.*;

public class GameWindow {
    public static void main(String[] args) {
        Game myCanvas = new Game();
        myCanvas.init();

        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBounds(0, 0, 1280, 720);
        window.getContentPane().add(myCanvas);
        window.setVisible(true);
    }
}


