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