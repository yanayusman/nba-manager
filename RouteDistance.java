import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class RouteDistance <T extends Comparable<T>, N extends Comparable <N>>{
    City<T,N> head;
    int size;
    Integer totalDistance;
    private Map<T, N> map;

    public RouteDistance(Map<T, N> map) {
        this.map = map;
        this.totalDistance=0;
    }
    /*public ArrayList<ArrayList<T>> breadthFirstSearch(T source) {
        ArrayList<ArrayList<T>> journeys = new ArrayList<>();
        if (!map.hasCity(source))
            return journeys; // Starting city not found in the map
    
        int totalCities = map.getSize(); // Total number of cities in the map
    
        HashSet<T> visited = new HashSet<>();
        LinkedList<ArrayList<T>> queue = new LinkedList<>();
    
        ArrayList<T> initialJourney = new ArrayList<>();
        initialJourney.add(source);
        queue.offer(initialJourney);
        visited.add(source); // Mark the starting city as visited
    
        while (!queue.isEmpty()) {
            ArrayList<T> currentJourney = queue.poll();
            T currentCity = currentJourney.get(currentJourney.size() - 1);
    
            // Check if all cities have been visited at least once
            if (visited.size() == totalCities) {
                journeys.add(new ArrayList<>(currentJourney));
            } else {
                for (T neighbor : map.getNeighbours(currentCity)) {
                    if (!visited.contains(neighbor)) {
                        ArrayList<T> newJourney = new ArrayList<>(currentJourney);
                        newJourney.add(neighbor); // Add the neighbor to the current journey
                        queue.offer(newJourney);
                        visited.add(neighbor);
                    }
                }
            }
        }
        return journeys;
    }
    */
public ArrayList<ArrayList<T>> depthFirstSearch(T source) {
    ArrayList<ArrayList<T>> allRoutes = new ArrayList<>();
    if (!map.hasCity(source))
        return allRoutes; // Starting city not found in the map

    HashSet<T> visited = new HashSet<>();
    ArrayList<T> currentRoute = new ArrayList<>();

    depthFirstSearchRecursive(source, visited, currentRoute, allRoutes);
    return allRoutes;
}


private void depthFirstSearchRecursive(T currentCity, HashSet<T> visited, ArrayList<T> currentRoute, ArrayList<ArrayList<T>> allRoutes) {
    visited.add(currentCity);
    currentRoute.add(currentCity); // Add current city to the route

    // Check if the current route is complete
    if (currentRoute.size() == map.getSize()) {
        allRoutes.add(new ArrayList<>(currentRoute));
        visited.remove(currentCity); // Backtrack: Remove current city from visited set after exploring all neighbors
        currentRoute.remove(currentRoute.size() - 1); // Backtrack: Remove current city from current route
        return;
    }

    for (T neighbor : map.getNeighbours(currentCity)) {
        if (!visited.contains(neighbor)) {
            depthFirstSearchRecursive(neighbor, visited, currentRoute, allRoutes);
        }
    }

    visited.remove(currentCity); // Backtrack: Remove current city from visited set after exploring all neighbors
    currentRoute.remove(currentRoute.size() - 1); // Backtrack: Remove current city from current route
}
public ArrayList<T> bfsTraversal(T startingCity) {
    if (!map.hasCity(startingCity)) {
      return null; // Starting city not found in the map
    }

    LinkedList<T> queue = new LinkedList<>();
    HashMap<T, Boolean> visited = new HashMap<>();

    queue.offer(startingCity);
    visited.put(startingCity, true);

    ArrayList<T> traversalList = new ArrayList<>(); // Track visited cities

    while (!queue.isEmpty()) {
      T currentCity = queue.poll();
      traversalList.add(currentCity);

      ArrayList<T> neighbors = map.getNeighbours(currentCity);
      if (neighbors != null) {
        for (T neighbor : neighbors) {
          if (!visited.containsKey(neighbor)) {
            queue.offer(neighbor);
            visited.put(neighbor, true);
          }
        }
      }
    }

    return traversalList;
  }

    public N getTotalDistance() {
        N currentTotalDistance = (N) totalDistance;
        totalDistance = 0; // Reset total distance
        return currentTotalDistance;
    }

    public N totalDistance(N distanceBetweenRoute, N total){
        totalDistance += (Integer)distanceBetweenRoute;
        return (N) totalDistance;
    }
}
