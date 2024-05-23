package src;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class InjuryReserveManagement extends JFrame implements ActionListener {
    private JTextField playerNameField, injuryField;
    private JButton addButton, removeButton;
    private JTextArea displayArea;
    private ArrayList<String> injuredPlayers;

    public InjuryReserveManagement(){
        initialize();
    }

    public void initialize() {
        setTitle("Injury Reserve Queue");
        setSize(1000, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        injuredPlayers = new ArrayList<>();

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Player Name:"));
        playerNameField = new JTextField();
        inputPanel.add(playerNameField);
        inputPanel.add(new JLabel("Injury:"));
        injuryField = new JTextField();
        inputPanel.add(injuryField);

        addButton = new JButton("Add to Injury Reserve");
        addButton.addActionListener(this);
        inputPanel.add(addButton);

        removeButton = new JButton("Remove from Injury Reserve");
        removeButton.addActionListener(this);
        inputPanel.add(removeButton);

        
        JPanel displayPanel = new JPanel(new BorderLayout());
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        displayPanel.add(scrollPane, BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(inputPanel, BorderLayout.NORTH);
        getContentPane().add(displayPanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            String playerName = playerNameField.getText();
            String injury = injuryField.getText();
            addPlayerToInjuryReserve(playerName, injury);
        } else if (e.getSource() == removeButton) {
            String playerName = playerNameField.getText();
            removePlayerFromInjuryReserve(playerName);
        }
    }

    private void addPlayerToInjuryReserve(String playerName, String injury) {
        injuredPlayers.add(playerName);
        displayArea.append("-- Adding Player to Injury Reserve --\n");
        displayArea.append("Player: " + playerName + "\n");
        displayArea.append("Injury: " + injury + "\n");
        displayArea.append("Status: Added to Injury Reserve\n\n");
        clearInputFields();
    }

    private void removePlayerFromInjuryReserve(String playerName) {
        if (injuredPlayers.contains(playerName)) {
            injuredPlayers.remove(playerName);
            displayArea.append("-- Removing Player from Injury Reserve --\n");
            displayArea.append("Player: " + playerName + "\n");
            displayArea.append("Status: Cleared to Play\n\n");
        } else {
            JOptionPane.showMessageDialog(this, "Player not found in the injury reserve list.");
        }
        clearInputFields();
    }

    private void clearInputFields() {
        playerNameField.setText("");
        injuryField.setText("");
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InjuryReserveManagement managementSystem = new InjuryReserveManagement();
            managementSystem.setVisible(true);
        });
    }
}
