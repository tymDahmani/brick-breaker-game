package test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class full_code {
    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }
}

class Game {
    private JFrame frame;
    private GamePanel panel;

    public Game() {
        frame = new JFrame("brick breaker");
        panel = new GamePanel();
        frame.add(panel);
        frame.setSize(Constants.width, Constants.height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.addKeyListener(panel); // Add the KeyListener to the frame
    }

    public void start() {
        Thread gameThread = new Thread(panel); // Run the game loop in a separate thread
        gameThread.start();
    }
}

class Brick {
    private int x, y, width, height;
    private boolean destroyed;

    public Brick(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.destroyed = false;
    }

    public void draw(Graphics g) {
        if (!destroyed) {
            g.setColor(Color.gray);
            g.fillRect(x, y, width, height);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }
}

class Paddle {
    private int x, y, width, height;
    private int dx;

    public Paddle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.dx = 0;
    }

    public void move() {
        x += dx;
        if (x < 0)
            x = 0;
        if (x + width > Constants.width)
            x = Constants.width - width;
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, width, height);
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}

class Ball {
    private int x, y, diameter;
    private int dx, dy;

    public Ball(int x, int y, int diameter) {
        this.x = x;
        this.y = y;
        this.diameter = diameter;
        this.dx = 2;
        this.dy = -2;
    }

    public void move() {
        x += dx;
        y += dy;

        if (x < 0 || x + diameter > Constants.width)
            dx = -dx;
        if (y < 0)
            dy = -dy;
    }

    public void draw(Graphics g) {
        g.setColor(Color.MAGENTA);
        g.fillOval(x, y, diameter, diameter);
    }

    public void reverseYDirection() {
        dy = -dy;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, diameter, diameter);
    }
}

class GamePanel extends JPanel implements Runnable, KeyListener {
    Ball ball;
    Paddle paddle;
    Brick[] bricks;

    public GamePanel() {
        ball = new Ball(150, 200, 20);
        paddle = new Paddle(150, 450, 100, 20);
        bricks = new Brick[30];

        // Initialize bricks with some positions and sizes
        for (int i = 0; i < bricks.length; i++) {
            bricks[i] = new Brick(50 + (i % 10) * 70, 50 + (i / 10) * 30, 60, 20);
        }

        addKeyListener(this);
        setFocusable(true);
        setBackground(Color.BLACK);
    }

    @Override
    public void run() {
        boolean OpModeIsActive = true;
        while (OpModeIsActive) {
            update();
            repaint();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        ball.move();
        paddle.move();
        checkCollisions();
    }

    private void checkCollisions() {
        // Get the bounding rectangles of the ball and paddle
        Rectangle ballRect = ball.getBounds();
        Rectangle paddleRect = paddle.getBounds();

        // Check for collision between ball and paddle
        if (ballRect.intersects(paddleRect)) {
            // Collision detected, reverse the ball's y-direction
            ball.reverseYDirection();
        }

        // Check for collision between ball and bricks
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && ballRect.intersects(brick.getBounds())) {
                brick.setDestroyed(true);
                ball.reverseYDirection();
                break;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ball.draw(g);
        paddle.draw(g);
        for (Brick brick : bricks) {
            brick.draw(g);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            paddle.setDx(-5);
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            paddle.setDx(5);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        paddle.setDx(0);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}

class Constants {
    public static int width = 800;
    public static final int height = 600;
}