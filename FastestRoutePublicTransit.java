/**
 * Public Transit
 * Author: Nehal Patel and Carolyn Yao
 * Does this compile? Y
 */

/**
 * This class contains solutions to the Public, Public Transit problem in the
 * shortestTimeToTravelTo method. There is an existing implementation of a
 * shortest-paths algorithm. As it is, you can run this class and get the solutions
 * from the existing shortest-path algorithm.
 */
public class FastestRoutePublicTransit {

  //NOTE: 
  //The function only calculates time AFTER arrival to station S.
  //In other words, the cost to get to station S is 0 (not startTime).
  //To include the startTime cost for the shortest time to T, just add
  //startTime to the calculated shortest time to T from S.

  /**
   * The algorithm that could solve for shortest travel time from a station S
   * to a station T given various tables of information about each edge (u,v)
   *
   * @param S the s th vertex/station in the transit map, start From
   * @param T the t th vertex/station in the transit map, end at
   * @param startTime the start time in terms of number of minutes from 5:30am
   * @param lengths lengths[u][v] The time it takes for a train to get between two adjacent stations u and v
   * @param first first[u][v] The time of the first train that stops at u on its way to v, int in minutes from 5:30am
   * @param freq freq[u][v] How frequently is the train that stops at u on its way to v
   * @return shortest travel time between S and T
   */
  public int myShortestTravelTime(
    int S,
    int T,
    int startTime,
    int[][] lengths,
    int[][] first,
    int[][] freq
  ) {
    //Edge case check if no verticies provided or dimension mismatch on any matrix
    if(!validDimensions(lengths, first, freq))
      throw new Error("No verticies to process or dimension mismatch between lengths, freq and first.");
    
    int numOfVerticies = lengths[0].length;

    //NOTE: shortestTimes keeps track of shortest time AFTER arrival to S, not including startTime
    int[] shortestTimes = new int[numOfVerticies];
    Boolean[] checked = new Boolean[numOfVerticies];

    for(int i = 0; i < numOfVerticies; i++){
      shortestTimes[i] = Integer.MAX_VALUE;
      checked[i] = false;
    }
    
    shortestTimes[0] = 0;

    //Use !checked[T] to terminate loop once we have checked the target station
    for(int i = 0; i < numOfVerticies && !checked[T]; i++){
      int u = findNextToProcess(shortestTimes, checked);

      for(int v = 0; v < numOfVerticies; v++){
        int lenEdge = lengths[u][v];

        //Edge doesn't exist or v is already checked
        if(lenEdge == Integer.MAX_VALUE || checked[v])
          continue;
        
        //Used to calculate the next available train from u to v at the current shortest time
        int currentTime = shortestTimes[u] + startTime;
        int firstTrainAvail = first[u][v];
        int frequency = freq[u][v];
        int trainIndx = nextTrainIndex(currentTime, firstTrainAvail, frequency);

        //Calculate the cost from u to v
        int waitTime = firstTrainAvail + (trainIndx * frequency) - currentTime;
        int minCostUToV = waitTime + lenEdge + shortestTimes[u];

        //Update path from S to v if u to v has a shorter path
        shortestTimes[v] = Math.min(shortestTimes[v], minCostUToV);
      }

      checked[u] = true;
    }
    printShortestTimes(shortestTimes);
    return shortestTimes[T];
  }

  private boolean validDimensions(int[][] lengths, int[][] first, int[][] freq){
    int n = lengths.length;

    return n > 0 && lengths[0].length == n &&
           first.length == n && first[0].length == n &&
           freq.length == n && freq[0].length == n;
  }

  /**
   * Calculates the next available train(index). 
   * 
   * @param arrivalTime: time of arrival to station
   * @param firstAvailTrain: Time of first train on that station
   * @param freq: The frequency at which trains come to that station
   * 
   * @return The ceiling of ((arrivalTime - firstAvailTrain) / freq) if value is >= 0. If not,
   *         will return 0 (indicating that the person arrived earlier than the firstAvailTrain time)
   */

