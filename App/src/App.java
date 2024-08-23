import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.Timer;

public class App extends JPanel {

    private List<Ball> balls = new ArrayList<>();
    private final double gravity = 0.1;
    private Rectangle box;

    public App() {
        calculateBoxSize();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (box.contains(e.getX(), e.getY())) {
                    balls.add(new Ball(e.getX(), e.getY(), box));
                }
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                calculateBoxSize(); 
                for (Ball ball : balls) {
                    ball.setBox(box); 
                }
            }
        });

        Timer timer = new Timer(1, e -> {
            moveBalls();
            checkCollisions(); 
            repaint();
        });
        timer.start();
    }

    private void calculateBoxSize() {
        int margin = 50;
        int boxWidth = getWidth() - 2 * margin;
        int boxHeight = getHeight() - 2 * margin;
        int x = margin;
        int y = margin;
        box = new Rectangle(x, y, boxWidth, boxHeight);
    }

    private void moveBalls() {
        for (Ball ball : balls) {
            ball.move(gravity);
        }
    }

    private void checkCollisions() {
        for (int i = 0; i < balls.size(); i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                Ball ball1 = balls.get(i);
                Ball ball2 = balls.get(j);
                if (ball1.isCollidingWith(ball2)) {
                    ball1.handleCollision(ball2);
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Kutuyu çiziyoruz
        g.setColor(Color.BLACK);
        g.drawRect(box.x, box.y, box.width, box.height);

        for (Ball ball : balls) {
            ball.draw(g);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Bouncing Balls in a Resizable Box");
        App app = new App();
        frame.add(app);
        frame.setSize(500, 500);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class Ball {
    private double x, y;
    private double velocityX = 0, velocityY = 0;
    private final int diameter = 50;
    private final double damping = 0.9;
    private final double friction = 0.9; // Yatay hız için sönümleme faktörü
    private final Color color; // Topun rengi
    private Rectangle box; // Topun sekmesi gereken kutu

    public Ball(int x, int y, Rectangle box) {
        this.x = x - diameter / 2; // Topun merkezi tıklanan yerde olsun
        this.y = y - diameter / 2;
        this.box = box;
        this.color = generateRandomColor(); // Rastgele renk oluşturma
    }

    public void setBox(Rectangle newBox) {
        this.box = newBox;
    }

    
    public void move(double gravity) {
        // Yerçekimini hızına ekliyoruz
        velocityY = velocityY + gravity / 50;
    
        // X ekseninde sönümleme (damping) uyguluyoruz
        velocityX = velocityX*friction; // Yatay hıza sönümleme uygulama
    
        // Sınır kontrolü: Kutu içinde yere çarpma
        if (y + velocityY > box.getMaxY() - diameter) {
            y = box.getMaxY() - diameter;
            velocityY = -velocityY * damping; // Hızın bir kısmını kaybederek sekme
        } else if (y + velocityY < box.getMinY()) {
            y = box.getMinY();
            velocityY = -velocityY * damping;
        } else {
            y += velocityY;
        }
    
        // Sınır kontrolü: Kutu içinde yan duvarlara çarpma
        if (x + velocityX > box.getMaxX() - diameter) {
            x = box.getMaxX() - diameter;
            velocityX = -velocityX * damping; // Hızın bir kısmını kaybederek sekme
        } else if (x + velocityX < box.getMinX()) {
            x = box.getMinX();
            velocityX = -velocityX * damping; // Hızın bir kısmını kaybederek sekme
        } else {
            x += velocityX;
        }
    }
    public boolean isCollidingWith(Ball other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < this.diameter;
    }

    public void handleCollision(Ball other) {
        double dx = other.x - this.x;
        double dy = other.y - this.y;

        if (dx == 0 && dy == 0) {
            dx = Math.random() - 0.5;
            dy = Math.random() - 0.5;
        }

        double distance = Math.sqrt(dx * dx + dy * dy);
        dx = dx/distance;
        dy = dy/distance;

        double combinedMass = diameter * diameter;
        double v1 = this.velocityX * dx + this.velocityY * dy;
        double v2 = other.velocityX * dx + other.velocityY * dy;

        double p = 2 * (v1 - v2) / combinedMass;

        this.velocityX = this.velocityX - p * dx;
        this.velocityY = this.velocityY - p * dy;

        other.velocityX = other.velocityX + p * dx;
        other.velocityY = other.velocityY + p * dy;

        double overlap = 0.5 * (diameter - distance);
        this.x -= overlap * dx;
        this.y -= overlap * dy;
        other.x += overlap * dx;
        other.y += overlap * dy;
    }

    // Rastgele bir renk oluşturur
    private Color generateRandomColor() {
        Random rand = new Random();
        int r = rand.nextInt(256);
        int g = rand.nextInt(256);
        int b = rand.nextInt(256);
        return new Color(r, g, b);
    }

    public void draw(Graphics g) {
        g.setColor(color); // Topun rengini ayarlıyoruz
        g.fillOval((int) x, (int) y, diameter, diameter);
    }
}
