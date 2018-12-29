import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App {
    public static void main(String[] args) {
        Solution solution = new Solution();

        //TEST CASE 2
        List<List<Integer>> lot2 = new ArrayList<>();
        lot2.add(Arrays.asList(-1,1,0,1));
        lot2.add(Arrays.asList(1,1,0,9));
        lot2.add(Arrays.asList(0,1,1,1));
        lot2.add(Arrays.asList(9,1,0,9));

        //TEST CASE 1
        List<List<Integer>> lot = new ArrayList<>();
        lot.add(Arrays.asList(-1,1,0,1,1));
        lot.add(Arrays.asList(0,1,0,9,0));
        lot.add(Arrays.asList(1,1,1,1,9));
        lot.add(Arrays.asList(9,9,0,1,1));
        lot.add(Arrays.asList(1,1,0,9,0));

        //TEST CASE 3
        List<List<Integer>> lot3 = new ArrayList<>();
        lot3.add(Arrays.asList(-1,1,0,1,1,9,0));
        lot3.add(Arrays.asList(0,1,0,9,0,1,1));
        lot3.add(Arrays.asList(1,1,1,1,9,1,9));
        lot3.add(Arrays.asList(9,9,0,1,1,0,1));
        lot3.add(Arrays.asList(1,1,0,9,0,9,1));
        lot3.add(Arrays.asList(0,1,0,9,0,1,0));
        lot3.add(Arrays.asList(0,1,0,9,0,1,1));


        System.out.println("Starting obstacle removal...");
        long startTime = System.currentTimeMillis();
        int result = solution.removeObstacle(lot3.size(),lot3.get(0).size(),lot3);
        long endTime = System.currentTimeMillis();
        System.out.println("");
        System.out.println("FINAL RESULT: " + result);
        System.out.println("Execution took " + (endTime-startTime) + "ms");

        System.out.println("Starting obstacle removal...");

    }
}
