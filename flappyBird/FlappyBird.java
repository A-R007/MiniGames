import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    // Bird
    int birdX = boardWidth / 8;
    int birdY = boardWidth / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    // Pipe
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    // Game logic
    Bird bird;
    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;
    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipeTimer;
    boolean gameOver = false;
    double score = 0;

    // Game state control
    boolean showStartPage = true;
    boolean showInstructions = false;
    JButton startButton, instructionButton, retryButton;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        // Bird initialization
        bird = new Bird(birdImg);
        pipes = new ArrayList<>();

        // Start page buttons
        startButton = createButton("START", 100, 250);
        instructionButton = createButton("INSTRUCTIONS", 100, 350);
        retryButton = createButton("RETRY", 130, 400);
        retryButton.setVisible(false);
        add(startButton);
        add(instructionButton);
        add(retryButton);

        // Pipe placement
        placePipeTimer = new Timer(1500, e -> placePipes());

        // Game loop
        gameLoop = new Timer(1000 / 60, this);
    }

    // Helper method for creating buttons with simple style and bold white text
    private JButton createButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 150, 50);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setForeground(Color.white);
        button.setBorder(BorderFactory.createLineBorder(Color.white));
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.addActionListener(e -> handleButtonClick(text));
        return button;
    }

    // Start game
    private void startGame() {
        showStartPage = false;
        showInstructions = false;
        startButton.setVisible(false);
        instructionButton.setVisible(false);
        retryButton.setVisible(false);

        // Reset game state
        bird.y = birdY;
        velocityY = 0;
        pipes.clear();
        score = 0;
        gameOver = false;

        placePipeTimer.start();
        gameLoop.start();
    }

    private void showGameInstructions() {
        showInstructions = true;
        startButton.setVisible(false);
        instructionButton.setVisible(false);
        retryButton.setVisible(false);
        repaint();
    }

    private void handleButtonClick(String buttonText) {
        if (buttonText.equals("START")) {
            startGame();
        } else if (buttonText.equals("INSTRUCTIONS")) {
            showGameInstructions();
        } else if (buttonText.equals("RETRY")) {
            startGame();
        }
    }

    void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, this.boardWidth, this.boardHeight, null);

        if (showStartPage) {
            drawStartPage(g);
        } else if (showInstructions) {
            drawInstructions(g);
        } else {
            drawGame(g);
        }
    }

    private void drawStartPage(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.setColor(Color.white);
        g.drawString("FLAPPY BIRD", 40, 100);
        
        // Draw bird image below the title
        g.drawImage(birdImg, 100, 200, birdWidth * 4, birdHeight * 4, null);
    }

    private void drawInstructions(Graphics g) {
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.setColor(Color.white);
        int y = 150;
        g.drawString("HOW TO PLAY:", 100, y);
        y += 30;
        g.drawString("1. Press the SPACEBAR to make the bird jump.", 30, y);
        y += 30;
        g.drawString("2. Avoid hitting the pipes to stay alive.", 30, y);
        y += 30;
        g.drawString("3. The longer you survive, the higher your score!", 30, y);
        y += 30;
        g.drawString("4. You get 1 point for every set of pipes passed.", 30, y);
        y += 30;
        g.drawString("5. If you hit a pipe, it's GAME OVER!", 30, y);
        y += 50;
        g.drawString("GOOD LUCK!", 130, y);
        y += 30;
        g.drawString("P.S. Don't blame me if you get addicted.", 50, y); // A joke to enhance the experience
    }

    private void drawGame(Graphics g) {
        // Draw bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        // Draw pipes
        for (Pipe pipe : pipes) {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // Score display
        g.setFont(new Font("Arial", Font.BOLD, 32));
        g.setColor(Color.white);
        if (gameOver) {
            g.drawString("GAME OVER", boardWidth / 6, boardHeight / 2 - 50);
            g.drawString("SCORE: " + (int) score, boardWidth / 4, boardHeight / 2);
            retryButton.setVisible(true);
        } else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move() {
        if (gameOver) return;

        // Bird movement
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        // Pipes movement and collision times
        for (Pipe pipe : pipes) {
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                score += 0.5;
                pipe.passed = true;
            }

            if (collision(bird, pipe)) {
                gameOver = true;
                gameLoop.stop();
                placePipeTimer.stop();
            }
        }

        if (bird.y > boardHeight) {
            gameOver = true;
            gameLoop.stop();
            placePipeTimer.stop();
        }
    }

    boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (showStartPage) {
                startGame(); // Start the game on first space bar press
            } else if (!gameOver) {
                velocityY = -9; // Make the bird jump
            }
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    // Main method to set up the JFrame
    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Bird");
        FlappyBird game = new FlappyBird();
        frame.add(game);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
