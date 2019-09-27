package io.swagger.client.runner;

import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.ResortsApi;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.measurement.Metrics;
import io.swagger.client.model.LiftRide;
import io.swagger.client.model.ResponseMsg;
import io.swagger.client.model.SeasonsList;

public class Runner implements Runnable {
  private String serverAddress;
  private int[] timeRange;
  private int[] skierRange;
  private CountDownLatch phaseLatch;
  private CountDownLatch totalLatch;
  private int numOfRuns;
  private int numOfSkiLifts;
  private Metrics metrics;
  private SkiersApi apiInstance;


  public Runner(String serverAddress, int[] timeRange, int[] skierRange, CountDownLatch phaseLatch,
                CountDownLatch totalLatch, int numOfRuns, int numOfSkiLifts, Metrics metrics) {
    this.serverAddress = serverAddress;
    this.timeRange = timeRange;
    this.skierRange = skierRange;
    this.phaseLatch = phaseLatch;
    this.totalLatch = totalLatch;
    this.numOfRuns = numOfRuns;
    this.numOfSkiLifts = numOfSkiLifts;
    this.metrics = metrics;

    SkiersApi apiInstance = new SkiersApi();
    ApiClient client = apiInstance.getApiClient();
    client.setBasePath(serverAddress);
  }

  public void run() {
    for (int i = 0; i < numOfRuns; i++) {
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




//    try {
//      SeasonsList result = apiInstance.getResortSeasons(resortID);
//      System.out.println(result);
//    } catch (ApiException e) {
//      System.err.println("Exception when calling ResortsApi#getResortSeasons");
//      e.printStackTrace();
//    }
  }

  private void makePostRequest(int skierId, int time, int liftId) throws Exception{
    long startTime = System.currentTimeMillis();
     //TODO: which api to call?
    int resortID = 0;
    String seasonID = "0";
    String dayID = "0";
    LiftRide body = new LiftRide();
    body.setLiftID(liftId);
    body.setTime(time);

    String responseCode = "";
    try {
      apiInstance.writeNewLiftRide(body, resortID, seasonID, dayID, skierId);
//      if(response.getStatus() == 201) metrics.successfulRequest.getAndIncrement();
//    //System.out.println("Post " + response.getStatus());
//    if (response.getStatus() != 201) {
//      metrics.successfulRequest.getAndIncrement();
//      logger.error(null, "This is the log message", throwable);
//    }
    } catch (ApiException e) {
      System.err.println("Exception when calling SkierAPI#writeNewLiftRide");
      e.printStackTrace();
    }

    long endTime = System.currentTimeMillis();
    try {
      addSingleRequestMetrics(startTime, endTime, responseCode, "POST");
    } catch (IOException e) {
      System.err.println("Exception when writing single request record to csv");
      e.printStackTrace();
    }

  }

  public void addSingleRequestMetrics(long startTime, long endTime, String responseCode,
                                      String responseType) throws IOException{
//    int duration = (int)((endTime - metrics.globalStartTime) / 1000);
//    metrics.graph.putIfAbsent(duration, 1);
//    metrics.graph.put(duration, metrics.graph.get(duration) + 1);
    long latency = endTime - startTime;
    metrics.getLatency().add(latency);
    try (ICsvListWriter listWriter = new CsvListWriter(new FileWriter("/Users/yang/Documents/NEU/CS6650/java-client-generated/data.csv"),
            CsvPreference.STANDARD_PREFERENCE)){
       listWriter.write(startTime, responseType, latency, responseCode);
      } catch (IOException e) {
      System.err.println("Exception when writing single request record to csv");
      e.printStackTrace();
    }
  }

}
