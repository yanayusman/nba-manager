import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class Home extends JFrame {
    final private Dimension dimension = new Dimension(150, 50);
    final private Dimension maxSize = new Dimension(600, 300);
    final private Dimension minSize = new Dimension(400, 100);

    private JPanel navigationPanel;
    private JPanel sidebarPanel;
    private BackgroundPanel contentPanel;
    private BufferedImage backgroundImage;
    private BufferedImage nbaImage;

    public Home(){
        initialize();
    }

    public void initialize() {
        setTitle("Home");
        setSize(1200, 800);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        // Load the background image
        try {
            backgroundImage = ImageIO.read(new File("NBA-Logo.png"));
            nbaImage = loadImage("nba.png", true, 550, 250);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // navigation Panel
        navigationPanel = new JPanel();
        navigationPanel.setBackground(Color.LIGHT_GRAY);
        navigationPanel.setPreferredSize(new Dimension(800, 50));

        JLabel titleLabel = new JLabel("NBA Manager Game");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        navigationPanel.add(titleLabel);

        // sidebar Panel
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(Color.DARK_GRAY);
        sidebarPanel.setPreferredSize(new Dimension(200, 600));
        String[] buttonLabels = {"HOME", "TEAM", "PLAYER", "JOURNEY", "INJURY", "CONTRACT"};

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setPreferredSize(dimension);
            button.setMaximumSize(maxSize);
            button.setMinimumSize(minSize);
            button.setBackground(Color.LIGHT_GRAY);
            button.setFont(new Font("Arial", Font.BOLD, 15));
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    handleSidebarButtonClick(label);
                }
            });
            sidebarPanel.add(button);
        }

        // content Panel with background image
        contentPanel = new BackgroundPanel();
        contentPanel.setBackgroundImage(backgroundImage);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Adding NBA image
        JLabel imageLabel = new JLabel(new ImageIcon(nbaImage));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Adding welcome message
        JLabel welcomeLabel = new JLabel("<html>Welcome to the NBA Manager Game!<br>Build and manage your dream team to victory.</html>");
        welcomeLabel.setFont(new Font("Monospaced", Font.BOLD, 25));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Adding descriptive text
        JLabel descriptionLabel = new JLabel("<html>Choose an option from the left sidebar to get started.<br>Manage your team, view player stats, track injuries, and more!</html>");
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Adding a start label
        JLabel startLabel = new JLabel("Get Started!");
        startLabel.setFont(new Font("Arial", Font.BOLD, 18));
        startLabel.setHorizontalAlignment(SwingConstants.CENTER);
        startLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(Box.createVerticalGlue()); 
        contentPanel.add(imageLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20))); 
        contentPanel.add(welcomeLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20))); 
        contentPanel.add(descriptionLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20))); 
        contentPanel.add(startLabel);
        contentPanel.add(Box.createVerticalGlue()); 

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(navigationPanel, BorderLayout.NORTH);
        getContentPane().add(sidebarPanel, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
    }

    // to handle side button when it's clicked
    private void handleSidebarButtonClick(String label) {
        this.dispose();
        switch (label) {
            case "HOME":
                new Home();
                break;
            case "TEAM":
                new Team();
                break;
            case "PLAYER":
                new Players();
                break;
            case "JOURNEY":
                new JourneyGraph();
                break;
            case "INJURY":
                new Injury();
                break;
            case "CONTRACT":
                new Contract();
                break;
        }
    }

    // Custom JPanel to handle background image
    class BackgroundPanel extends JPanel {
        private BufferedImage image;

        public void setBackgroundImage(BufferedImage image) {
            this.image = image;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                Graphics2D g2d = (Graphics2D) g.create();
                int width = getWidth();
                int height = getHeight();
                g2d.drawImage(image, 0, 0, width, height, this);

                // Apply a translucent overlay to fade the image
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                g2d.setColor(getBackground());
                g2d.fillRect(0, 0, width, height);
                g2d.dispose();
            }
        }
    }

    // load image
    private BufferedImage loadImage(String fileName, boolean isResized, int targetWidth, int targetHeight) {
        BufferedImage image;

        try {
            image = ImageIO.read(new File(fileName));

            if (isResized) {
                image = resizeImage(image, targetWidth, targetHeight);
            }

            return image;
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
    }

    // resizing image
    private BufferedImage resizeImage(BufferedImage image, int targetWidth, int targetHeight) {
        BufferedImage resizedImg = new BufferedImage(targetWidth,
                targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImg.createGraphics();
        graphics2D.drawImage(image, 0, 0, targetWidth,
                targetHeight, null);
        graphics2D.dispose();
        return resizedImg;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Home();
        });
    }
}
