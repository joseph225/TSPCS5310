
import org.testng.annotations.Test;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.*;



public class TSP_DP {

    static int l ;

    @Test
    public void test() throws Exception {
        Integer[][] w =
                {{0, 12, 11, 16},
                        {15, 0, 15, 10},
                        {8, 14, 0, 18},
                        {9, 11, 17, 0}};

        int n = w[0].length;
        int k = (int) Math.pow(2, (n - 1));
        int[][] p =new int[n][k];
        System.out.println("TSP Minimum cost : "+ travel(n, w, p));
        System.out.print("TSP minimum cost path : ");
        System.out.print("V0->");
        print(0,p,k-1);
        System.out.print("V0\n");
    }

    @Test
    public void testMatrixNotInitialized() throws Exception {
        Integer[][] w = {};
        int n = w.length;
        int k = (int) Math.pow(2, (n - 1));
        int[][] p =new int[n][k];
        System.out.println("TSP Minimum cost : "+ travel(n, w, p));;
    }

    @Test
    public void testNegativeWeights() throws Exception {
        Integer[][] w =
                {{0, 12, 11, 16},
                        {15, 0, -15, 10},
                        {8, 14, 0, 18},
                        {9, 11, 17, 0}};
        int n = w[0].length;
        int k = (int) Math.pow(2, (n - 1));
        int[][] p =new int[n][k];
        System.out.println("TSP Minimum cost : "+ travel(n, w, p));;
    }

    @Test
    public void testWeightsIIAreZero() throws Exception {
        Integer[][] w =
                {{1, 12, 11, 16},
                        {15, 0, -15, 10},
                        {8, 14, 0, 18},
                        {9, 11, 17, 0}};
        int n = w[0].length;
        int k = (int) Math.pow(2, (n - 1));
        int[][] p =new int[n][k];
        System.out.println("TSP Minimum cost : "+ travel(n, w, p));;
    }

    @Test
    public void testSomeWeightsAreNotInitialized() throws Exception {
        Integer[][] w =
                {{null, 12, 11, 16},
                        {15, 0, -15, 10},
                        {8, 14, 0, 18},
                        {9, 11, 17, 0}};
        int n = w[0].length;
        int k = (int) Math.pow(2, (n - 1));
        int[][] p =new int[n][k];
        System.out.println("TSP Minimum cost : "+ travel(n, w, p));;
    }

    private static Integer[][] readFromFile(String fileName) {
        Scanner sc;
        try {
            sc = new Scanner(new BufferedReader(new FileReader(fileName)));
            String[] header = sc.nextLine().trim().split(",");
            int rows = header.length;
            int columns = rows;
            Integer [][] array = new Integer[rows][columns];
            while(sc.hasNextLine()) {
                for (int i=0; i < array.length; i++) {
                    String[] line = sc.nextLine().trim().split(",");
                    for (int j=0; j < line.length; j++) {
                        array[i][j] = Integer.parseInt(line[j]);
                    }
                }
            }
            return array;
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return new Integer[1][1];
    }

    private static int travel(int n, Integer[][] w, int[][] p) throws Exception {

        int numberOfSubsets = (int) Math.pow(2, (n - 1));
        int[][] D = new int[n][numberOfSubsets];

        for (int i = 1; i < n; i++) {
            D[i][0] = w[i][0];
        }

        if (n == 0) throw new Exception("Validation failed! Adjacency matrix not initialized");
        if (n < 2) throw new Exception("Validation failed! Incomplete graph");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (w[i][j] == null) {
                    throw new Exception("Validation failed! Few weights/distance not initialized");
                }
                if (w[i][j] < 0) {
                    throw new Exception("Validation failed! Negative weights/distance found");
                }
                if (i == j && w[i][i] != 0) {
                    throw new Exception("Validation failed! Weight (i,i) has to be zero");
                }
            }
        }

