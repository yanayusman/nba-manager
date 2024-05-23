package src;
import java.util.*;

public class InjuryQueue {
    private Queue<Player> injuryQueue;

    public InjuryQueue(){
        injuryQueue = new LinkedList<>(); 
    }

    public void addPlayer(Player player){
        injuryQueue.add(player);
    }

    public Player removePlayer(){
        return injuryQueue.poll();
    }

    public boolean isEmpty(){
        return injuryQueue.isEmpty();
    }

    public Queue<Player> getInjuryQueue(){
        return injuryQueue;
    }
}
