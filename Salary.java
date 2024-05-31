import java.sql.*;
import java.util.Random;

public class Salary {

    private String nameProfile;
    private String nameStat, points;
    private String label, salary;
    private Double pointsDouble, salaryDouble;

    public Salary() {
    }

    public void initialize() {
        String jdbcUrl = "jdbc:mysql://localhost:3306/nbamanager";
        String username = "useract";
        String password = "welcome1";
        Random random = new Random();

        try (Connection con = DriverManager.getConnection(jdbcUrl, username, password)) {

            String sqlProfile = "SELECT * FROM players_profile ORDER BY name";
            Statement statementProfile = con.createStatement();
            ResultSet rsProfile = statementProfile.executeQuery(sqlProfile);

            String sqlStat = "SELECT * FROM players_stat_23_24 ORDER BY name";
            Statement statementStat = con.createStatement();
            ResultSet rsStat = statementStat.executeQuery(sqlStat);

            /*
            * Adding new columns to the players_profile table
            */
            // String sqlLabelColumn = "ALTER TABLE players_profile ADD label varchar(100);";
            // int rsLabelColumn = statement.executeUpdate(sqlLabelColumn);
            // String sqlSalaryColumn = "ALTER TABLE players_profile ADD salary varchar(100);";
            // int rsSalaryColumn = statement.executeUpdate(sqlSalaryColumn);

            while (rsProfile.next() && rsStat.next()) {
                nameProfile = rsProfile.getString("name");
                nameStat = rsStat.getString("name");
                points = rsStat.getString("pts");
                pointsDouble = Double.parseDouble(points);


                if(nameProfile.equals(nameStat)) {
                    if(pointsDouble >= 20.0) {
                        label = "Superstar Player";
                        salaryDouble = 3000 + (5000 - 3000) *  random.nextDouble();
                        salary = String.format("%.2f", salaryDouble);

                    } else if (pointsDouble < 20.0) {
                        label = "Non-superstar Player";
                        salaryDouble = 1000 + (3000 - 1000) * random.nextDouble();
                        salary = String.format("%.2f", salaryDouble);

                    } else {
                        label = "LABEL";
                        salary = "SALARY";
                    }

                    String sqlUpdateLabel = "UPDATE players_profile SET label = ? where name = ?";
                    PreparedStatement psUpdateLabel = con.prepareStatement(sqlUpdateLabel);
                    psUpdateLabel.setString(1, label);
                    psUpdateLabel.setString(2, nameProfile);
                    psUpdateLabel.executeUpdate();

                    String sqlUpdateSalary = "UPDATE players_profile SET salary = ? where name = ?";
                    PreparedStatement psUpdateSalary = con.prepareStatement(sqlUpdateSalary);
                    psUpdateSalary.setString(1, salary);
                    psUpdateSalary.setString(2, nameProfile);
                    psUpdateSalary.executeUpdate();

                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Salary salary = new Salary();
        salary.initialize();
    }

}