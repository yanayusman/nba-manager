import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Player_  extends JFrame {
    final private Font font = new Font("Arial", Font.BOLD, 15);             
    final private Dimension dimension = new Dimension(150, 50);
    final private Dimension maxSize = new Dimension(600, 300);
    final private Dimension minSize = new Dimension(400, 100);

    private JPanel navigationPanel, sidebarPanel, contentPanel;
    private JTextField heightField, weightField, positionField;
    private DefaultTableModel playersFoundTableModel;
    private JTable playersFoundTable;
    private String playerName, team, number, position, height, weight, lastAttended, country;

    public Player_(){
        initialize();
    }

    public void initialize() {
        setTitle("Player Dynamic Search");          
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
        searchButton.addActionListener(e -> loadPlayersFoundTable());

        JPanel searchButtonPanel = new JPanel();
        searchButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        searchButtonPanel.add(searchButton);
        inputPanel.add(searchButtonPanel, BorderLayout.SOUTH);

        /**
         * TABLE PANEL
         */
        JPanel tablePanel = new JPanel(new GridLayout(1, 2, 10, 50));

        //JTable
        JPanel playersFoundPanel = new JPanel(new BorderLayout());
        playersFoundTableModel = new DefaultTableModel();
        String[] columns = {"Name", "Team", "Number", "Position", "Height", "Weight", "Last Attended", "Country", "Add To Team"};
        for(String column: columns) {
            playersFoundTableModel.addColumn(column);
        }
        playersFoundTable = new JTable(playersFoundTableModel);
        playersFoundTable.setPreferredSize(new Dimension(900, 17500));
        playersFoundTable.setRowHeight(30);
        playersFoundPanel.setBorder(new TitledBorder("Match(es) Found: "));
        JScrollPane tableScrollPane = new JScrollPane(playersFoundTable);
        tableScrollPane.setPreferredSize(new Dimension(900, 500));
        playersFoundPanel.add(tableScrollPane, BorderLayout.CENTER);
        loadPlayersTable();
        tablePanel.add(playersFoundPanel);

        /**
         * BOTTOM PANEL
         */
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
        contentPanel.add(tablePanel, BorderLayout.CENTER);
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

            while (rs.next()) {
                playerName = rs.getString("name");
                team = rs.getString("team");
                number = rs.getString("number");
                position = rs.getString("position");
                height = rs.getString("height");
                weight = rs.getString("weight");
                lastAttended = rs.getString("last_attended");
                country = rs.getString("country");
                JButton addButton = new JButton("Add");
                playersFoundTable.getColumnModel().getColumn(8).setCellRenderer(new ButtonRenderer());
                playersFoundTable.getColumnModel().getColumn(8).setCellEditor(new ButtonEditor(new JTextField(), playersFoundTable));
                playersFoundTableModel.addRow(new Object[]{playerName, team, number, position, height, weight, lastAttended, country, addButton});
            }

            rs.close();
            statement.close();  

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadPlayersFoundTable() {
        String jdbcUrl = "jdbc:mysql://localhost:3306/nbamanager";
        String username = "useract";
        String password = "welcome1";

        String heightInput = heightField.getText();
        String weightInput = weightField.getText() + " lbs";
        String positionInput = positionField.getText();

        String sql = "SELECT * FROM players_profile WHERE height > ? AND weight < ? AND position LIKE ?";

        try (Connection con = DriverManager.getConnection(jdbcUrl, username, password);
            PreparedStatement pstmt = con.prepareStatement(sql)) {
    
                pstmt.setString(1, heightInput);
                pstmt.setString(2, weightInput);
                pstmt.setString(3, positionInput);
        
                try (ResultSet rs = pstmt.executeQuery()) {  
                    playersFoundTableModel.setRowCount(0);
                    playersFoundTable.setPreferredSize(new Dimension(900, 1750));

                    while (rs.next()) {
                        playerName = rs.getString("name");
                        team = rs.getString("team");
                        number = rs.getString("number");
                        position = rs.getString("position");
                        height = rs.getString("height");
                        weight = rs.getString("weight");
                        lastAttended = rs.getString("last_attended");
                        country = rs.getString("country");
                        JButton addButton = new JButton("Add");
                        playersFoundTable.getColumnModel().getColumn(8).setCellRenderer(new ButtonRenderer());
                        playersFoundTable.getColumnModel().getColumn(8).setCellEditor(new ButtonEditor(new JTextField(), playersFoundTable));
                        playersFoundTableModel.addRow(new Object[]{playerName, team, number, position, height, weight, lastAttended, country, addButton});
                    }
                }

        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                new Contract();
                break;
            case "INJURY":
                new Injury();
                break;
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Player_();
        });
    }
}


class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object obj, boolean selected, boolean focused, int row, int column) {
        setText("Add");
        return this;
    }
}

class ButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private String label;
    private boolean clicked;
    private int selectedRow;

    public ButtonEditor(JTextField text, JTable table) {
        super(text);
        this.button = new JButton();
        button.setOpaque(true);

        button.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                TableModel tableModel = table.getModel();
                String value = ((DefaultTableModel) tableModel).getDataVector().elementAt(table.convertRowIndexToModel(table.getSelectedRow())).toString();
                addPlayerToTeam(value);
                fireEditingStopped();
            }
        
        });
    }

    private void addPlayerToTeam(String playerInfo) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/nbamanager";
        String username = "useract";
        String password = "welcome1";

        playerInfo = playerInfo.replace("[", "");
        String[] playerInfoArr = playerInfo.split(", ", 9);

        String playerName = playerInfoArr[0];
        String team = playerInfoArr[1];
        String number = playerInfoArr[2];
        String position = playerInfoArr[3];
        String height = playerInfoArr[4];
        String weight = playerInfoArr[5];
        String lastAttended = playerInfoArr[6];
        String country = playerInfoArr[7];

        try (Connection con = DriverManager.getConnection(jdbcUrl, username, password)) {
            String sql = "INSERT INTO team_players (name, team, number, position, height, weight, last_attended, country) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, playerName);
                ps.setString(2, team);
                ps.setString(3, number);
                ps.setString(4, position);
                ps.setString(5, height);
                ps.setString(6, weight);
                ps.setString(7, lastAttended);
                ps.setString(8, country);

                int result = ps.executeUpdate();

                if (result > 0) {
                    System.out.println("Player " + playerName + " added to the team.");
                } else {
                    //JOptionPane.showMessageDialog(this, "Failed to add player to the team. Please try again.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean selected, int row, int column) {

        label = "Add";
        button.setText(label);
        clicked = true;
        selectedRow = row;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if(clicked) {
            JOptionPane.showMessageDialog(button, "Player added to team.");
        }
        clicked = false;
        return new String(label);
    }

    @Override
    public boolean stopCellEditing() {
        clicked = false;
        return super.stopCellEditing();
    }

    @Override
    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}