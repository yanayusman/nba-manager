import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.Date;
import java.util.Queue;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class MyContract extends JFrame {
    final private Font font = new Font("Arial", Font.BOLD, 15);
    final private Dimension dimension = new Dimension(150, 50);
    final private Dimension maxSize = new Dimension(600, 300);
    final private Dimension minSize = new Dimension(400, 100);

    private JPanel navigationPanel, sidebarPanel, contentPanel;
    private JComboBox<String> playerField;
    private DefaultTableModel contractTableModel, renewedTableModel;
    private JTable contractTable, renewedTable;
    private PriorityQueue<Player> contractQueue;
    private Queue<Player> renewedQueue;
    private Map<String, Player> playerMap = new HashMap<>();

    public MyContract() {
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

        // Initialize the table models for the contract and renewed lists
        contractTableModel = new DefaultTableModel(new String[]{"Player Name", "Efficiency", "Expiry Date"}, 0);
        renewedTableModel = new DefaultTableModel(new String[]{"Player Name", "Efficiency", "Expiry Date"}, 0);
        contractTable = new JTable(contractTableModel);
        renewedTable = new JTable(renewedTableModel);

        // Center the content in the table cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < contractTable.getColumnCount(); i++) {
            contractTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        for (int i = 0; i < renewedTable.getColumnCount(); i++) {
            renewedTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
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
        playerMap = new HashMap<>(); // Initialize the player map

        contractTable.setPreferredSize(new Dimension(50, 200));
        renewedTable.setPreferredSize(new Dimension(400, 500));

        JPanel contractPanel = new JPanel(new BorderLayout());
        contractPanel.setBorder(new TitledBorder("Contract Extension Queue"));
        contractPanel.add(new JScrollPane(contractTable), BorderLayout.CENTER);

        JPanel renewedPanel = new JPanel(new BorderLayout());
        renewedPanel.setBorder(new TitledBorder("Renewed Contract Queue"));
        renewedPanel.add(new JScrollPane(renewedTable), BorderLayout.CENTER);

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
        String jdbcUrl = "jdbc:mysql://localhost:3306/nbamanager";
        String username = "useract";
        String password = "welcome1";

        try (Connection con = DriverManager.getConnection(jdbcUrl, username, password)) {
            String sql = "SELECT name FROM team_players";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                String playername = rs.getString("name");
                playerField.addItem(playername);
                playerMap.put(playername, null);
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
                contractQueue.add(player);
                contractTableModel.addRow(new Object[]{player.getName(), player.getEfficiency(), player.getExpiryDate()}); // Add player to the contract table model
                playerField.removeItem(playername); // Remove player from the dropdown list
                JOptionPane.showMessageDialog(this, "Player \"" + playername + "\" added to Contract Queue.");
            } else {
                JOptionPane.showMessageDialog(this, "Player details could not be fetched.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a player.");
        }
    }

    // Method to fetch player details from the database
    private Player fetchPlayerDetails(String playername) {
        // Check if the player details are already cached in the map
        if (playerMap.get(playername) != null) {
            return playerMap.get(playername);
        }

        String jdbcUrl = "jdbc:mysql://localhost:3306/nbamanager";
        String username = "useract";
        String password = "welcome1";

        try (Connection con = DriverManager.getConnection(jdbcUrl, username, password)) {
            String sql = "SELECT eff, contract_expiration_date FROM players_stat_23_24 WHERE name = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, playername);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                int efficiency = rs.getInt("eff");
                Date expiryDate = rs.getDate("contract_expiration_date");
                Player player = new Player(playername, efficiency, expiryDate);
                playerMap.put(playername, player);
                return player;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method to remove a player from the contract list and potentially add to the renewed list
    private void rmvFromContractList() {
        Player removedPlayer = contractQueue.poll(); // Remove the player with the highest priority
    
        if (removedPlayer != null) {
            contractTableModel.removeRow(findPlayerIndexInTable(removedPlayer.getName(), contractTableModel));
            playerField.removeItem(removedPlayer.getName());
            boolean extended = extendContractExpirationDate(removedPlayer);
            if (extended) {
                renewedQueue.add(removedPlayer);
                renewedTableModel.addRow(new Object[]{removedPlayer.getName(), removedPlayer.getEfficiency(), removedPlayer.getExpiryDate()});
                JOptionPane.showMessageDialog(this, "Player \"" + removedPlayer.getName() + "\" has been moved to the Renewed Contract Queue.");
            } else {
                contractQueue.add(removedPlayer); // Re-add to the contractQueue if no valid date entered
                contractTableModel.addRow(new Object[]{removedPlayer.getName(), removedPlayer.getEfficiency(), removedPlayer.getExpiryDate()});
            }
        } else {
            JOptionPane.showMessageDialog(this, "Contract Queue is empty.");
        }
    }    


    // Utility method to find a player in the table model
    private int findPlayerIndexInTable(String playerName, DefaultTableModel model) {
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).equals(playerName)) {
                return i;
            }
        }
        return -1; // Player not found
    }

    // Method to extend the contract expiration date of a player
    private boolean extendContractExpirationDate(Player player) {
        // Prompt the user to enter a new expiration date
        String newDateString = JOptionPane.showInputDialog(this, "Enter new contract expiration date (YYYY-MM-DD) for: " + player);
        if (newDateString != null && !newDateString.trim().isEmpty()) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                dateFormat.setLenient(false);
                java.util.Date parsedDate = dateFormat.parse(newDateString);
                java.sql.Date newExpiryDate = new java.sql.Date(parsedDate.getTime());

                // Get the current date and add one year to it
                Calendar currentDate = Calendar.getInstance();
                currentDate.add(Calendar.YEAR, 1);
                java.util.Date oneYearLater = currentDate.getTime();

                if (newExpiryDate.before(oneYearLater)) {
                    JOptionPane.showMessageDialog(this, "The new contract expiration date must be at least one year from today.");
                    return false;
                }

                player.setExpiryDate(newExpiryDate);

                String jdbcUrl = "jdbc:mysql://localhost:3306/nbamanager";
                String username = "useract";
                String password = "welcome1";

                try (Connection con = DriverManager.getConnection(jdbcUrl, username, password)) {
                    String sql = "UPDATE players_stat_23_24 SET contract_expiration_date = ? WHERE name = ?";
                    PreparedStatement preparedStatement = con.prepareStatement(sql);
                    preparedStatement.setDate(1, newExpiryDate);
                    preparedStatement.setString(2, player.getName());
                    preparedStatement.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Contract expiration date for player \"" + player.getName() + "\" has been extended to " + newExpiryDate + ".");
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Failed to update the database. Please try again.");
                    return false; 
                }
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please enter the date in YYYY-MM-DD format.");
                return false; 
            }
        } else {
            JOptionPane.showMessageDialog(this, "No date entered. Player remains in the Contract Extension Queue.");
            return false;
        }
    }

    private void handleSidebarButtonClick(String label) {
        this.dispose();
        switch (label) {
            case "HOME":
                new Home();
                break;
            case "TEAM":
                new MyTeam();
                break;
            case "PLAYER":
                new MyPlayer(); 
                break;
            case "JOURNEY":
                new Temp();
                break;
            case "CONTRACT":
                new MyContract();
                break;
            case "INJURY":
                new Injury();
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
            new MyContract();
        });
    }
}
