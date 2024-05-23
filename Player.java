import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Player  extends JFrame {
    final private Font font = new Font("Arial", Font.BOLD, 30);             // to customize font
    final private Dimension dimension = new Dimension(150, 50);
    final private Dimension maxSize = new Dimension(600, 300);
    final private Dimension minSize = new Dimension(400, 100);

    private JPanel navigationPanel, sidebarPanel, contentPanel;
    private JTextField heightField, weightField, positionField;
    private DefaultTableModel playersFoundTableModel;
    private JTable playersFoundTable;

    public Player(){
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
        JLabel heightLabel = new JLabel("Height: ");
        heightLabel.setFont(new Font("Arial", Font.PLAIN, 17));
        inputPanel.add(heightLabel);
        heightField = new JTextField();
        heightField.setPreferredSize(new Dimension(100, 30));
        heightField.setFont(new Font("Arial", Font.PLAIN, 17));
        inputPanel.add(heightField);

        //Weight Search Input Bar
        JLabel weightLabel = new JLabel("Weight: ");
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
        searchButton.setFont(new Font("Arial", Font.BOLD, 17));
        searchButton.setPreferredSize(new Dimension(125, 45));
        searchButton.addActionListener(e -> loadPlayersTable());

        JPanel searchButtonPanel = new JPanel();
        searchButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        searchButtonPanel.add(searchButton);
        inputPanel.add(searchButtonPanel, BorderLayout.SOUTH);

        contentPanel.add(inputPanel, BorderLayout.NORTH);

        /**
         * TABLE PANEL
         */
        JPanel tablePanel = new JPanel(new BorderLayout());

        //JTable
        loadPlayersTable();
        JPanel playersFoundPanel = new JPanel(new BorderLayout());
        playersFoundPanel.setBorder(new TitledBorder("Match(es) Found: "));
        playersFoundPanel.add(new JScrollPane(playersFoundTable), BorderLayout.CENTER);
        tablePanel.add(playersFoundPanel);

        contentPanel.add(tablePanel, BorderLayout.CENTER);

        /**
         * BOTTOM PANEL
         */
        JPanel bottomPanel = new JPanel(new BorderLayout());

        //Add to Team Button
        JButton addButton = new JButton("ADD TO TEAM");
        addButton.setFont(new Font("Arial", Font.BOLD, 17));
        addButton.setPreferredSize(new Dimension(150, 45));
        //addButton.addActionListener();

        JPanel addButtonPanel = new JPanel();
        addButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        addButtonPanel.add(addButton);
        bottomPanel.add(addButtonPanel, BorderLayout.SOUTH);

        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(navigationPanel, BorderLayout.NORTH);
        getContentPane().add(sidebarPanel, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);                // set content position
   
        setVisible(true);
    }

    private void loadPlayersTable() {
        String jdbcUrl = "jdbc:mysql://localhost:3306/nbamanager";
        String username = "useract";
        String password = "welcome1";

        try (Connection con = DriverManager.getConnection(jdbcUrl, username, password)) {
            String sql = "SELECT * FROM players_profile";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            // Finding total number of rows
            int rows = 0;
            if(rs.next()) {
                rs.last();
                rows = rs.getRow();
                rs.beforeFirst();
            }

            String[][] data = new String[rows][8];
            int i = 0;

            while (rs.next()) {
                data[i][0] = rs.getString(1);
                data[i][1] = rs.getString(2);
                data[i][2] = rs.getString(3);
                data[i][3] = rs.getString(4);
                data[i][4] = rs.getString(5);
                data[i][5] = rs.getString(6);
                data[i][6] = rs.getString(7);
                data[i][7] = rs.getString(8);
                System.out.println(data);
                i++;
            }

            String[] columns = {"Name", "Team", "Number", "Position", "Height", "Weight", "Last Attended", "Country"};
            playersFoundTableModel = new DefaultTableModel(data, columns);
            playersFoundTable = new JTable(playersFoundTableModel);
            playersFoundTable.setModel(playersFoundTableModel);

            rs.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // private void loadPlayersFoundTable() {
    //     String jdbcUrl = "jdbc:mysql://localhost:3306/nbamanager";
    //     String username = "useract";
    //     String password = "welcome1";

    //     String height = heightField.getText();
    //     String weight = weightField.getText() + " lbs";
    //     String position = positionField.getText();

    //     try (Connection con = DriverManager.getConnection(jdbcUrl, username, password)) {
    //         String sql = "SELECT * FROM players_profile WHERE height LIKE " + height + 
    //                         " OR weight LIKE " + weight + " AND position LIKE " + position;
    //         Statement statement = con.createStatement();
    //         ResultSet rs = statement.executeQuery(sql);

    //         while (rs.next()) {
    //             String playername = rs.getString("name");
    //             //playersFoundTableModel.addRow();
    //         }

    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //     }
    // }

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
                new Contract();
                break;
            case "INJURY":
                new Injury();
                break;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Player();
        });
    }
}
