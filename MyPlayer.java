import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MyPlayer  extends JFrame {
    final private Font font = new Font("Arial", Font.PLAIN, 15);  
    final private Dimension dimension = new Dimension(150, 50);
    final private Dimension maxSize = new Dimension(600, 300);
    final private Dimension minSize = new Dimension(400, 100);

    private JPanel navigationPanel, sidebarPanel, contentPanel;
    private JTextField heightField, weightField, positionField;
    private DefaultTableModel playersFoundTableModel;
    private JTable playersFoundTable;
    private String playerName, team, number, position, height, weight, lastAttended, country, label, salary;
    private int playerCounter, guardCounter, forwardCounter, centerCounter;
    private double salaryCounter;

    private String jdbcUrl = "jdbc:mysql://localhost:3306/nbamanager";
    private String username = "useract";
    private String password = "welcome1";

    public MyPlayer(){
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
        heightLabel.setFont(font);
        inputPanel.add(heightLabel);
        heightField = new JTextField();
        heightField.setPreferredSize(new Dimension(100, 30));
        heightField.setFont(font);
        inputPanel.add(heightField);

        //Weight Search Input Bar
        JLabel weightLabel = new JLabel("Weight: ");
        weightLabel.setFont(font);
        inputPanel.add(weightLabel);
        weightField = new JTextField();
        weightField.setPreferredSize(new Dimension(100, 30));
        weightField.setFont(font);
        inputPanel.add(weightField);

        //Position Search Input Bar
        JLabel positionLabel = new JLabel("Position: ");
        positionLabel.setFont(font);
        inputPanel.add(positionLabel);
        positionField = new JTextField();
        positionField.setPreferredSize(new Dimension(100, 30));
        positionField.setFont(font);
        inputPanel.add(positionField);

        //Search Button
        JButton searchButton = new JButton("SEARCH");
        searchButton.setFont(font);
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
        String[] columns = {"Name", "Team", "Number", "Position", "Height", "Weight", "Last Attended", "Country", "Label", "Salary", "Add To Team"};
        for(String column: columns) {
            playersFoundTableModel.addColumn(column);
        }
        playersFoundTable = new JTable(playersFoundTableModel);
        playersFoundTable.setPreferredSize(new Dimension(900, 8750));
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
        JPanel bottomPanel = new JPanel(); 
        bottomPanel.setPreferredSize(new Dimension(450, 400));
        getRequirementsStatus();
        JLabel playerCounterLabel = new JLabel("Current number of team players (Minimum 10 players, Maximum 15 players): " + playerCounter);
        JLabel salaryCounterLabel = new JLabel("Current total salary (Maximum 20,000): " + String.format("%.2f", salaryCounter));
        JLabel forwardCounterLabel = new JLabel("Current number of forwards (Minimum 2 players): " + forwardCounter);
        JLabel centerCounterLabel = new JLabel("Current number of centers (Minimum 2 players): " + centerCounter);
        JLabel guardCounterLabel = new JLabel("Current number of guards (Minimum 2 players): " + guardCounter);

        playerCounterLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        salaryCounterLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        forwardCounterLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        centerCounterLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        guardCounterLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        bottomPanel.add(playerCounterLabel, BorderLayout.SOUTH);
        bottomPanel.add(salaryCounterLabel, BorderLayout.SOUTH);
        bottomPanel.add(forwardCounterLabel, BorderLayout.SOUTH);
        bottomPanel.add(centerCounterLabel, BorderLayout.SOUTH);
        bottomPanel.add(guardCounterLabel, BorderLayout.SOUTH);

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

    private void getRequirementsStatus() {
        try (Connection con = DriverManager.getConnection(jdbcUrl, username, password)) {
            try { 
                String sqlPlayerCounter = "SELECT COUNT(name) FROM team_players";
                PreparedStatement psPlayerCounter = con.prepareStatement(sqlPlayerCounter);
                ResultSet rsPlayerCounter = psPlayerCounter.executeQuery(sqlPlayerCounter);
                rsPlayerCounter.next();
                playerCounter = rsPlayerCounter.getInt(1);

                String sqlSalaryCounter = "SELECT SUM(salary) FROM team_players";
                PreparedStatement psSalaryCounter = con.prepareStatement(sqlSalaryCounter);
                ResultSet rsSalaryCounter = psSalaryCounter.executeQuery(sqlSalaryCounter);
                rsSalaryCounter.next();
                salaryCounter = rsSalaryCounter.getDouble(1);

                String sqlForwardCounter = "SELECT COUNT(position) FROM team_players WHERE position LIKE '%F%'";
                PreparedStatement psForwardCounter = con.prepareStatement(sqlForwardCounter);
                ResultSet rsForwardCounter = psForwardCounter.executeQuery(sqlForwardCounter);
                rsForwardCounter.next();
                forwardCounter = rsForwardCounter.getInt(1);

                String sqlGuardCounter = "SELECT COUNT(position) FROM team_players WHERE position LIKE '%G%'";
                PreparedStatement psGuardCounter = con.prepareStatement(sqlGuardCounter);
                ResultSet rsGuardCounter = psGuardCounter.executeQuery(sqlGuardCounter);
                rsGuardCounter.next();
                guardCounter = rsGuardCounter.getInt(1);

                String sqlCenterCounter = "SELECT COUNT(position) FROM team_players WHERE position LIKE '%C%'";
                PreparedStatement psCenterCounter = con.prepareStatement(sqlCenterCounter);
                ResultSet rsCenterCounter = psCenterCounter.executeQuery(sqlCenterCounter);
                rsCenterCounter.next();
                centerCounter = rsCenterCounter.getInt(1);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadPlayersTable() {
        try (Connection con = DriverManager.getConnection(jdbcUrl, username, password)) {
            String sql = "SELECT * FROM players_profile WHERE name NOT IN (SELECT name FROM team_players)";
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
                label = rs.getString("label");
                salary = rs.getString("salary");
                JButton addButton = new JButton("Add");
                playersFoundTable.getColumnModel().getColumn(10).setCellRenderer(new ButtonRenderer());
                playersFoundTable.getColumnModel().getColumn(10).setCellEditor(new ButtonEditor(new JTextField(), playersFoundTable));
                playersFoundTableModel.addRow(new Object[]{playerName, team, number, position, height, weight, lastAttended, country, label, salary, addButton});
            }

            rs.close();
            statement.close();  

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadPlayersFoundTable() {
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
                        label = rs.getString("label");
                        salary = rs.getString("salary");
                        JButton addButton = new JButton("Add");
                        playersFoundTable.getColumnModel().getColumn(10).setCellRenderer(new ButtonRenderer());
                        playersFoundTable.getColumnModel().getColumn(10).setCellEditor(new ButtonEditor(new JTextField(), playersFoundTable));
                        playersFoundTableModel.addRow(new Object[]{playerName, team, number, position, height, weight, lastAttended, country, label, salary, addButton});
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
                new MyTeam();
                break;
            case "PLAYER":
                new MyPlayer(); 
                break;
            case "JOURNEY":
                new JourneyGraph();
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

class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
    protected JButton button;
    private String label;
    private boolean clicked;
    private int selectedRow;
    private JTable table;

    private String jdbcUrl = "jdbc:mysql://localhost:3306/nbamanager";
    private String username = "useract";
    private String password = "welcome1";

    public ButtonEditor(JTextField text, JTable table) {
        super();
        this.button = new JButton();
        this.table = table;
        button.setOpaque(true);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (table.getSelectedRow() == -1) {
                    JOptionPane.showMessageDialog(button, "No row selected.");
                    return;
                }

                TableModel tableModel = table.getModel();
                String value = ((DefaultTableModel) tableModel).getDataVector().elementAt(table.convertRowIndexToModel(table.getSelectedRow())).toString();
                addPlayerToTeam(value);
            }
        });

        button.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    fireEditingStopped();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });
    }

    private void addPlayerToTeam(String playerInfo) {
        playerInfo = playerInfo.replace("[", "").replace("]", "");
        String[] playerInfoArr = playerInfo.split(", ", 11);

        String playerName = playerInfoArr[0];
        String team = playerInfoArr[1];
        String number = playerInfoArr[2];
        String position = playerInfoArr[3];
        String height = playerInfoArr[4];
        String weight = playerInfoArr[5];
        String lastAttended = playerInfoArr[6];
        String country = playerInfoArr[7];
        String label = playerInfoArr[8];
        String salary = playerInfoArr[9];
        Double salaryDouble = Double.parseDouble(salary);

        try (Connection con = DriverManager.getConnection(jdbcUrl, username, password)) {
            try {
                String sqlPlayerCounter = "SELECT COUNT(name) FROM team_players";
                PreparedStatement psPlayerCounter = con.prepareStatement(sqlPlayerCounter);
                ResultSet rsPlayerCounter = psPlayerCounter.executeQuery();
                rsPlayerCounter.next();
                int playerCounter = rsPlayerCounter.getInt(1);

                String sqlSalaryCounter = "SELECT SUM(salary) FROM team_players";
                PreparedStatement psSalaryCounter = con.prepareStatement(sqlSalaryCounter);
                ResultSet rsSalaryCounter = psSalaryCounter.executeQuery();
                rsSalaryCounter.next();
                double salaryCounter = rsSalaryCounter.getDouble(1);

                double newSalaryTotal = salaryCounter + salaryDouble;

                if(playerCounter < 15 && newSalaryTotal <= 20000) {
                    String sql = "INSERT INTO team_players (name, team, number, position, height, weight, last_attended, country, label, salary) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement ps = con.prepareStatement(sql);

                    ps.setString(1, playerName);
                    ps.setString(2, team);
                    ps.setString(3, number);
                    ps.setString(4, position);
                    ps.setString(5, height);
                    ps.setString(6, weight);
                    ps.setString(7, lastAttended);
                    ps.setString(8, country);
                    ps.setString(9, label);
                    ps.setString(10, salary);

                    int result = ps.executeUpdate();

                    if (result > 0) {
                        System.out.println("Player " + playerName + " added to the team.");
                        removePlayerFromList(table);
                        JOptionPane.showMessageDialog(button, "Player " + playerName + " added to the team.");
                    } else {
                        System.out.println("Failed to add player to the team. Please try again.");
                        JOptionPane.showMessageDialog(button, "Failed to add player to the team. Please try again.");
                    }
                } else {
                    System.out.println("Exceeded number of team players or total team salary.");
                    JOptionPane.showMessageDialog(button, "Cannot add player. Exceeded team constraints.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removePlayerFromList(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        if (table.getSelectedRow() != -1) {
            model.removeRow(table.convertRowIndexToModel(table.getSelectedRow()));
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean selected, int row, int column) {
        label = "Add";
        button.setText(label);
        selectedRow = row;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return label;
    }
}