  public int nextTrainIndex(int arrivalTime, int firstAvailTrain, int freq){
    double nextTrain = (arrivalTime - firstAvailTrain) / ((double) freq);

    return nextTrain <= 0 ? 0 : (int) Math.ceil(nextTrain);

  }
  /**
   * Finds the vertex with the minimum time from the source that has not been
   * processed yet.
   * @param times The shortest times from the source
   * @param processed boolean array tells you which vertices have been fully processed
   * @return the index of the vertex that is next vertex to process
   */
  public int findNextToProcess(int[] times, Boolean[] processed) {
    int min = Integer.MAX_VALUE;
    int minIndex = -1;

    for (int i = 0; i < times.length; i++) {
      if (processed[i] == false && times[i] <= min) {
        min = times[i];
        minIndex = i;
      }
    }
    return minIndex;
  }

  public void printShortestTimes(int times[]) {
    System.out.println("Vertex Distances (time) from Source");
    for (int i = 0; i < times.length; i++)
        System.out.println(i + ": " + times[i] + " minutes");
  }

  /**
   * Given an adjacency matrix of a graph, implements
   * @param graph The connected, directed graph in an adjacency matrix where
   *              if graph[i][j] != 0 there is an edge with the weight graph[i][j]
   * @param source The starting vertex
   */
  public void shortestTime(int graph[][], int source) {
    int numVertices = graph[0].length;

    // This is the array where we'll store all the final shortest times
    int[] times = new int[numVertices];

    // processed[i] will true if vertex i's shortest time is already finalized
    Boolean[] processed = new Boolean[numVertices];

    // Initialize all distances as INFINITE and processed[] as false
    for (int v = 0; v < numVertices; v++) {
      times[v] = Integer.MAX_VALUE;
      processed[v] = false;
    }

    // Distance of source vertex from itself is always 0
    times[source] = 0;

    // Find shortest path to all the vertices
    for (int count = 0; count < numVertices - 1 ; count++) {
      // Pick the minimum distance vertex from the set of vertices not yet processed.
      // u is always equal to source in first iteration.
      // Mark u as processed.
      int u = findNextToProcess(times, processed);
      processed[u] = true;

      // Update time value of all the adjacent vertices of the picked vertex.
      for (int v = 0; v < numVertices; v++) {
        // Update time[v] only if is not processed yet, there is an edge from u to v,
        // and total weight of path from source to v through u is smaller than current value of time[v]
        if (!processed[v] && graph[u][v]!=0 && times[u] != Integer.MAX_VALUE && times[u]+graph[u][v] < times[v]) {
          times[v] = times[u] + graph[u][v];
        }
      }
    }

    printShortestTimes(times);
  }

