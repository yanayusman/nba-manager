import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class Injury extends JFrame {
    final private Font font = new Font("Arial", Font.BOLD, 15);
    final private Dimension dimension = new Dimension(150, 50);
    final private Dimension maxSize = new Dimension(600, 300);
    final private Dimension minSize = new Dimension(400, 100);

    private JPanel navigationPanel, sidebarPanel, contentPanel;
    private JTextField injuryField;
    private JComboBox<String> playerField;
    private DefaultTableModel injuryTableModel, recoveredTableModel;
    private JTable injuryTable, recoveredTable;
    private Stack<String> injuryStack, tempStack, recoveredStack;

    private final String jdbcUrl = "jdbc:mysql://localhost:3306/nbamanager";
    private final String username = "useract";
    private final String password = "welcome1";

    public Injury() {
        initialize();
        loadInjuryData(); // Load initial injury data
        loadRecoveredData(); // Load initial recovered data
    }

    public void initialize() {
        setTitle("Injury Reserve");
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

        // content Panel
        contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setPreferredSize(new Dimension(50, 500));

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 5));
        JLabel playerNameLabel = new JLabel("       Player Name: ");
        playerNameLabel.setFont(new Font("Arial", Font.PLAIN, 17));
        inputPanel.add(playerNameLabel);
        playerField = new JComboBox<>();
        playerField.setPreferredSize(new Dimension(100, 30));
        loadPlayerName(); // Load player names into the combo box
        inputPanel.add(playerField);

        JLabel injuryLabel = new JLabel("       Injury: ");
        injuryLabel.setFont(new Font("Arial", Font.PLAIN, 17));
        inputPanel.add(injuryLabel);
        injuryField = new JTextField();
        injuryField.setPreferredSize(new Dimension(100, 30));
        injuryField.setFont(new Font("Arial", Font.PLAIN, 17));
        inputPanel.add(injuryField);

        JButton addButton = new JButton("ADD");
        addButton.setFont(font);
        addButton.setPreferredSize(new Dimension(100, 35));
        addButton.addActionListener(e -> addToInjuryList());

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        btnPanel.add(addButton);
        inputPanel.add(btnPanel);

        String[] columnNames = {"Player Name", "Injury"};

        injuryTableModel = new DefaultTableModel(columnNames, 0);
        recoveredTableModel = new DefaultTableModel(columnNames, 0);
        injuryTable = new JTable(injuryTableModel);
        recoveredTable = new JTable(recoveredTableModel);
        injuryStack = new Stack<>();
        tempStack = new Stack<>();
        recoveredStack = new Stack<>();

        injuryTable.setPreferredSize(new Dimension(50, 200));
        recoveredTable.setPreferredSize(new Dimension(400, 500));

        JPanel injuryPanel = new JPanel(new BorderLayout());
        injuryPanel.setBorder(new TitledBorder("Injury Reserve Stack"));
        injuryPanel.add(new JScrollPane(injuryTable), BorderLayout.CENTER);

        JPanel recoveredPanel = new JPanel(new BorderLayout());
        recoveredPanel.setBorder(new TitledBorder("Recovered Stack"));
        recoveredPanel.add(new JScrollPane(recoveredTable), BorderLayout.CENTER);

        JPanel listPanel = new JPanel(new GridLayout(1, 2, 10, 50));
        listPanel.add(injuryPanel);
        listPanel.add(recoveredPanel);

        JButton rmvButton = new JButton("REMOVE");
        rmvButton.setFont(font);
        rmvButton.setPreferredSize(new Dimension(110, 45));
        rmvButton.addActionListener(e -> rmvFromInjuryList());

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

        /* 
     * Method to load player names into the combo box.
     * It fetches player names from the database table 'team_players'
     * excluding those already in the injuryStack table with status = 1 (injured).
     */
    private void loadPlayerName() {
        playerField.removeAllItems();

        try (Connection con = DriverManager.getConnection(jdbcUrl, username, password)) {
            String sql = "SELECT name FROM team_players WHERE name NOT IN (SELECT name FROM injuryStack WHERE status = 1)";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                String playername = rs.getString("name");
                playerField.addItem(playername);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        playerField.revalidate();
        playerField.repaint();
    }

    /*
     * Method to add a player to the injury list.
     * It retrieves the selected player name from the combo box
     * and the injury description from the text field.
     * Then it adds the player to the injuryStack and updates the GUI and database accordingly.
     */
    private void addToInjuryList() {
        String playername = (String) playerField.getSelectedItem();
        String injury = injuryField.getText();

        if(injury == null || injury.trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "Please enter a valid injury.");
            return;
        }

        if (playername != null && !injury.isEmpty()) {
            String entry = String.format("Player: %-20s|           Injury: %-20s", playername, injury);
            injuryStack.push(entry);
            injuryTableModel.addRow(new Object[]{playername, injury});
            addToDatabase(playername, injury, 1); // Add to database with status 1 (injured)
        }
        JOptionPane.showMessageDialog(this, "Player \"" + playername + "\" added to Injury Reserve.");
        injuryField.setText("");

        loadPlayerName(); // Reload player names
    }

    /* 
     * Method to add injury data to the database.
     * It inserts a new row into the 'injuryStack' table with player name, injury, and status.
     */
    private void addToDatabase(String playername, String injury, int status) {
        try (Connection con = DriverManager.getConnection(jdbcUrl, username, password)) {
            String sql = "INSERT INTO injuryStack (name, injury, status) VALUES (?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, playername);
            pstmt.setString(2, injury);
            pstmt.setInt(3, status); // Status 1 indicates player is injured
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* 
     * Method to remove a player from the injury list.
     * It removes the player from the injuryStack and updates the GUI and database accordingly.
     */
    private void rmvFromInjuryList() {
        // Transfer elements from injuryStack to tempStack to reverse order
        while (!injuryStack.isEmpty()) {
            tempStack.push(injuryStack.pop());
        }

        // Pop the top element from tempStack (the first element added to injuryStack)
        if (!tempStack.isEmpty()) {
            String removedPlayer = tempStack.pop();
            String[] parts = removedPlayer.split("\\|\\s+");
            String playername = parts[0].split(":\\s+")[1].trim();
            String injury = parts[1].split(":\\s+")[1].trim();

            // Remove the first occurrence of the player from the injury table
            for (int i = 0; i < injuryTableModel.getRowCount(); i++) {
                if (injuryTableModel.getValueAt(i, 0).equals(playername) && injuryTableModel.getValueAt(i, 1).equals(injury)) {
                    injuryTableModel.removeRow(i);
                    break;
                }
            }

            recoveredStack.push(removedPlayer);
            recoveredTableModel.addRow(new Object[]{playername, injury});
            JOptionPane.showMessageDialog(this, "Player \"" + playername + "\" has been cleared to play.");
            updateDatabase(playername, 0); // Update status to 0 (recovered)
        } else {
            JOptionPane.showMessageDialog(this, "No players in the Injury Reserve.");
        }

        // Transfer elements back to injuryStack to maintain the order
        while (!tempStack.isEmpty()) {
            injuryStack.push(tempStack.pop());
        }

        loadPlayerName(); // Reload player names
    }

    /* 
     * Method to update player status in the database.
     * It updates the status of the player in the 'injuryStack' table to indicate recovery.
     */
    private void updateDatabase(String playername, int status) {
        try (Connection con = DriverManager.getConnection(jdbcUrl, username, password)) {
            String sql = "UPDATE injuryStack SET status = ? WHERE name = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, status); // Update status to 0 (recovered)
            pstmt.setString(2, playername);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* 
     * Method to load initial injury data.
     * It retrieves data from the 'injuryStack' table where status is 1 (indicating players are injured).
     */
    private void loadInjuryData() {
        try (Connection con = DriverManager.getConnection(jdbcUrl, username, password)) {
            String sql = "SELECT name, injury FROM injuryStack WHERE status = 1"; // Fetch injured players
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String playername = rs.getString("name");
                String injury = rs.getString("injury");
                injuryStack.push(String.format("Player: %-20s|           Injury: %-20s", playername, injury));
                injuryTableModel.addRow(new Object[]{playername, injury});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* 
     * Method to load initial recovered data.
     * It retrieves data from the 'injuryStack' table where status is 0 (indicating players are recovered).
     */
    private void loadRecoveredData() {
        try (Connection con = DriverManager.getConnection(jdbcUrl, username, password)) {
            String sql = "SELECT name, injury FROM injuryStack WHERE status = 0"; // Fetch recovered players
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String playername = rs.getString("name");
                String injury = rs.getString("injury");
                recoveredStack.push(String.format("Player: %-20s|           Injury: %-20s", playername, injury));
                recoveredTableModel.addRow(new Object[]{playername, injury});
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Injury();
        });
    }
}
