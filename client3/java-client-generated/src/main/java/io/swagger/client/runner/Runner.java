package io.swagger.client.runner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
  private int successCount;
  private int unsuccessCount;
  private List<Long> latencyList;

  public static final int RETRY_TIMES = 20;


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

    successCount = 0;
    unsuccessCount = 0;
    latencyList = new ArrayList<>();

    client.setBasePath(serverAddress);
  }

  public void run() {
    StringBuilder sb = new StringBuilder();
    System.out.println("Time starts " + timeRange[0] + ", number of requests is" + numOfRequests);
    for (int i = 0; i < numOfRequests; i++) {
      int[] ids = null;
      try {
        ids = makePostRequest(sb);
        if (issueGetRequest && ids != null) {
          makeGetRequest(sb, ids);
        }
      }
      catch(Exception e) {
        System.err.println("Exception when making single post request");
        e.printStackTrace();
      }

    }


    metrics.getSuccessfulRequest().getAndAdd(successCount);
    metrics.getUnsuccessfulRequest().getAndAdd(unsuccessCount);
    metrics.getLatency().addAll(latencyList);

    metrics.getRecord().add(sb.toString());
    //System.out.println("added record " + metrics.getRecord().size() + " : " + sb.toString());
    phaseLatch.countDown();
    totalLatch.countDown();
    long currentPhaseCount = phaseLatch.getCount();
    long currentTotalCount = totalLatch.getCount();
    System.out.println("currentPhaseCount: " + currentPhaseCount);
    System.out.println("currentTotalCount: " + currentTotalCount);

  }

  private int[] generateIds() {
    int[] res = new int[6];
    res[0] = ThreadLocalRandom.current().nextInt(skierRange[1] - skierRange[0] + 1) + skierRange[0];
    res[1] = ThreadLocalRandom.current().nextInt(timeRange[1] - timeRange[0] + 1) + timeRange[0];
    res[2] = ThreadLocalRandom.current().nextInt(numOfSkiLifts) + 1;
    res[3] = ThreadLocalRandom.current().nextInt(30) + 1;
    res[4] = ThreadLocalRandom.current().nextInt(10) + 2009;
    res[5] = ThreadLocalRandom.current().nextInt(365) + 1;
    return res;
  }

  private int[] makePostRequest(StringBuilder sb) throws Exception{
    long startTime = System.currentTimeMillis();

    LiftRide body = new LiftRide();
    int[] ids = null;

    int responseCode = 0;
    int i = 0;
    while (i < RETRY_TIMES && responseCode != 201) {
      try {
        ids = generateIds();
        int skierId = ids[0];
        int time = ids[1];
        int liftId = ids[2];
        int resortID = ids[3];
        String seasonID = Integer.toString(ids[4]);
        String dayID = Integer.toString(ids[5]);
        body.setLiftID(liftId);
        body.setTime(time);
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
     // metrics.getSuccessfulRequest().getAndIncrement();
      successCount++;
    } else {
      unsuccessCount++;
     // metrics.getUnsuccessfulRequest().getAndIncrement();
    }


    long endTime = System.currentTimeMillis();
    try {
      addSingleRequestMetrics(startTime, endTime, responseCode, "POST", sb);
    } catch (IOException e) {
      System.err.println("Exception when writing single request record");
      e.printStackTrace();
    }
    return responseCode == 201? ids: null;

  }

  private void makeGetRequest(StringBuilder sb, int[] ids) throws Exception{
    long startTime = System.currentTimeMillis();
    int skierId = ids[0];
    int resortID = ids[3];
    String seasonID = Integer.toString(ids[4]);
    String dayID = Integer.toString(ids[5]);

    int responseCode = 0;
    int res = 0;
    int i = 0;
    while (i < RETRY_TIMES && responseCode != 200 && res <= 0) {
      try {
        res = apiInstance.getSkierDayVertical(resortID, seasonID, dayID, skierId);
        responseCode = 200;
      } catch (ApiException e) {
        responseCode = e.getCode();
        System.err.println("Exception when calling SkierAPI#writeNewLiftRide. Error Code: " + e.getCode());
        e.printStackTrace();
      }
      i++;
    }
    if (responseCode == 200) {
      //metrics.getSuccessfulRequest().getAndIncrement();
      successCount++;
    } else {
      //metrics.getUnsuccessfulRequest().getAndIncrement();
      unsuccessCount++;
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
    //metrics.getLatency().add(latency);
    latencyList.add(latency);
    String singleRecord = startTime + "," + responseType + "," + latency + "," + responseCode + "\n";
    //System.out.println("single record: " + singleRecord);
    sb.append(singleRecord);
  }

}
