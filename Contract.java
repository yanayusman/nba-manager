package test;

/**
 *
 * @author Chanteq Demo
 */
import java.sql.Date;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class Contract extends JFrame {
    final private Font font = new Font("Arial", Font.BOLD, 15);
    final private Dimension dimension = new Dimension(150, 50);
    final private Dimension maxSize = new Dimension(600, 300);
    final private Dimension minSize = new Dimension(400, 100);

    private JPanel navigationPanel, sidebarPanel, contentPanel;
    private JComboBox<String> playerField;
    private DefaultListModel<String> contractListModel, renewedListModel;
    private JList<String> contractList, renewedList;
    private PriorityQueue<Player> contractQueue;
    private Queue<Player> renewedQueue;

    public Contract() {
        initialize();
    }

    public void initialize() {
        setTitle("Contract Extension");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // navigation Panel
        navigationPanel = new JPanel();
        navigationPanel.setBackground(Color.LIGHT_GRAY);
        navigationPanel.setPreferredSize(new Dimension(1200, 50));

        JLabel titleLabel = new JLabel("NBA Manager Game");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        navigationPanel.add(titleLabel);

        // sidebar Panel
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

        // content Panel
        contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setPreferredSize(new Dimension(50, 500));

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        JLabel playerNameLabel = new JLabel("       Player Name: ");
        playerNameLabel.setFont(new Font("Arial", Font.PLAIN, 17));
        inputPanel.add(playerNameLabel);
        playerField = new JComboBox<>();
        playerField.setPreferredSize(new Dimension(100, 30));
        loadPlayerName();
        inputPanel.add(playerField);

        JButton addButton = new JButton("ADD");
        addButton.setFont(font);
        addButton.setPreferredSize(new Dimension(100, 35));
        addButton.addActionListener(e -> addToContractList());

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        btnPanel.add(addButton);
        inputPanel.add(btnPanel);

        contractListModel = new DefaultListModel<>();
        renewedListModel = new DefaultListModel<>();
        contractList = new JList<>(contractListModel);
        renewedList = new JList<>(renewedListModel);
        // Initialize the contractQueue with a custom comparator
        contractQueue = new PriorityQueue<>((p1, p2) -> {
            // Compare players based on efficiency first
            int efficiencyComparison = Integer.compare(p2.getEfficiency(), p1.getEfficiency());
            // If efficiencies are the same, compare based on contract expiry date
            if (efficiencyComparison == 0) {
                return p1.getExpiryDate().compareTo(p2.getExpiryDate());
            }
            return efficiencyComparison;
        });
        renewedQueue = new LinkedList<>();

        contractList.setPreferredSize(new Dimension(50, 200));
        renewedList.setPreferredSize(new Dimension(400, 500));

        JPanel contractPanel = new JPanel(new BorderLayout());
        contractPanel.setBorder(new TitledBorder("Contract Extension Queue"));
        contractPanel.add(new JScrollPane(contractList), BorderLayout.CENTER);

        JPanel renewedPanel = new JPanel(new BorderLayout());
        renewedPanel.setBorder(new TitledBorder("Renewed Contract Queue"));
        renewedPanel.add(new JScrollPane(renewedList), BorderLayout.CENTER);

        JPanel listPanel = new JPanel(new GridLayout(1, 2, 10, 50));
        listPanel.add(contractPanel);
        listPanel.add(renewedPanel);

        JButton rmvButton = new JButton("REMOVE");
        rmvButton.setFont(font);
        rmvButton.setPreferredSize(new Dimension(110, 45));
        rmvButton.addActionListener(e -> rmvFromContractList());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(rmvButton);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        contentPanel.add(inputPanel, BorderLayout.NORTH);
        contentPanel.add(listPanel, BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(navigationPanel, BorderLayout.NORTH);
        getContentPane().add(sidebarPanel, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void loadPlayerName() {
        String jdbcUrl = "jdbc:mysql://localhost:3306/test";
        String username = "root";
        String password = "";

        try (Connection con = DriverManager.getConnection(jdbcUrl, username, password)) {
            String sql = "SELECT name FROM players_stat_23_24";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                String playername = rs.getString("name");
                playerField.addItem(playername);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to add selected player to the contract list
    private void addToContractList() {
        String playername = (String) playerField.getSelectedItem();

        if (playername != null) {
            Player player = fetchPlayerDetails(playername);
            if (player != null) {
                // Add player to the priority queue
                contractQueue.add(player);
                contractListModel.addElement(player.toString());
                JOptionPane.showMessageDialog(this, "Player \"" + playername + "\" added to Contract Queue.");
            } else {
                JOptionPane.showMessageDialog(this, "Player details could not be fetched.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a player.");
        }
    }

    // Method to fetch player details from database
    private Player fetchPlayerDetails(String playername) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/test";
        String username = "root";
        String password = "";

        try (Connection con = DriverManager.getConnection(jdbcUrl, username, password)) {
            String sql = "SELECT eff, contract_expiration_date FROM players_stat_23_24 WHERE name = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, playername);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                int efficiency = rs.getInt("eff");
                Date expiryDate = rs.getDate("contract_expiration_date");
                return new Player(playername, efficiency, expiryDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method to remove a player from the contract list and add to the renewed list
    private void rmvFromContractList() {
        // Remove the player with the highest priority (highest efficiency and earliest contract expiry date)
        Player removedPlayer = contractQueue.poll();

        if (removedPlayer != null) {
            contractListModel.removeElement(removedPlayer.toString());
            extendContractExpirationDate(removedPlayer);
            renewedQueue.add(removedPlayer);
            renewedListModel.addElement(removedPlayer.toString());
            JOptionPane.showMessageDialog(this, "Player \"" + removedPlayer.getName() + "\" has been moved to the Renewed Contract Queue.");
        } else {
            JOptionPane.showMessageDialog(this, "Contract Queue is empty.");
        }
    }

    // Method to extend the contract expiration date of a player
    private void extendContractExpirationDate(Player player) {
        // Prompt the user to enter a new expiration date
        String newDateString = JOptionPane.showInputDialog(this, "Enter new contract expiration date (YYYY-MM-DD):");
        if (newDateString != null && !newDateString.trim().isEmpty()) {
            try {
                Date newExpiryDate = Date.valueOf(newDateString);
                player.setExpiryDate(newExpiryDate);

                // Update the database with the new expiration date
                String jdbcUrl = "jdbc:mysql://localhost:3306/test";
                String username = "root";
                String password = "";

                try (Connection con = DriverManager.getConnection(jdbcUrl, username, password)) {
                    String sql = "UPDATE players_stat_23_24 SET contract_expiration_date = ? WHERE name = ?";
                    PreparedStatement preparedStatement = con.prepareStatement(sql);
                    preparedStatement.setDate(1, newExpiryDate);
                    preparedStatement.setString(2, player.getName());
                    preparedStatement.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Contract expiration date for player \"" + player.getName() + "\" has been extended to " + newExpiryDate + ".");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please enter the date in YYYY-MM-DD format.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No date entered. Contract extension canceled.");
        }
    }

    private void handleSidebarButtonClick(String label) {
        // Close current frame before opening a new one if necessary
        this.dispose();
        switch (label) {
            case "HOME":
                new Home();
                break;
            case "TEAM":
            case "PLAYER":
            case "JOURNEY":
            case "CONTRACT":
            case "INJURY":
                new Temp();
                break;
        }
    }

    class Player {
        private String name;
        private int efficiency;
        private Date expiryDate;

        public Player(String name, int efficiency, Date expiryDate) {
            this.name = name;
            this.efficiency = efficiency;
            this.expiryDate = expiryDate;
        }

        public String getName() {
            return name;
        }

        public int getEfficiency() {
            return efficiency;
        }

        public Date getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(Date expiryDate) {
            this.expiryDate = expiryDate;
        }

        @Override
        public String toString() {
            return String.format("Player: %-20s | Efficiency: %-5d | Expiry: %s", name, efficiency, expiryDate.toString());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Contract();
        });
    }
}