        for (int i = 1; i <= n - 2; i++) {
            for (int subset = 1; subset < numberOfSubsets; subset++) {
                if (length(subset) == i) {
                    for (int v = 1; v < n; v++) {
                        if (!containsI(subset, v - 1)) {
                            D[v][subset] = minimum(v, w, D, subset);
                            p[v][subset] = l;
                        }
                    }
                }
            }
        }
        int min = minimum(0, w, D, numberOfSubsets - 1);
        p[0][numberOfSubsets - 1] = l;
        return min;
    }

    private static int length(int l) {
        int length = 0;
        while (l != 0) {
            l = l & (l - 1);
            length++;
        }
        return length;
    }

    private static boolean containsI(int subset, int i) {
        int num = subset & ~(1 << (i));
        return  subset != (num & subset) ? true : false;
    }

    private static void print(int i, int[][] p, int n) {
        while (n > 0) {
            System.out.print("V" + p[i][n] + "->");
            i = p[i][n];
            n = n & ~(1 << (i - 1));
        }
    }

    private static int minimum(int v, Integer[][] w, int[][] D, int set) {
        int n = w.length;
        int[] m = new int[length(set)];
        int[] i = new int[length(set)];
        int index = 0;
        for (int j = 0; j < n - 1; j++) {
            if (containsI(set, j)) {
                int num = set & ~(1 << (j));
                num = set & num;
                m[index] = w[v][j + 1] + D[j + 1][num];
                i[index] = j + 1;
                index++;
            }
        }
        int minimum = m[0];
        l = i[0];
        for (int j = 1; j < length(set); j++){
            if (minimum > m[j]) {
                minimum = m[j];
                l = i[j];
            }
        }
        return minimum;
    }


    public static void main(String[] args) throws Exception {

        Random r = new Random();
        TreeMap<Integer, Long> sizeVsTime = new TreeMap<>();

        if(args.length > 0) {
            Integer[][] array = readFromFile(args[0]);
            int n = array[0].length;
            int k = (int) Math.pow(2, (n - 1));
            int[][] p = new int[n][k];
            Long startTime = System.nanoTime();
            System.out.println("TSP Minimum cost for path containing "+ n+" vertices is  "+ travel(n, array, p));
            Long endTime = System.nanoTime();
            sizeVsTime.put(n, endTime - startTime);
            System.out.print("TSP minimum cost path for path containing " + n + " vertices is ");
            System.out.print("V0->");
            print(0, p, k - 1);
            System.out.print("V0\n");

        } else {
            for (int i = 2; i < 25; i++) {
                Integer[][] w = new Integer[i][i];
                for (int j = 0; j < i; j++) {
                    for (int k = 0; k < i; k++) {
                        if (j == k) w[j][j] = 0;
                        else w[j][k] = r.nextInt(10);
                    }
                }
                int n = w[0].length;
                int k = (int) Math.pow(2, (n - 1));
                int[][] p = new int[n][k];
                Long startTime = System.nanoTime();
                travel(n, w, p);
                Long endTime = System.nanoTime();
                sizeVsTime.put(i, endTime - startTime);
                System.out.print("TSP minimum cost path for path containing " + n + " vertices is ");
                System.out.print("V0->");
                print(0, p, k - 1);
                System.out.print("V0\n");
            }
        }

        try (PrintWriter writer = new PrintWriter("output.csv")) {
            Set<Integer> keyid = sizeVsTime.keySet();
            StringBuilder sb = new StringBuilder();
            sb.append("No. of Vertices");
            sb.append(',');
            sb.append("Time(nano seconds)");
            sb.append('\n');
            writer.write(sb.toString());
            sb.setLength(0);

            for (Integer key : keyid) {
                sb.append(key.toString());
                sb.append(',');
                sb.append(sizeVsTime.get(key));
                sb.append('\n');
                writer.write(sb.toString());
                sb.setLength(0);
            }

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}




