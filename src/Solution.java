import java.sql.Time;
import java.util.*;


public class Solution {

    private final String SOURCE_CELL_KEY = "0,0";
    private final int FLAT_LAND_DISTANCE = 1;

    /**
     * PROBLEM:
     *  Given a matrix (lot) of values (-1:Robot,1:Land,0:Trench,9:Target) 
     *      calculate the shortest paths to clear all targets (9s) by only using land(1s) and avoiding trenches(0s).
     * The algorithm works as follows:
     *  1) Creates a graph (tree) from the the matrix.
     *      1.1) Lot cells with value 0 (trenches) are not added in the graph as they cannot be traversed.
     *      1.2) Each edge in the graph has a distance of 1
     *      1.3) Each created Node is either a Robot (cell value of -1), Land (cell value of 1) or Obstacle (cell value of 9)
     *  2) Gathers all Nodes that play a role in the distance calculation (Robot and Obstacles)
     *  3) Using Dijksta's algorithm, calculates the following:
     *      3.1) Distances between Robot and all Obstacles
     *      3.2) Distances between all Obstacles
     *  4) Calculates the shortest path that Robot needs to take to clear all obstacles using results from point 3
     *  5) Returns the shortest path distance.
     * @param numRows number of rows the lot has (must match numColumns)
     * @param numColumns number of columns the lot has (must match numRows)
     * @param lot matrix of cell values where each value represents an object (-1:Robot, 0:Trench: 1:Land, 9:Obstacle)
     * @return the shortest path distance (where each step has a distance of 1)
     */
    public int removeObstacle(int numRows, int numColumns, List<List<Integer>> lot)
    {
        // Check for correct parameters.
        if(faultyParams(numRows, numColumns, lot)) return 0;
        if(lot.size()==1) return 1;

        HashMap<String, Node> nodeRepo = new HashMap<>();
        HashMap<String, Hashtable<String, Integer>> distancesMap= new HashMap<>();

        // Create a graph tree from the lot (matrix)
        Graph origGraph = createGraph(lot, nodeRepo);   //O(N^2)

        // Gather Robot and all Obstacle nodes
        List<Node> nodesOfInterest = findAllInterestingNodes(origGraph);    //O(N)


        // Calculate distances between Robot and Obstacles:
        //  ...calculate the distance between obstacle, and Robot
        //  ...calculate the distance between obstacle and other obstacles
        for(Node n:nodesOfInterest){
            // Reset all nodes for calculating the shortest paths for a new root
            for(Node origNode:origGraph.nodes) origNode.reset();

            // Calculate shortest path from new root to all targets
            Dijkstra.calculateShortestPathFromSource(origGraph, n);     // O(N^2)
            Hashtable<String, Integer> distToTargets = new Hashtable<>();

            for(Node node:origGraph.nodes){
                // Only consider targets (special nodes) when looking at path options
                if(node.isSpecial){
                    distToTargets.put(node.getCoordsAsString(), node.getShortestPathDistance());
                    //System.out.println("to -> " + node.getCoordsAsString() + "= " + node.getShortestPathDistance());
                }
            }
            distancesMap.put(n.getCoordsAsString(), distToTargets);
        }

        // For DEBUG
        plotHashtable(distancesMap);

        // Get shortest path and return its total distance.
        Hashtable<String, Integer> pathAndDistances = getShortestPathToObstaclesRec(distancesMap, SOURCE_CELL_KEY, 0,new Hashtable<String, Integer>());
        return getPathDistance(pathAndDistances);
    }

    private boolean faultyParams(int numRows, int numColumns, List<List<Integer>> lot) {
        if(numRows != numColumns) return true;
        else if(numRows < 1) return true;
        if(lot.size() < 1) return true;
        return false;
    }

    // Returns nodes that are of interest:
    //  - Root (robot)
    //  - Targets (obstacles)
    // Time Complexity: O(N)
    private List<Node> findAllInterestingNodes(Graph graph){
        List<Node> result = new ArrayList<>();
        for(Node node:graph.nodes){
            if(node.isSpecial || node.isSource) result.add(node);
        }

        return result;
    }

