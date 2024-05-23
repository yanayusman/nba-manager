import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Players extends JFrame {
    final private Font font = new Font("Arial", Font.BOLD, 15);             // to customize font
    final private Dimension dimension = new Dimension(150, 50);
    final private Dimension maxSize = new Dimension(600, 300);
    final private Dimension minSize = new Dimension(400, 100);

    private JPanel navigationPanel, sidebarPanel, contentPanel;
    private JTextField heightField, weightField, positionField;
    private DefaultListModel<String> playersFoundListModel;
    private JList<String> playersFoundList, playersDetails;

    public Players(){
        initialize();
    }

    public void initialize() {
        setTitle("Player Dynamic Search");                                               //name for every page
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        /**
         * NAVIGATION PANEL
         */
        navigationPanel = new JPanel();
        navigationPanel.setBackground(Color.LIGHT_GRAY);
        navigationPanel.setPreferredSize(new Dimension(1200, 50));

        JLabel titleLabel = new JLabel("NBA Manager Game");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        navigationPanel.add(titleLabel);

        /**
         * SIDEBAR PANEL
         */
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(Color.DARK_GRAY);
        sidebarPanel.setPreferredSize(new Dimension(200, 750));
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
        
        /**
         * CONTENT PANEL
         */
        contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new FlowLayout());
        contentPanel.setPreferredSize(new Dimension(50, 500));

        /**
         * INPUT PANEL
         */
        JPanel inputPanel = new JPanel();

        //Height Search Input Bar   
        JLabel heightLabel = new JLabel("Height(feet)[?-?]: ");
        heightLabel.setFont(new Font("Arial", Font.PLAIN, 17));
        inputPanel.add(heightLabel);
        heightField = new JTextField();
        heightField.setPreferredSize(new Dimension(100, 30));
        heightField.setFont(new Font("Arial", Font.PLAIN, 17));
        inputPanel.add(heightField);

        //Weight Search Input Bar
        JLabel weightLabel = new JLabel("Weight(lbs): ");
        weightLabel.setFont(new Font("Arial", Font.PLAIN, 17));
        inputPanel.add(weightLabel);
        weightField = new JTextField();
        weightField.setPreferredSize(new Dimension(100, 30));
        weightField.setFont(new Font("Arial", Font.PLAIN, 17));
        inputPanel.add(weightField);

        //Position Search Input Bar
        JLabel positionLabel = new JLabel("Position: ");
        positionLabel.setFont(new Font("Arial", Font.PLAIN, 17));
        inputPanel.add(positionLabel);
        positionField = new JTextField();
        positionField.setPreferredSize(new Dimension(100, 30));
        positionField.setFont(new Font("Arial", Font.PLAIN, 17));
        inputPanel.add(positionField);

        //Search Button
        JButton searchButton = new JButton("SEARCH");
        searchButton.setFont(font);
        searchButton.setPreferredSize(new Dimension(125, 45));
        searchButton.addActionListener(e -> loadPlayersFound());

        JPanel searchButtonPanel = new JPanel();
        searchButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        searchButtonPanel.add(searchButton);
        inputPanel.add(searchButtonPanel, BorderLayout.SOUTH);

        /**
         * LIST PANEL
         */
        JPanel listPanel = new JPanel(new GridLayout(1, 2, 10, 50));

        // JList  
        playersFoundListModel = new DefaultListModel<>();
        playersFoundList = new JList<>(playersFoundListModel);
        playersFoundList.setPreferredSize(new Dimension(700, 500));
        JScrollPane scrollPane = new JScrollPane(playersFoundList);
        scrollPane.setPreferredSize(new Dimension(600, 500)); 
        JPanel playersFoundPanel = new JPanel(new BorderLayout());
        playersFoundPanel.setBorder(new TitledBorder("Match(es) Found: "));
        playersFoundPanel.add(scrollPane, BorderLayout.CENTER); 
        loadPlayersFound();
        listPanel.add(playersFoundPanel);

        // Bottom Panel
        JButton addButton = new JButton("ADD TO TEAM");
        addButton.setFont(font);
        addButton.setPreferredSize(new Dimension(150, 45));
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addButton);

        JPanel bottomPanel = new JPanel(new BorderLayout()); 
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Content Panel
        contentPanel.add(inputPanel, BorderLayout.NORTH);
        contentPanel.add(listPanel, BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Main Layout
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(navigationPanel, BorderLayout.NORTH);
        getContentPane().add(sidebarPanel, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
  
        setVisible(true);
    }

    private void loadPlayersFound() {
        String jdbcUrl = "jdbc:mysql://localhost:3306/nbamanager";
        String username = "useract";
        String password = "welcome1";
    
        String height = heightField.getText();
        String weight = weightField.getText() + " lbs";
        String position = positionField.getText();
    
        String sql = "SELECT * FROM players_profile WHERE height > ? AND weight < ? AND position LIKE ?";
    
        try (Connection con = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement pstmt = con.prepareStatement(sql)) {
    
            pstmt.setString(1, height);
            pstmt.setString(2, weight);
            pstmt.setString(3, position);
    
            try (ResultSet rs = pstmt.executeQuery()) {
                playersFoundListModel.clear(); 
                while (rs.next()) {
                    String playername = rs.getString("name");
                    String team = rs.getString("team");
                    String number = rs.getString("number");
                    String lastAttended = rs.getString("last_attended");
                    String country = rs.getString("country");
                    playersFoundListModel.addElement(playername);
                }
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }    

    private void handleSidebarButtonClick(String label) {
        this.dispose();
        switch (label) {
            case "HOME":
                new Home();
                break;
            case "TEAM":
                new Temp();
                break;
            case "PLAYER":
                new Players(); 
                break;
            case "JOURNEY":
                new Temp();
                break;
            case "CONTRACT":
                new Contract();
                break;
            case "INJURY":
                new Injury();
                break;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Players();
        });
    }
}