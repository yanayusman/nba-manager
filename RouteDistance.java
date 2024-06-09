import java.util.ArrayList;
import java.util.HashSet;

public class RouteDistance <T extends Comparable<T>, N extends Comparable <N>>{
    City<T,N> head;
    int size;
    Integer totalDistance;
    private Map<T, N> map;

    public RouteDistance(Map<T, N> map) {
        this.map = map;
        this.totalDistance=0;
    }
  
public ArrayList<ArrayList<T>> depthFirstSearch(T source) {
    ArrayList<ArrayList<T>> allRoutes = new ArrayList<>();
    if (!map.hasCity(source))
        return allRoutes;                                                       // Starting city not found in the map

    HashSet<T> visited = new HashSet<>();
    ArrayList<T> currentRoute = new ArrayList<>();

    depthFirstSearchRecursive(source, visited, currentRoute, allRoutes);        //calls dfs method to find the journeys
    return allRoutes;
}

//depth first search with recursive
private void depthFirstSearchRecursive(T currentCity, HashSet<T> visited, ArrayList<T> currentRoute, ArrayList<ArrayList<T>> allRoutes) {
    visited.add(currentCity);
    currentRoute.add(currentCity);                                                // Add current city to the route

    if (currentRoute.size() == map.getSize()) {                                   // Check if the current route is complete
        allRoutes.add(new ArrayList<>(currentRoute));
        visited.remove(currentCity);                                              // Backtrack: Remove current city from visited set after exploring all neighbors
        currentRoute.remove(currentRoute.size() - 1);                             // Backtrack: Remove current city from current route
        return;
    }

    for (T neighbor : map.getNeighbours(currentCity)) {                           //do dfs according to its neighbouring cities
        if (!visited.contains(neighbor)) {
            depthFirstSearchRecursive(neighbor, visited, currentRoute, allRoutes);    // do dfs until it reaches the final end
        }
    }

    visited.remove(currentCity);                                                // Backtrack: Remove current city from visited set after exploring all neighbors
    currentRoute.remove(currentRoute.size() - 1);                               // Backtrack: Remove current city from current route
}
}
