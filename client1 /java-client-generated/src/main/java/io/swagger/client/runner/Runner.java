package io.swagger.client.runner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.measurement.Metrics;
import io.swagger.client.model.LiftRide;

public class Runner implements Runnable {
  private int[] timeRange;
  private int[] skierRange;
  private CountDownLatch phaseLatch;
  private CountDownLatch totalLatch;
  private int numOfRequests;
  private int numOfSkiLifts;
  private Metrics metrics;
  private SkiersApi apiInstance;


  public Runner(String serverAddress, int[] timeRange, int[] skierRange, CountDownLatch phaseLatch,
                CountDownLatch totalLatch, int numOfRequests, int numOfSkiLifts, Metrics metrics) {
    this.timeRange = timeRange;
    this.skierRange = skierRange;
    this.phaseLatch = phaseLatch;
    this.totalLatch = totalLatch;
    this.numOfRequests = numOfRequests;
    this.numOfSkiLifts = numOfSkiLifts;
    this.metrics = metrics;
    apiInstance = new SkiersApi();
    ApiClient client = apiInstance.getApiClient();
    client.setBasePath(serverAddress);
  }

  public void run() {
    System.out.println("Time starts " + timeRange[0] + ", number of requests is" + numOfRequests);
    for (int i = 0; i < numOfRequests; i++) {
      int skierId = ThreadLocalRandom.current().nextInt(skierRange[1] - skierRange[0] + 1) + skierRange[0];
      int time = ThreadLocalRandom.current().nextInt(timeRange[1] - timeRange[0] + 1) + timeRange[0];
      int liftId = ThreadLocalRandom.current().nextInt(numOfSkiLifts) + 1;
      try {
        makePostRequest(skierId, time, liftId);
      }
      catch(Exception e) {
        System.err.println("Exception when making single post request");
        e.printStackTrace();
      }

    }
    phaseLatch.countDown();
    totalLatch.countDown();
    long currentPhaseCount = phaseLatch.getCount();
    long currentTotalCount = totalLatch.getCount();
    System.out.println("currentPhaseCount: " + currentPhaseCount);
    System.out.println("currentTotalCount: " + currentTotalCount);

  }

  private void makePostRequest(int skierId, int time, int liftId) throws Exception{
    int resortID = 10;
    String seasonID = "2019";
    String dayID = "20";
    LiftRide body = new LiftRide();
    body.setLiftID(liftId);
    body.setTime(time);

    try {
      apiInstance.writeNewLiftRide(body, resortID, seasonID, dayID, skierId);
      metrics.getSuccessfulRequest().getAndIncrement();
    } catch (ApiException e) {
      metrics.getUnsuccessfulRequest().getAndIncrement();
      System.err.println("Exception when calling SkierAPI#writeNewLiftRide. Error Code: " + e.getCode());
      e.printStackTrace();
    }

  }

}
