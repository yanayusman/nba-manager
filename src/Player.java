package src;
public class Player {
    private String name;
    private String injury;

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
