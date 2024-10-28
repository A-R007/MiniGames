import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

// Create a rounded button class
class RoundedButton extends JButton {
    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);
        setFont(new Font("Serif", Font.BOLD, 16)); // Fancy font
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Round the button corners
        g.setColor(getBackground());
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        g.setColor(getForeground());
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
    }
}

public class BlackJack {
    private class Card {
        String value;
        String type;

        Card(String value, String type) {
            this.value = value;
            this.type = type;
        }

        public String toString() {
            return value + "-" + type;
        }

        public int getValue() {
            if ("AJQK".contains(value)) {
                if (value.equals("A")) {
                    return 11;
                }
                return 10;
            }
            return Integer.parseInt(value);
        }

        public boolean isAce() {
            return value.equals("A");
        }

        public String getImagePath() {
            return "./cards/" + toString() + ".png";
        }
    }

    private ArrayList<Card> deck;
    private Random random = new Random();
    private Card hiddenCard;
    private ArrayList<Card> dealerHand;
    private int dealerSum;
    private int dealerAceCount;
    private ArrayList<Card> playerHand;
    private int playerSum;
    private int playerAceCount;

    private JFrame frame;
    private JPanel mainPanel, gamePanel, entryPanel, instructionsPanel, buttonPanel;
    private RoundedButton hitButton, stayButton, restartButton, startButton, instructionsButton;
    private JTextArea instructionsText;

    BlackJack() {
        frame = new JFrame("Black Jack");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        createEntryPanel(); // Create entry panel
        frame.add(entryPanel);
        frame.setVisible(true);
    }

    private void createEntryPanel() {
        entryPanel = new JPanel();
        entryPanel.setLayout(new BorderLayout());
        entryPanel.setBackground(new Color(30, 60, 40));

        JLabel titleLabel = new JLabel("Blackjack", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 40)); // Fancier font
        titleLabel.setForeground(Color.WHITE);
        entryPanel.add(titleLabel, BorderLayout.NORTH);

        // Card images for visual appeal
        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(new Color(30, 60, 40));
        cardPanel.setLayout(new FlowLayout());

        for (int i = 1; i <= 3; i++) {
            ImageIcon cardImage = new ImageIcon(getClass().getResource("./cards/start.png")); // Example image
            JLabel cardLabel = new JLabel(cardImage);
            cardPanel.add(cardLabel);
        }
        entryPanel.add(cardPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(30, 60, 40));
        buttonPanel.setLayout(new FlowLayout());

        startButton = new RoundedButton("Start Game");
        instructionsButton = new RoundedButton("Instructions");
        customizeButton(startButton);
        customizeButton(instructionsButton);

        buttonPanel.add(startButton);
        buttonPanel.add(instructionsButton);
        entryPanel.add(buttonPanel, BorderLayout.SOUTH);

        startButton.addActionListener(e -> {
            entryPanel.setVisible(false);
            createGamePanel();
        });

