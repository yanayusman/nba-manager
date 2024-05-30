import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MyTeam extends JFrame {
    final private Font font = new Font("Arial", Font.PLAIN, 15);             
    final private Dimension dimension = new Dimension(150, 50);
    final private Dimension maxSize = new Dimension(600, 300);
    final private Dimension minSize = new Dimension(400, 100);

    private JPanel navigationPanel;
    private JPanel sidebarPanel;
    private JPanel contentPanel;

    private String playerName, team, number, position, height, weight, lastAttended, country, profile_pic;

    public MyTeam(){
        initialize();
        loadPlayers();
    }

    public void initialize() {
        setTitle("Team Profile");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Navigation Panel
        navigationPanel = new JPanel();
        navigationPanel.setBackground(Color.LIGHT_GRAY);
        navigationPanel.setPreferredSize(new Dimension(800, 50));

        JLabel titleLabel = new JLabel("NBA Manager Game");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        navigationPanel.add(titleLabel);

        // Sidebar Panel
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(Color.DARK_GRAY);
        sidebarPanel.setPreferredSize(new Dimension(200, 600));
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

        // Content Panel
        contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new GridLayout(0, 3, 10, 10));

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(navigationPanel, BorderLayout.NORTH);
        getContentPane().add(sidebarPanel, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);                
        
        setVisible(true);
    }

    private void loadPlayers() {
        String jdbcUrl = "jdbc:mysql://localhost:3306/nbamanager";
        String username = "useract";
        String password = "welcome1";
    
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
    
                //image
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
                playerPanel.add(nameLabel, BorderLayout.SOUTH);

                JButton removeButton = new JButton("Remove");
                removeButton.addActionListener(e -> removePlayer(player));

                namePanel.add(nameLabel, BorderLayout.CENTER);
                namePanel.add(removeButton, BorderLayout.EAST);

                playerPanel.add(namePanel, BorderLayout.SOUTH);
    
                playerPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        showPlayerDetails(player);
                    }
                });
    
                contentPanel.add(playerPanel);
            }
    
            contentPanel.revalidate();
            contentPanel.repaint();
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removePlayer(Player player){
        String jdbcUrl = "jdbc:mysql://localhost:3306/nbamanager";
        String username = "useract";
        String password = "welcome1";

        try(Connection con = DriverManager.getConnection(jdbcUrl, username, password)){
            String sql = "DELETE FROM team_players WHERE name = ?";

            try(PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, player.name);
                ps.executeUpdate();
            }
            contentPanel.removeAll();
            loadPlayers();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private void showPlayerDetails(Player player) {
        JDialog dialog = new JDialog(this, "Player Details", true);
        dialog.setSize(300, 400);
        dialog.setLocationRelativeTo(this);

        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));

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

        dialog.add(detailPanel);
        dialog.setVisible(true);
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
                new Player_(); 
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
            new MyTeam();
        });
    }
}
