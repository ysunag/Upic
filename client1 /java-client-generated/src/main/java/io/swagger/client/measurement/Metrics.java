package io.swagger.client.measurement;

import java.util.concurrent.atomic.AtomicLong;

public class Metrics {
  private AtomicLong successfulRequest;
  private AtomicLong unsuccessfulRequest;


  public AtomicLong getSuccessfulRequest() {
    return successfulRequest;
  }
  public AtomicLong getUnsuccessfulRequest() {
    return unsuccessfulRequest;
  }


  public Metrics(){
    successfulRequest = new AtomicLong();
    unsuccessfulRequest = new AtomicLong();
  }

}
