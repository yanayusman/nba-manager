package src;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.*;

public class PlayerDisplayPage extends JFrame implements ActionListener {
    private JComboBox<String> playerListComboBox;
    private JTextArea displayArea;

    private Connection connection;
    private ArrayList<String> players;

    public PlayerDisplayPage() {
        setTitle("Player Display Page");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        players = new ArrayList<>();

        JPanel inputPanel = new JPanel(new GridLayout(2, 1));
        playerListComboBox = new JComboBox<>();
        inputPanel.add(playerListComboBox);

        JButton displayButton = new JButton("Display Player Info");
        displayButton.addActionListener(this);
        inputPanel.add(displayButton);

        JPanel displayPanel = new JPanel(new BorderLayout());
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        displayPanel.add(scrollPane, BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(inputPanel, BorderLayout.NORTH);
        getContentPane().add(displayPanel, BorderLayout.CENTER);

        // connectToDatabase();
        // loadPlayerNames();
    }

    // private void connectToDatabase() {
    //     String url = "jdbc:mysql://localhost:3306/nbamanager";
    //     String username = "admin";
    //     String password = "welcome1";

    //     try {
    //         connection = DriverManager.getConnection(url, username, password);
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //         JOptionPane.showMessageDialog(this, "Failed to connect to the database.");
    //         System.exit(1);
    //     }
    // }

    // private void loadPlayerNames() {
    //     try {
    //         String query = "SELECT name FROM players_profile";
    //         PreparedStatement statement = connection.prepareStatement(query);
    //         ResultSet resultSet = statement.executeQuery();
    //         while (resultSet.next()) {
    //             String playerName = resultSet.getString("name");
    //             players.add(playerName);
    //             playerListComboBox.addItem(playerName);
    //         }
    //         resultSet.close();
    //         statement.close();
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //         JOptionPane.showMessageDialog(this, "Failed to load player names from the database.");
    //     }
    // }

    // private void displayPlayerInfo(String playerName) {
    //     try {
    //         String query = "SELECT * FROM players_profile WHERE name=?";
    //         PreparedStatement statement = connection.prepareStatement(query);
    //         statement.setString(1, playerName);
    //         ResultSet resultSet = statement.executeQuery();
    //         if (resultSet.next()) {
    //             String playerNameResult = resultSet.getString("name");
    //             String playerPosition = resultSet.getString("position");
    //             int playerAge = resultSet.getInt("age");
    //             String playerTeam = resultSet.getString("team");

    //             displayArea.setText("Player Name: " + playerNameResult + "\n");
    //             displayArea.append("Position: " + playerPosition + "\n");
    //             displayArea.append("Age: " + playerAge + "\n");
    //             displayArea.append("Team: " + playerTeam + "\n");
    //         } else {
    //             JOptionPane.showMessageDialog(this, "Player information not found.");
    //         }
    //         resultSet.close();
    //         statement.close();
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //         JOptionPane.showMessageDialog(this, "Failed to display player information.");
    //     }
    // }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Display Player Info")) {
            String selectedPlayer = (String) playerListComboBox.getSelectedItem();
            // displayPlayerInfo(selectedPlayer);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PlayerDisplayPage playerDisplayPage = new PlayerDisplayPage();
            playerDisplayPage.setVisible(true);
        });
    }
}
