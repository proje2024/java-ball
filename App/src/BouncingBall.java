import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Graphics;

public class BouncingBall extends JPanel implements ActionListener {

    private static final int BALL_DIAMETER = 30;
    private int x = 0, y = 1;
    private int xDirection = 1, yDirection = 1;
    private Timer timer;

    public BouncingBall() {
        timer = new Timer(5, this); // Topun hızını kontrol eder
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);
        g.fillOval(x, y, BALL_DIAMETER, BALL_DIAMETER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (x + xDirection < 0 || x + xDirection > getWidth() - BALL_DIAMETER) {
            xDirection *= -1; // Duvara çarptığında yön değiştir
        }
        if (y + yDirection < 0 || y + yDirection > getHeight() - BALL_DIAMETER) {
            yDirection *= -1; // Duvara çarptığında yön değiştir
        }
        x += xDirection;
        y += yDirection;
        repaint(); // Framei tekrar çizer, böylece top hareket eder
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Bouncing Ball");
        BouncingBall bouncingBall = new BouncingBall();
        frame.add(bouncingBall);
        frame.setSize(400, 400); // Frame boyutu
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
