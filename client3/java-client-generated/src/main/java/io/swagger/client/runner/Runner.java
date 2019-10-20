package io.swagger.client.runner;

import java.io.IOException;
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
  private boolean issueGetRequest;

  public static final int RETRY_TIMES = 5;


  public Runner(String serverAddress, int[] timeRange, int[] skierRange, CountDownLatch phaseLatch,
                CountDownLatch totalLatch, int numOfRequests, int numOfSkiLifts, Metrics metrics, boolean issueGetRequest) {
    this.timeRange = timeRange;
    this.skierRange = skierRange;
    this.phaseLatch = phaseLatch;
    this.totalLatch = totalLatch;
    this.numOfRequests = numOfRequests;
    this.numOfSkiLifts = numOfSkiLifts;
    this.metrics = metrics;
    this.issueGetRequest = issueGetRequest;
    apiInstance = new SkiersApi();
    ApiClient client = apiInstance.getApiClient();

    client.setBasePath(serverAddress);
  }

  public void run() {
    StringBuilder sb = new StringBuilder();
    System.out.println("Time starts " + timeRange[0] + ", number of requests is" + numOfRequests);
    for (int i = 0; i < numOfRequests; i++) {
      int skierId = ThreadLocalRandom.current().nextInt(skierRange[1] - skierRange[0] + 1) + skierRange[0];
      int time = ThreadLocalRandom.current().nextInt(timeRange[1] - timeRange[0] + 1) + timeRange[0];
      int liftId = ThreadLocalRandom.current().nextInt(numOfSkiLifts) + 1;
      try {
        makePostRequest(skierId, time, liftId, sb);
        if (issueGetRequest) {
          makeGetRequest(skierId, time, liftId, sb);
        }
      }
      catch(Exception e) {
        System.err.println("Exception when making single post request");
        e.printStackTrace();
      }

    }
    metrics.getRecord().add(sb.toString());
    //System.out.println("added record " + metrics.getRecord().size() + " : " + sb.toString());
    phaseLatch.countDown();
    totalLatch.countDown();
    long currentPhaseCount = phaseLatch.getCount();
    long currentTotalCount = totalLatch.getCount();
    System.out.println("currentPhaseCount: " + currentPhaseCount);
    System.out.println("currentTotalCount: " + currentTotalCount);

  }

  private void makePostRequest(int skierId, int time, int liftId, StringBuilder sb) throws Exception{
    long startTime = System.currentTimeMillis();
    int resortID = 10;
    String seasonID = "2019";
    String dayID = "20";
    LiftRide body = new LiftRide();
    body.setLiftID(liftId);
    body.setTime(time);

    int responseCode = 0;
    int i = 0;
    while (i < RETRY_TIMES && responseCode != 201) {
      try {
        apiInstance.writeNewLiftRide(body, resortID, seasonID, dayID, skierId);

        responseCode = 201;
      } catch (ApiException e) {

        responseCode = e.getCode();
        System.err.println("Exception when calling SkierAPI#writeNewLiftRide. Error Code: " + e.getCode());
        e.printStackTrace();
      }
      i++;
    }
    if (responseCode == 201) {
      metrics.getSuccessfulRequest().getAndIncrement();
    } else {
      metrics.getUnsuccessfulRequest().getAndIncrement();
    }


    long endTime = System.currentTimeMillis();
    try {
      addSingleRequestMetrics(startTime, endTime, responseCode, "POST", sb);
    } catch (IOException e) {
      System.err.println("Exception when writing single request record");
      e.printStackTrace();
    }

  }

  private void makeGetRequest(int skierId, int time, int liftId, StringBuilder sb) throws Exception{
    long startTime = System.currentTimeMillis();
    int resortID = 10;
    String seasonID = "2019";
    String dayID = "20";

    int responseCode = 0;
    int i = 0;
    while (i < RETRY_TIMES && responseCode != 200) {
      try {
        apiInstance.getSkierDayVertical(resortID, seasonID, dayID, skierId);
        responseCode = 200;
      } catch (ApiException e) {
        responseCode = e.getCode();
        System.err.println("Exception when calling SkierAPI#writeNewLiftRide. Error Code: " + e.getCode());
        e.printStackTrace();
      }
      i++;
    }
    if (responseCode == 200) {
      metrics.getSuccessfulRequest().getAndIncrement();
    } else {
      metrics.getUnsuccessfulRequest().getAndIncrement();
    }

    long endTime = System.currentTimeMillis();
    try {
      addSingleRequestMetrics(startTime, endTime, responseCode, "GET", sb);
    } catch (IOException e) {
      System.err.println("Exception when writing single request record");
      e.printStackTrace();
    }

  }

  public void addSingleRequestMetrics(long startTime, long endTime, int responseCode,
                                      String responseType, StringBuilder sb) throws IOException{

    long latency = endTime - startTime;
    //System.out.println("latency: " + latency);
    metrics.getLatency().add(latency);
    String singleRecord = startTime + "," + responseType + "," + latency + "," + responseCode + "\n";
    //System.out.println("single record: " + singleRecord);
    sb.append(singleRecord);
  }

}
