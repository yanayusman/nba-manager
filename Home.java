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
    private JPanel contentPanel;

    public Home(){
        intialize();
    }

    public void intialize() {
        setTitle("Home");
        setSize(900, 800);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        // Navigation Panel
        navigationPanel = new JPanel();
        navigationPanel.setBackground(Color.LIGHT_GRAY);
        navigationPanel.setPreferredSize(new Dimension(800, 50));

        JLabel imageLabel = loadImage("nba.png", true,700, 300);

        JLabel titleLabel = new JLabel("NBA Manager Game");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        navigationPanel.add(titleLabel);

        

        // Sidebar Panel
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(Color.DARK_GRAY);
        sidebarPanel.setPreferredSize(new Dimension(200, 600));
        String[] buttonLabels = {"HOME", "TEAM", "PLAYER", "JOURNEY", "CONTRACT", "INJURY"};

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

        // Content Panel
        contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new FlowLayout());
        contentPanel.add(imageLabel);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(navigationPanel, BorderLayout.NORTH);
        getContentPane().add(sidebarPanel, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
    }

    private void handleSidebarButtonClick(String label) {
        switch (label) {
            case "HOME":
                new Home();
                break;
            case "TEAM":
                new Temp();
                break;
            case "PLAYER":
                new Temp(); 
                break;
            case "JOURNEY":
                new Temp();
                break;
            case "CONTRACT":
                new Temp();
                break;
            case "INJURY":
                new InjuryReserveManagement();
                break;
        }
    }

    // load image
    private JLabel loadImage(String fileName, boolean isResized, int targetWidth, int targetHeight) {
        BufferedImage image;
        JLabel imageContainer;

        try {
            image = ImageIO.read(new File(fileName));

            if (isResized) {
                image = resizeImage(image, targetWidth, targetHeight);
            }

            imageContainer = new JLabel(new ImageIcon(image));
            return imageContainer;
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