    // Create a graph from a matrix and its cell values.
    // (Robot can more up, down, left, right)
    // Time Complexity: O(N^2)
    private Graph createGraph(List<List<Integer>> lot, HashMap<String, Node> nodeRepo ){
        Graph graph = new Graph();
        for(int row = 0; row < lot.size(); row++){
            for(int col = 0; col < lot.get(0).size(); col++){
                if(isValidCell(lot, row, col)){
                    Node node = getNode(lot, row, col, nodeRepo);
                    if(isValidCell(lot, row, col+1)) {
                        Node n = getNode(lot, row, col+1, nodeRepo );
                        node.addDestination(n, FLAT_LAND_DISTANCE );
                    }
                    if(isValidCell(lot, row, col-1)) {
                        Node n = getNode(lot, row, col-1, nodeRepo );
                        node.addDestination(n,FLAT_LAND_DISTANCE );
                    }
                    if(isValidCell(lot, row+1, col)) {
                        Node n = getNode(lot, row+1, col, nodeRepo );
                        node.addDestination(n,FLAT_LAND_DISTANCE );
                    }
                    if(isValidCell(lot, row-1, col)) {
                        Node n = getNode(lot, row-1, col, nodeRepo );
                        node.addDestination(n,FLAT_LAND_DISTANCE );
                    }
                    graph.addNode(node);
                }
            }
        }
        return graph;
    }

    // Check if the the cell is valid for the robot to potentially move there
    // Cell is not valid if:
    //  - cell has negative coordinates (over the lot edge)
    //  - cell has value of 0 (cell is a trench)
    private boolean isValidCell(List<List<Integer>> lot, int row, int col){
        if(row < 0 || col < 0 || row >= lot.size() || col >= lot.get(0).size()) return false;  // over the edge

        if(lot.get(row).get(col) == -1) return true;        // robot
        if(lot.get(row).get(col) == 1) return true;         // flat land
        else if(lot.get(row).get(col) == 9)  return true;   // obstacle
        else return false;                                  // trench or something else invalid
    }

    // Get a Node from repository or create a new Node if not found.
    // Depending on the cell value, a Node can be:
    //  - Robot (source/root)
    //  - Land (travestable cell)
    //  - Trench (non-traversable cell)
    //  - Obstacle (target for the robot)
    private Node getNode(List<List<Integer>> lot, int row, int col, HashMap<String, Node> nodeRepo ){
        Node node = nodeRepo.getOrDefault(""+row+col, null);
        if(node != null){
            return node;
        }else{
            if(lot.get(row).get(col) == -1) node = new Node("Robot", false, true, false, new int[]{row,col});
            else if(lot.get(row).get(col) == 1) node =  new Node("Land", new int[]{row,col});
            else if(lot.get(row).get(col) == 9)  node =  new Node("Obstacle", true, false, false, new int[]{row,col});
            else  node = new Node("Trench", false, false, true, new int[]{row,col});
            nodeRepo.put(""+row+col, node);
            return node;
        }
    }

    // Created the shortest path for the robot to travel from all the possible traversal options.
    // Uses recursion to compose the path and each step distance.
    private Hashtable<String, Integer> getShortestPathToObstaclesRec(HashMap<String, Hashtable<String, Integer>> table, String rowKeyStart, int distance, Hashtable<String, Integer> path){
        if(rowKeyStart=="") return path;
        else if(path.get(rowKeyStart) != null) return path;
        else{
            path.put(rowKeyStart, distance);
            Hashtable<String, Integer> colKeyValue = table.get(rowKeyStart);
            Enumeration<String> colKeys= colKeyValue.keys();

            int smallestInRow = Integer.MAX_VALUE;
            String selectedCellColKey="";

            while(colKeys.hasMoreElements()) {
                String colKey = colKeys.nextElement();
                Integer cellValue = colKeyValue.get(colKey);
                if (cellValue != 0 && cellValue < smallestInRow && !path.keySet().contains(colKey)) {
                    smallestInRow = cellValue;
                    selectedCellColKey = colKey;
                }
            }
            return getShortestPathToObstaclesRec(table, selectedCellColKey, smallestInRow, path);
        }
    }

    // Find the total distance from a path map with distances.
    private int getPathDistance(Hashtable<String, Integer> pathAndDistances) {
        int result = 0;
        Enumeration<String> keys = pathAndDistances.keys();
        while(keys.hasMoreElements()) {
            String key = keys.nextElement();
            Integer distance = pathAndDistances.get(key);
            result+=distance;
        }
        return result;
    }

    // Helper method to plot out all possible traversal options for Robot.
    private void plotHashtable(HashMap<String, Hashtable<String, Integer>> table){
        for (String rowKey : table.keySet()) {
            System.out.println("");
            Hashtable<String, Integer> row = table.get(rowKey);

            Enumeration<String> cols = row.keys();
            System.out.print(rowKey + " | ");
            while(cols.hasMoreElements()) {
                String colKey = cols.nextElement();
                Integer cellValue = row.get(colKey);
                System.out.print(cellValue + " ");
            }
        }
    }
}
