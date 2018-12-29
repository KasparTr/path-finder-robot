import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Node {

    public final String name;
    public int[] coords;
    public final boolean isSpecial;
    public boolean isSource;
    public final boolean isDenied;

    private List<Node> shortestPath = new LinkedList<>();

    private Integer distance = Integer.MAX_VALUE;

    Map<Node, Integer> adjacentNodes = new HashMap<>();

    public void addDestination(Node destination, int distance) {
        adjacentNodes.put(destination, distance);
    }


    public void reset(){
        shortestPath = new LinkedList<>();
        distance = Integer.MAX_VALUE;
    }

    public Node(String name, int[] coords) {
        this.name = name;
        this.isSpecial=false;
        this.isSource=false;
        this.isDenied=false;
        this.coords = coords;
    }
    public Node(String name, boolean isSpecial, boolean isSource, boolean isDenied, int[] coords) {
        this.name = name;
        this.isSpecial = isSpecial;
        this.isSource =  isSource;
        this.isDenied = isDenied;
        this.coords = coords;
    }

    public int getShortestPathDistance(){
        return shortestPath.size();
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public  Map<Node, Integer> getAdjacentNodes() {
        return this.adjacentNodes;
    }

    public int getDistance() {
        return this.distance;
    }

    public List<Node> getShortestPath() {
        return shortestPath;
    }

    public String getShortestPathAsString(){
        String result ="";
        for(Node n:this.shortestPath){
            result += "->" + n.getCoordsAsString();
        }
        return result;
    }

    public void setShortestPath(LinkedList<Node> shortestPath) {
        this.shortestPath = shortestPath;
    }

    public String toString(){
        return "Name: " + this.name + ". Coords: " + getCoordsAsString();
    }

    public String getCoordsAsString(){
        return this.coords[0] + "," + this.coords[1];
    }
}