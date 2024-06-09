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
    public double points;
    public double rebounds;
    public double steals;
    public double assists;
    public double blocks;
    public double compositeScore;
    public String imagePath;

    public Player(String name, double points, double rebounds, double steals, double assists, double blocks, String position) {
        this.name = name;
        this.points = points;
        this.rebounds = rebounds;
        this.steals = steals;
        this.assists = assists;
        this.blocks = blocks;
        this.position = position;
    }

    public void calculateCompositeScore(Player player) {
        double pointsWeight = 1.0;
        double reboundsWeight;
        double stealsWeight;
        double assistsWeight;
        double blocksWeight;

        switch (player.position) {
            case "Center":
                reboundsWeight = 1.5;
                stealsWeight = 1.0;
                assistsWeight = 1.0;
                blocksWeight = 1.5;
                break;
            case "Forward":
                reboundsWeight = 1.3;
                stealsWeight = 1.0;
                assistsWeight = 1.1;
                blocksWeight = 1.3;
                break;
            case "Guard":
                reboundsWeight = 1.0;
                stealsWeight = 1.5;
                assistsWeight = 1.5;
                blocksWeight = 1.0;
                break;
            default:
                reboundsWeight = 1.0;
                stealsWeight = 1.0;
                assistsWeight = 1.0;
                blocksWeight = 1.0;
                break;
        }

        player.compositeScore = (points * pointsWeight) + (rebounds * reboundsWeight) + (steals * stealsWeight) + (assists * assistsWeight) +(blocks * blocksWeight);
    }
    
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
