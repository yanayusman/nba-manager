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
    public double pts;
    public double oreb;
    public double dreb;
    public double reb;
    public double stl;
    public double ast;
    public double blk;
    public double tov;
    public double fg;
    public double eff;
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