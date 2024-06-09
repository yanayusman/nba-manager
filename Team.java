import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;

public class Team extends JFrame {
    final private Font font = new Font("Arial", Font.BOLD, 15);             
    final private Dimension dimension = new Dimension(150, 50);
    final private Dimension maxSize = new Dimension(600, 300);
    final private Dimension minSize = new Dimension(400, 100);

    private JPanel navigationPanel;
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private JPanel searchPanel;
    private JPanel playersPanel;

    // database connection details
    private String jdbcUrl = "jdbc:mysql://localhost:3306/nbamanager";
    private String username = "useract";
    private String password = "welcome1";

    public Team(){
        initialize();  // initialize the GUI components
        loadPlayersFromDatabase(jdbcUrl, username, password);  // load players from the database
    }

    // method to initialize the GUI components
    public void initialize() {
        setTitle("Team Profile");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // navigation Panel
        navigationPanel = new JPanel();
        navigationPanel.setBackground(Color.LIGHT_GRAY);
        navigationPanel.setPreferredSize(new Dimension(800, 50));

        JLabel titleLabel = new JLabel("NBA Manager Game");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        navigationPanel.add(titleLabel);

        // sidebar Panel
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(Color.DARK_GRAY);
        sidebarPanel.setPreferredSize(new Dimension(200, 600));
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
                    handleSidebarButtonClick(label);    // handle sidebar button clicks
                }
            });
            sidebarPanel.add(button);
        }

        // search Panel
        searchPanel = new JPanel();
        searchPanel.setBackground(Color.GRAY);
        searchPanel.setPreferredSize(new Dimension(800, 70));

        JLabel heightLabel = new JLabel("Height: ");
        heightLabel.setFont(font);
        searchPanel.add(heightLabel);
        JTextField heightField = new JTextField();
        heightField.setPreferredSize(new Dimension(100, 30));
        heightField.setFont(font);
        searchPanel.add(heightField);

        JLabel weightLabel = new JLabel("Weight: ");
        weightLabel.setFont(font);
        searchPanel.add(weightLabel);
        JTextField weightField = new JTextField();
        weightField.setPreferredSize(new Dimension(100, 30));
        weightField.setFont(font);
        searchPanel.add(weightField);

        JLabel positionLabel = new JLabel("Position: ");
        positionLabel.setFont(font);
        searchPanel.add(positionLabel);
        JTextField positionField = new JTextField();
        positionField.setPreferredSize(new Dimension(100, 30));
        positionField.setFont(font);
        searchPanel.add(positionField);

        JButton searchButton = new JButton("Search");
        searchButton.setFont(font);
        searchButton.setPreferredSize(new Dimension(125, 45));
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // perform search based on user input
                String height = heightField.getText();
                String weight = weightField.getText() + " lbs";
                String position = positionField.getText();
                searchPlayers(height, weight, position);
            }
        });

        JPanel searchButtonPanel = new JPanel();
        searchButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        searchButtonPanel.setBackground(Color.GRAY);
        searchButtonPanel.add(searchButton);

        //rank button
        JButton rankButton = new JButton("Ranking");
        rankButton.setFont(font);
        rankButton.setPreferredSize(new Dimension(125, 45));
        rankButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rankPlayers();      // rank players based on performance
                showRankings(Team.this);    // display player rankings
            }
        });
        searchButtonPanel.add(rankButton);
        searchPanel.add(searchButtonPanel, BorderLayout.SOUTH);

        // content Panel
        contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new BorderLayout());

        // create a panel for search and add it to the top of the contentPanel
        JPanel searchContentPanel = new JPanel(new BorderLayout());
        searchContentPanel.setBackground(Color.WHITE);
        searchContentPanel.add(searchPanel, BorderLayout.NORTH);
        contentPanel.add(searchContentPanel, BorderLayout.NORTH);

        // create a panel for displaying players
        playersPanel = new JPanel();
        playersPanel.setBackground(Color.WHITE);
        playersPanel.setLayout(new GridLayout(0, 3, 10, 10));
        contentPanel.add(playersPanel, BorderLayout.CENTER);

        // load and display players
        loadPlayers();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(navigationPanel, BorderLayout.NORTH);
        getContentPane().add(sidebarPanel, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    /*
     * Method to search players based on given criteria
     * List to store matching players
     * Establish database connection
     * SQL query to select players based on height, weight, and position
     * Set query parameters
     * Execute query and get results
     * Iterate through the result set and create Player objects
     * Add player to the list of matching players
     */
    private void searchPlayers(String height, String weight, String position) {
        List<Player> matchingPlayer = new ArrayList<>();

        try(Connection con = DriverManager.getConnection(jdbcUrl, username, password)){
            String sql = "SELECT * FROM players_profile WHERE height > ? AND weight < ? AND position LIKE ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, height);
            ps.setString(2, weight);
            ps.setString(3, position);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                Player player = new Player();
                player.name = rs.getString("name");
                player.team = rs.getString("team");
                player.number = rs.getString("number");
                player.position = rs.getString("position");
                player.height = rs.getString("height");
                player.weight = rs.getString("weight");
                player.lastAttended = rs.getString("last_attended");
                player.country = rs.getString("country");
                player.label = rs.getString("label");
                player.salary = rs.getString("salary");
                matchingPlayer.add(player);
            }
            rs.close();
            ps.close();
        }catch(SQLException e){
            e.printStackTrace();
        }

        // display the search results
        if(!matchingPlayer.isEmpty()){
            String[] columns = {"Name", "Team", "Number", "Position", "Height", "Weight", "Last Attended", "Country", "Label", "Salary"};
            // Create data array for the table
            Object[][] data = new Object[matchingPlayer.size()][columns.length];
            for(int i = 0; i < matchingPlayer.size(); i++){
                Player player = matchingPlayer.get(i);
                data[i] = new Object[]{player.name, player.team, player.number, player.position, player.height, player.weight, player.lastAttended, player.country, player.label, player.salary};
            }
            JTable table = new JTable(data, columns);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(900, 600));
            JOptionPane.showMessageDialog(this, scrollPane, "Matching Players", JOptionPane.PLAIN_MESSAGE);
        }else{
            JOptionPane.showMessageDialog(this, "No players found with the specified criteria", "No matches", JOptionPane.PLAIN_MESSAGE);
        }
    }

    /*
     * Method to load players from the database and display them in the GUI
     * Establish database connection
     * SQL query to select all players
     * Execute query and get results
     * List to store player objects
     * Iterate through the result set and create Player objects
     */
    private void loadPlayers() {
        playersPanel.removeAll();
        try (Connection con = DriverManager.getConnection(jdbcUrl, username, password)) {
            String sql = "SELECT * FROM team_players";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
    
            List<Player> players = new ArrayList<>();
    
            while (rs.next()) {
                Player player = new Player();
                player.name = rs.getString("name");
                player.team = rs.getString("team");
                player.number = rs.getString("number");
                player.position = rs.getString("position");
                player.height = rs.getString("height");
                player.weight = rs.getString("weight");
                player.lastAttended = rs.getString("last_attended");
                player.country = rs.getString("country");
                player.imagePath = "profile_pic.png";
                players.add(player);
            }
    
            rs.close();
            statement.close();
    
            for (Player player : players) {
                JPanel playerPanel = new JPanel();
                playerPanel.setLayout(new BorderLayout());
                playerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    
                // Image
                ImageIcon icon = new ImageIcon(player.imagePath);
                Image img = icon.getImage();
                Image newImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                icon = new ImageIcon(newImg);
                JLabel picLabel = new JLabel(icon);
                picLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                playerPanel.add(picLabel, BorderLayout.CENTER);
    
                JPanel namePanel = new JPanel(new BorderLayout());
                JLabel nameLabel = new JLabel(player.name, JLabel.CENTER);
                nameLabel.setFont(new Font("Arial", Font.BOLD, 15));
                namePanel.add(nameLabel, BorderLayout.CENTER);
    
                JButton removeButton = new JButton("REMOVE");
                removeButton.addActionListener(e -> removePlayer(player));
                namePanel.add(removeButton, BorderLayout.EAST);
    
                playerPanel.add(namePanel, BorderLayout.SOUTH);
    
                playerPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        showPlayerDetails(player);
                    }
                });
    
                playersPanel.add(playerPanel);
            }
    
            playersPanel.revalidate();
            playersPanel.repaint();
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
     * method to remove a player from the database and update the GUI
     * Establish database connection
     * SQL query to delete player by name
     */
    private void removePlayer(Player player) {
        try (Connection con = DriverManager.getConnection(jdbcUrl, username, password)) {
            String sql = "DELETE FROM team_players WHERE name = ?";
            
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, player.name);
                int result = ps.executeUpdate();
        
                if(result > 0)
                    JOptionPane.showMessageDialog(null, "Player \"" + player.name + "\" has been removed successfully.", "Player Removed", JOptionPane.INFORMATION_MESSAGE);
                else    
                    JOptionPane.showMessageDialog(null, "Player \"" + player.name + "\" not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            loadPlayers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    } 

    // method to show player details in a dialog
    private void showPlayerDetails(Player player) {
        // Create and setup dialog
        JDialog dialog = new JDialog(this, "Player Details", true);
        dialog.setSize(300, 400);
        dialog.setLocationRelativeTo(this);

        // Create and setup detail panel
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));

        // Add player details to the panel
        JLabel nameLabel = new JLabel("Name: " + player.name);
        nameLabel.setFont(font);
        detailPanel.add(nameLabel);

        JLabel teamLabel = new JLabel("Team: " + player.team);
        teamLabel.setFont(font);
        detailPanel.add(teamLabel);

        JLabel numberLabel = new JLabel("Number: " + player.number);
        numberLabel.setFont(font);
        detailPanel.add(numberLabel);

        JLabel positionLabel = new JLabel("Position: " + player.position);
        positionLabel.setFont(font);
        detailPanel.add(positionLabel);

        JLabel heightLabel = new JLabel("Height: " + player.height);
        heightLabel.setFont(font);
        detailPanel.add(heightLabel);

        JLabel weightLabel = new JLabel("Weight: " + player.weight);
        weightLabel.setFont(font);
        detailPanel.add(weightLabel);

        JLabel lastAttendedLabel = new JLabel("Last Attended: " + player.lastAttended);
        lastAttendedLabel.setFont(font);
        detailPanel.add(lastAttendedLabel);

        JLabel countryLabel = new JLabel("Country: " + player.country);
        countryLabel.setFont(font);
        detailPanel.add(countryLabel);

        // Add detail panel to dialog and show it
        dialog.add(detailPanel);
        dialog.setVisible(true);
    }

    // method to handle sidebar button clicks
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

    // list to store players
    List<Player> players = new ArrayList<>();

    // method to add a player to the list
    public void addPlayer(Player player) {
        players.add(player);
    }
    
    // method to rank players based on performance
    public void rankPlayers() {
        for (Player player : players) {
            player.calculateCompositeScore(player);
        }
        players.sort(Comparator.comparingDouble(p -> -p.compositeScore));           // compare composite score among players
    }

    // method to show player rankings in a table
    public void showRankings(Component parentComponent) {
        String[] columnNames = {"Rank", "Player", "Composite Score"};
        Object[][] data = new Object[players.size()][3];
        
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            data[i][0] = i + 1;
            data[i][1] = player.name;
            data[i][2] = String.format("%.2f", player.compositeScore);
        }

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        // show in a message form
        JOptionPane.showMessageDialog(parentComponent, scrollPane, "Player Performance Ranking", JOptionPane.INFORMATION_MESSAGE);
    }

    // method to load players from the database and add them to the list
    public void loadPlayersFromDatabase(String dbUrl, String dbUser, String dbPassword) {
        String query = "SELECT tp.name, tp.position, ps.pts, ps.reb, ps.ast, ps.blk, ps.stl " +
                       "FROM team_players tp " +
                       "LEFT JOIN players_stat_23_24 ps ON tp.name = ps.name;";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Player player = new Player(
                    rs.getString("name"),
                    rs.getDouble("pts"),
                    rs.getDouble("reb"),
                    rs.getDouble("stl"),
                    rs.getDouble("ast"),
                    rs.getDouble("blk"),
                    rs.getString("position")
                );
                addPlayer(player);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Team();
        });
    }
}