  public static void main (String[] args) {
    /* length(e) */
    int lengthTimeGraph[][] = new int[][]{
      {0, 4, 0, 0, 0, 0, 0, 8, 0},
      {4, 0, 8, 0, 0, 0, 0, 11, 0},
      {0, 8, 0, 7, 0, 4, 0, 0, 2},
      {0, 0, 7, 0, 9, 14, 0, 0, 0},
      {0, 0, 0, 9, 0, 10, 0, 0, 0},
      {0, 0, 4, 14, 10, 0, 2, 0, 0},
      {0, 0, 0, 0, 0, 2, 0, 1, 6},
      {8, 11, 0, 0, 0, 0, 1, 0, 7},
      {0, 0, 2, 0, 0, 0, 6, 7, 0}
    };
    FastestRoutePublicTransit t = new FastestRoutePublicTransit();
    t.shortestTime(lengthTimeGraph, 0);

    // You can create a test case for your implemented method for extra credit below

    System.out.println("#### TEST CASES USING myShortestTravelTime ####");
    // NOTE: I used Infinity instead of 0 to represent no edge
    int inf = Integer.MAX_VALUE;

    /**
     * TEST ONE:
     * Very basic directed graph 0 -> 1 -> 2
     * 
     * With 0 as source 0 ~> 1 is 5, and 0 ~> 2 is 8
     */

    int lenTime1[][] = new int[][]{
      {inf, 3, inf},
      {inf, inf, 2},
      {inf, inf, inf}
    };

    int firstTime1[][] = new int[][]{
      {inf, 0, inf},
      {inf, inf, 4},
      {inf, inf, inf},
    };

    int freqTime1[][] = new int[][]{
      {inf, 3, inf},
      {inf, inf, 3},
      {inf, inf, inf}
    };

    int startTime1 = 7;

    //Should print 5
    System.out.println("(TEST ONE) cost from 0 ~> 1: " +  t.myShortestTravelTime(0, 1, startTime1, lenTime1, firstTime1, freqTime1));
    
    //Should print 8
    System.out.println("(TEST ONE) cost from 0 ~> 2: " +  t.myShortestTravelTime(0, 2, startTime1, lenTime1, firstTime1, freqTime1));
   
    /**
   * TEST TWO:
   * More complex, look at test2.jpg file for drawing
   * Should return 29
   * Num of stations: 10
   * Num of train routes: 12
   * 
   */

   int lenTime2[][] = new int[][]{
     {inf, 10, 12, 50, inf, inf, inf, inf, inf, inf},
     {inf, inf, inf, inf, inf, 3, inf, inf, inf ,inf},
     {inf, inf, inf, inf, 2, inf, inf, inf, inf, inf},
     {inf, inf, inf, inf, inf, inf, inf, inf, 15, inf},
     {inf, inf, inf, inf, inf, inf, inf, inf, 3, inf},
     {inf, inf, inf, inf, 2, inf, 4, inf, inf, inf},
     {inf, inf, inf, inf, inf, inf, inf, 2, inf, inf},
     {inf, inf, inf, inf, inf, inf, inf, inf, inf, 4},
     {inf, inf, inf, inf, inf, inf, inf, inf, inf, 4},
     {inf, inf, inf, inf, inf, inf, inf, 2, inf, inf}
    };

   int firstTime2[][] = new int[][]{
      {inf, 3, 0, 10, inf, inf, inf, inf, inf, inf},
      {inf, inf, inf, inf, inf, 11, inf, inf, inf ,inf},
      {inf, inf, inf, inf, 7, inf, inf, inf, inf, inf},
      {inf, inf, inf, inf, inf, inf, inf, inf, 13, inf},
      {inf, inf, inf, inf, inf, inf, inf, inf, 25, inf},
      {inf, inf, inf, inf, 3, inf, 3, inf, inf, inf},
      {inf, inf, inf, inf, inf, inf, inf, 7, inf, inf},
      {inf, inf, inf, inf, inf, inf, inf, inf, inf, 9},
      {inf, inf, inf, inf, inf, inf, inf, inf, inf, 13},
      {inf, inf, inf, inf, inf, inf, inf, 17, inf, inf}
    };

    int freqTime2[][] = new int[][]{
      {inf, 3, 2, 11, inf, inf, inf, inf, inf, inf},
      {inf, inf, inf, inf, inf, 2, inf, inf, inf ,inf},
      {inf, inf, inf, inf, 1, inf, inf, inf, inf, inf},
      {inf, inf, inf, inf, inf, inf, inf, inf, 7, inf},
      {inf, inf, inf, inf, inf, inf, inf, inf, 5, inf},
      {inf, inf, inf, inf, 2, inf, 7, inf, inf, inf},
      {inf, inf, inf, inf, inf, inf, inf, 2, inf, inf},
      {inf, inf, inf, inf, inf, inf, inf, inf, inf, 4},
      {inf, inf, inf, inf, inf, inf, inf, inf, inf, 8},
      {inf, inf, inf, inf, inf, inf, inf, 3, inf, inf}
    };

    int startTime2 = 9;

    //Should print 22
    System.out.println("(TEST TWO) cost from 0 ~> 7: " +  t.myShortestTravelTime(0, 7, startTime2, lenTime2, firstTime2, freqTime2));
    
    //Should print 24
    System.out.println("(TEST TWO) cost from 0 ~> 9: " +  t.myShortestTravelTime(0, 9, startTime2, lenTime2, firstTime2, freqTime2));
  }
}
