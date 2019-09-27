package io.swagger.client.runner;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.swagger.client.measurement.Metrics;
import io.swagger.client.setup.ParamParser;

public class MultiThreadApp {

  private int numOfMaxThread;
  private int numOfMaxSkiers;
  private int numOfSkiLifts;
  private int numOfMeanLifts;
  private String serverAddress;

  private static double OVER_LAPPING_RATIO = 0.1;

  private double totalTime;
  private Metrics metrics;
  private long[] latencyResults;



  public MultiThreadApp(int numOfMaxThread, int numOfMaxSkiers, int numOfSkiLifts,
                        int numOfMeanLifts, String serverAddress) {
    this.numOfMaxThread = numOfMaxThread;
    this.numOfMaxSkiers = numOfMaxSkiers;
    this.numOfSkiLifts = numOfSkiLifts;
    this.numOfMeanLifts = numOfMeanLifts;
    this.serverAddress = serverAddress;
    this.metrics = new Metrics();
  }

  public static void main(String[] args) {
    ParamParser paramParser = new ParamParser(args);
    int numOfMaxThread = paramParser.getNumOfMaxThreads();
    int numOfMaxSkiers = paramParser.getNumOfMaxSkiers();
    int numOfSkiLifts = paramParser.getNumOfSkiLifts();
    int numOfMeanLifts = paramParser.getNumOfMeanSkiLifts();
    String serverAddress = paramParser.getServerAddress();

    MultiThreadApp app = new MultiThreadApp(numOfMaxThread, numOfMaxSkiers, numOfSkiLifts,
            numOfMeanLifts, serverAddress);

    try {
      app.startService();
    }  catch (InterruptedException e) {
      System.err.println("Exception when starting service");
      e.printStackTrace();
    }


  }

  public void startService() throws InterruptedException {

    String[] phases = {"start up", "peak", "cool down"};
    int[][] timeRanges = new int[][]{new int[]{1,90}, new int[]{91,360}, new int[]{361, 420}};
    int[] threadNums = new int[]{numOfMaxThread/4, numOfMaxThread, numOfMaxThread/4};

    CountDownLatch latch = new CountDownLatch(threadNums[0] + threadNums[1] + threadNums[2]);

    //start up phase
    long startTimeStamp = System.currentTimeMillis();
    CountDownLatch latch1 = new CountDownLatch((int)(threadNums[0] * OVER_LAPPING_RATIO));
    System.out.println("Phase startup started with countdown latch count " + (int)(threadNums[0] * OVER_LAPPING_RATIO));
    singlePhaseProcess(phases[0],numOfMaxThread/4, timeRanges[0], latch1, latch, (int)(numOfMeanLifts * 0.1));
    latch1.await();
    System.out.println("Phase startup finished");

    //peak phase
    CountDownLatch latch2 = new CountDownLatch((int)(threadNums[1] * OVER_LAPPING_RATIO));
    System.out.println("Phase peak started with countdown latch count " + (int)(threadNums[1] * OVER_LAPPING_RATIO));
    singlePhaseProcess(phases[1], numOfMaxThread, timeRanges[0], latch2, latch, (int)(numOfMeanLifts * 0.8));
    latch2.await();
    System.out.println("Phase peak finished");

    //cool down phase
    CountDownLatch latch3 = new CountDownLatch(threadNums[2]);
    System.out.println("Phase cool down started with countdown latch count " + threadNums[2]);
    singlePhaseProcess(phases[2], numOfMaxThread/4, timeRanges[0], latch3, latch, (int)(numOfMeanLifts * 0.1));
    latch3.await();
    System.out.println("Phase cool down finished");

    latch.await();
    long endTimeStamp = System.currentTimeMillis();
    this.totalTime = endTimeStamp - startTimeStamp;

  }

  private void singlePhaseProcess(String phaseName, int numOfThread, int[] timeRange,
                                 CountDownLatch phaseLatch, CountDownLatch totalLatch, int numOfRuns) throws InterruptedException {
    System.out.print("Phase " + phaseName + "starts. Generating " + numOfThread);
    long startTimeStamp = System.currentTimeMillis();
    makingRequests(numOfThread, timeRange, numOfMaxSkiers/numOfThread, phaseLatch, totalLatch, numOfRuns);
    long endTimeStamp = System.currentTimeMillis();
    System.out.println(phaseName + " completed in " + (endTimeStamp - startTimeStamp) + "ms");

  }

  private void makingRequests(int numOfThread, int[] timeRange, int numOfSkiers,
                         CountDownLatch phaseLatch, CountDownLatch totalLatch, int numOfRuns) {
    ExecutorService executor = Executors.newFixedThreadPool(numOfThread);
    int[] skierRange = new int[]{1, numOfSkiers};
    for(int i = 0; i < numOfThread; i++) {
      executor.submit(new Runner(serverAddress, timeRange, skierRange, phaseLatch, totalLatch, numOfRuns, numOfSkiLifts, metrics));
      skierRange[0] = skierRange[0] + numOfSkiers;
      skierRange[1] = skierRange[1] + numOfSkiers;
    }
    executor.shutdown();
  }

  public void mesureLatency(){
    Collections.sort(metrics.getLatency());
    Long total = Long.valueOf(0);
    for(Long data : metrics.getLatency()) total += data;
    int size = metrics.getLatency().size();
    if(size == 0) return;
    latencyResults[0] = total / size;//mean
    latencyResults[1] = metrics.getLatency().get(size / 2); // median
    latencyResults[2] =  (long)((metrics.getSuccessfulRequest().get() + metrics.getUnsuccessfulRequest().get()) / totalTime);
    latencyResults[3] = metrics.getLatency().get((int) Math.round(size * 0.99) - 1);// 99th
    latencyResults[4] = metrics.getLatency().get(size-1); //max response time
    printResult();
  }

  public void printResult(){
    System.out.println("Number of successful requests sent: " + metrics.getSuccessfulRequest());
    System.out.println("Number of unsuccessful requests sent: " + metrics.getUnsuccessfulRequest());
    System.out.println("Total run time (wall time) is : " + totalTime + "ms");
    System.out.println("=================================================");
    System.out.println("Latency related measurements: ");
    System.out.println("mean response time: " + latencyResults[0] + "ms");
    System.out.println("median response time: " + latencyResults[1] + "ms");
    System.out.println("throughput: " + latencyResults[2]);
    System.out.println("p99 (99th percentile) response time: " + latencyResults[3] + "ms");
    System.out.println("max response time: " + latencyResults[4] + "ms");
  }

}