        instructionsButton.addActionListener(e -> {
            entryPanel.setVisible(false);
            createInstructionsPanel();
        });
    }

    private void createInstructionsPanel() {
        instructionsPanel = new JPanel();
        instructionsPanel.setLayout(new BorderLayout());
        instructionsPanel.setBackground(new Color(30, 60, 40));

        JLabel titleLabel = new JLabel("Instructions", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 30)); // Fancier font
        titleLabel.setForeground(Color.WHITE);
        instructionsPanel.add(titleLabel, BorderLayout.NORTH);

        instructionsText = new JTextArea();
        instructionsText.setEditable(false);
        instructionsText.setText("Instructions:\n"
                + "1. The goal is to get as close to 21 without going over.\n"
                + "2. Aces can be worth 1 or 11 points.\n"
                + "3. Face cards (K, Q, J) are worth 10 points.\n"
                + "4. Players can 'Hit' to take another card or 'Stay' to keep their hand.\n"
                + "Fun Fact: The game of Blackjack originated in France in the 1700s.\n");
        instructionsText.setFont(new Font("Arial", Font.PLAIN, 18));
        instructionsText.setBackground(new Color(30, 60, 40));
        instructionsText.setForeground(Color.WHITE);
        instructionsText.setCaretColor(Color.WHITE);
        instructionsPanel.add(instructionsText, BorderLayout.CENTER);

        RoundedButton backButton = new RoundedButton("Back");
        customizeButton(backButton);
        backButton.addActionListener(e -> {
            instructionsPanel.setVisible(false);
            entryPanel.setVisible(true);
        });
        instructionsPanel.add(backButton, BorderLayout.SOUTH);

        frame.add(instructionsPanel);
        instructionsPanel.setVisible(true);
    }

    private void createGamePanel() {
        gamePanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGame(g);
            }
        };
        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(53, 101, 77));

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        hitButton = new RoundedButton("Hit");
        stayButton = new RoundedButton("Stay");
        restartButton = new RoundedButton("Restart");
        customizeButton(hitButton);
        customizeButton(stayButton);
        customizeButton(restartButton);
        restartButton.setVisible(false);

        buttonPanel.add(hitButton);
        buttonPanel.add(stayButton);
        buttonPanel.add(restartButton);

        hitButton.addActionListener(e -> playerHit());
        stayButton.addActionListener(e -> dealerPlay());
        restartButton.addActionListener(e -> restartGame());

        gamePanel.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(gamePanel);
        startGame();
    }

    private void customizeButton(RoundedButton button) {
        button.setPreferredSize(new Dimension(150, 50));
        button.setBackground(Color.LIGHT_GRAY);
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(Color.DARK_GRAY);
                button.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.LIGHT_GRAY);
                button.setForeground(Color.BLACK);
            }
        });
    }

    private void drawGame(Graphics g) {
        try {
            // Draw hidden card
            Image hiddenCardImg = new ImageIcon(getClass().getResource("./cards/BACK.png")).getImage();
            if (!stayButton.isEnabled()) {
                hiddenCardImg = new ImageIcon(getClass().getResource(hiddenCard.getImagePath())).getImage();
            }
            g.drawImage(hiddenCardImg, 20, 20, 110, 154, null);

            // Draw dealer's hand cards
            for (int i = 0; i < dealerHand.size(); i++) {
                Card card = dealerHand.get(i);
                Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                g.drawImage(cardImg, 135 + (115 * i), 20, 110, 154, null);
            }

            // Draw player's hand cards
            for (int i = 0; i < playerHand.size(); i++) {
                Card card = playerHand.get(i);
                Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                g.drawImage(cardImg, 20 + (115 * i), 500, 110, 154, null);
            }

            if (!stayButton.isEnabled()) {
                dealerSum = reduceDealerAce();
                playerSum = reducePlayerAce();
                String message = "";
                if (playerSum > 21) {
                    message = "You Lose!";
                } else if (dealerSum > 21) {
                    message = "You Win!";
                } else if (playerSum == dealerSum) {
                    message = "Tie!";
                } else if (playerSum > dealerSum) {
                    message = "You Win!";
                } else {
                    message = "You Lose!";
                }

                g.setFont(new Font("Serif", Font.PLAIN, 30)); // Fancier font
                g.setColor(Color.WHITE);
                g.drawString(message, 220, 250);
                restartButton.setVisible(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startGame() {
        buildDeck();
        shuffleDeck();
        resetGame();
    }

    private void resetGame() {
        dealerHand = new ArrayList<>();
        dealerSum = 0;
        dealerAceCount = 0;
        playerHand = new ArrayList<>();
        playerSum = 0;
        playerAceCount = 0;

        hiddenCard = deck.remove(deck.size() - 1);
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;
        dealerHand.add(deck.remove(deck.size() - 1));
        for (int i = 0; i < 2; i++) {
            playerHand.add(deck.remove(deck.size() - 1));
            playerSum += playerHand.get(i).getValue();
            playerAceCount += playerHand.get(i).isAce() ? 1 : 0;
        }

        hitButton.setEnabled(true);
        stayButton.setEnabled(true);
        restartButton.setVisible(false);
        gamePanel.repaint();
    }

    private void playerHit() {
        Card card = deck.remove(deck.size() - 1);
        playerSum += card.getValue();
        playerAceCount += card.isAce() ? 1 : 0;
        playerHand.add(card);
        if (reducePlayerAce() > 21) {
            hitButton.setEnabled(false);
        }
        gamePanel.repaint();
    }

    private void dealerPlay() {
        hitButton.setEnabled(false);
        stayButton.setEnabled(false);
        while (dealerSum < 17) {
            Card card = deck.remove(deck.size() - 1);
            dealerSum += card.getValue();
            dealerAceCount += card.isAce() ? 1 : 0;
            dealerHand.add(card);
        }
        gamePanel.repaint();
    }

    private void restartGame() {
        resetGame();
        gamePanel.repaint();
    }

    public void buildDeck() {
        deck = new ArrayList<>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] types = {"C", "D", "H", "S"};

        for (String type : types) {
            for (String value : values) {
                deck.add(new Card(value, type));
            }
        }
    }

    public void shuffleDeck() {
        for (int i = 0; i < deck.size(); i++) {
            int j = random.nextInt(deck.size());
            Card currCard = deck.get(i);
            deck.set(i, deck.get(j));
            deck.set(j, currCard);
        }
    }

    public int reducePlayerAce() {
        while (playerSum > 21 && playerAceCount > 0) {
            playerSum -= 10;
            playerAceCount--;
        }
        return playerSum;
    }

    public int reduceDealerAce() {
        while (dealerSum > 21 && dealerAceCount > 0) {
            dealerSum -= 10;
            dealerAceCount--;
        }
        return dealerSum;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BlackJack::new);
    }
}
