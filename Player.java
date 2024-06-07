public class Player {
    public String name;
    public String injury;
    public String team;
    public String number;
    public String position;
    public String height;
    public String weight;
    public String lastAttended;
    public String country;
    public String label;
    public String salary;
    public double avgPoints;
    public double rebounds;
    public double steals;
    public double assists;
    public double blocks;
    public double compositeScore;
    public String imagePath;

    Player(){}

    Player(String name, String injury){
        this.name = name;
        this.injury = injury;
    }

    public String getName(){
        return this.name;
    }

    public String getInjury(){
        return this.injury;
    }
}