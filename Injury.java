import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.Queue;

public class Injury extends JFrame {
    final private Font font = new Font("Arial", Font.BOLD, 15);
    final private Dimension dimension = new Dimension(150, 50);
    final private Dimension maxSize = new Dimension(600, 300);
    final private Dimension minSize = new Dimension(400, 100);

    private JPanel navigationPanel, sidebarPanel, contentPanel;
    private JTextField injuryField;
    private JComboBox<String> playerField;
    private DefaultListModel<String> injuryListModel, recoveredListModel;
    private JList<String> injuryList, recoveredList;
    private Queue<String> injuryQueue, recoveredQueue;

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

        injuryListModel = new DefaultListModel<>();
        recoveredListModel = new DefaultListModel<>();
        injuryList = new JList<>(injuryListModel);
        recoveredList = new JList<>(recoveredListModel);
        injuryQueue = new LinkedList<>();
        recoveredQueue = new LinkedList<>();

        injuryList.setPreferredSize(new Dimension(50, 200));
        recoveredList.setPreferredSize(new Dimension(400, 500));

        JPanel injuryPanel = new JPanel(new BorderLayout());
        injuryPanel.setBorder(new TitledBorder("Injury Reserve Queue"));
        injuryPanel.add(new JScrollPane(injuryList), BorderLayout.CENTER);

        JPanel recoveredPanel = new JPanel(new BorderLayout());
        recoveredPanel.setBorder(new TitledBorder("Recovered Queue"));
        recoveredPanel.add(new JScrollPane(recoveredList), BorderLayout.CENTER);

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

    private void loadPlayerName() {
        String jdbcUrl = "jdbc:mysql://localhost:3306/nbamanager";
        String username = "useract";
        String password = "welcome1";

        try (Connection con = DriverManager.getConnection(jdbcUrl, username, password)) {
            String sql = "SELECT name FROM players";
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
        String playername = (String) playerField.getSelectedItem();
        String injury = injuryField.getText();

        if (playername != null && !injury.isEmpty()) {
            // String entry = "Player : " + playername + "         Injury : " + injury;
            String entry = String.format("Player: %-20s|           Injury: %-20s", playername, injury);
            injuryQueue.add(entry);
            injuryListModel.addElement(entry);
        }
        JOptionPane.showMessageDialog(this, "Player \"" + playername + "\" added to Injury Reserve.");
        injuryField.setText("");
    }

    private void rmvFromInjuryList() {
        String removedPlayer = injuryQueue.poll();

        if (removedPlayer != null) {
            injuryListModel.removeElement(removedPlayer);
            recoveredQueue.add(removedPlayer);
            recoveredListModel.addElement(removedPlayer);
        }
        JOptionPane.showMessageDialog(this, "Player \"" + removedPlayer + "\" has been cleared to play.");
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
