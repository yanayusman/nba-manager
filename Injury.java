import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Stack;

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
    private Stack<String> injuryStack, recoveredStack;

    public Injury() {
        initialize();
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

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 5));
        JLabel playerNameLabel = new JLabel("       Player Name: ");
        playerNameLabel.setFont(new Font("Arial", Font.PLAIN, 17));
        inputPanel.add(playerNameLabel);
        playerField = new JComboBox<>();
        playerField.setPreferredSize(new Dimension(100, 30));
        loadPlayerName();
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

        injuryStack = new Stack<>();
        recoveredStack = new Stack<>();

        injuryTableModel = new DefaultTableModel(new String[]{"Player", "Injury"}, 0);
        recoveredTableModel = new DefaultTableModel(new String[]{"Player", "Injury"}, 0);

        injuryTable = new JTable(injuryTableModel);
        recoveredTable = new JTable(recoveredTableModel);

        JPanel injuryPanel = new JPanel(new BorderLayout());
        injuryPanel.setBorder(new TitledBorder("Injury Reserve Queue"));
        injuryPanel.add(new JScrollPane(injuryTable), BorderLayout.CENTER);

        JPanel recoveredPanel = new JPanel(new BorderLayout());
        recoveredPanel.setBorder(new TitledBorder("Recovered Queue"));
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

        loadInjuryData(); // Load the injury data into JTable

        setVisible(true);
    }

    private void loadInjuryData() {
        String jdbcUrl = "jdbc:mysql://localhost:3306/nbamanager";
        String username = "useract";
        String password = "welcome1";
    
        try (Connection con = DriverManager.getConnection(jdbcUrl, username, password)) {
            String sql = "SELECT * FROM injuryStack";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
    
            while (rs.next()) {
                String playername = rs.getString("name");
                String injury = rs.getString("injury");
                int status = rs.getInt("status");
                if(status == 1){
                    injuryStack.push(playername + "|" + injury);
                    injuryTableModel.addRow(new Object[]{playername, injury});
                }else{
                    recoveredStack.push(playername + "|" + injury);
                    recoveredTableModel.addRow(new Object[]{playername, injury});
                } 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadPlayerName() {
        String jdbcUrl = "jdbc:mysql://localhost:3306/nbamanager";
        String username = "useract";
        String password = "welcome1";

        try (Connection con = DriverManager.getConnection(jdbcUrl, username, password)) {
            String sql = "SELECT name FROM team_players WHERE name NOT IN (SELECT name FROM injuryStack)";
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

    private void addToInjuryList() {
        String jdbcUrl = "jdbc:mysql://localhost:3306/nbamanager";
        String username = "useract";
        String password = "welcome1";
        String playername = (String) playerField.getSelectedItem();
        String injury = injuryField.getText();
        
        if(injury == null || injury.trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "Please enter a valid injury.");
            return;
        }
        try(Connection con = DriverManager.getConnection(jdbcUrl, username, password)){
            String sql = "INSERT INTO injuryStack (name, injury, status) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, playername);
            ps.setString(2, injury);
            ps.setInt(3, 1);

            int result = ps.executeUpdate();

            if(result > 0){
                if (playername != null && !injury.isEmpty()) {
                    injuryStack.push(playername + "|" + injury);
                    injuryTableModel.addRow(new Object[]{playername, injury});
                }
                JOptionPane.showMessageDialog(this, "Player \"" + playername + "\" added to Injury Reserve.");
                injuryField.setText("");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private void rmvFromInjuryList() {
        if (!injuryStack.isEmpty()) {
            String[] entry = injuryStack.remove(0).split("\\|");
            recoveredStack.push(entry[0] + "|" + entry[1]);
    
            String playername = entry[0];
    
            String jdbcUrl = "jdbc:mysql://localhost:3306/nbamanager";
            String username = "useract";
            String password = "welcome1";
    
            try (Connection con = DriverManager.getConnection(jdbcUrl, username, password)) {
                String sql = "UPDATE injuryStack SET status = 0 WHERE name = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, playername);
                
                int result = ps.executeUpdate();
                
                if (result > 0) {
                    injuryTableModel.removeRow(0);
                    recoveredTableModel.addRow(new Object[]{entry[0], entry[1]});
                    JOptionPane.showMessageDialog(this, "Player \"" + entry[0] + "\" has been cleared to play.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }    

    private void handleSidebarButtonClick(String label) {
        switch (label) {
            case "HOME":
                new Home();
                break;
            case "TEAM":
                new MyTeam();
                break;
            case "PLAYER":
                new Player_();
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Injury();
        });
    }
}
