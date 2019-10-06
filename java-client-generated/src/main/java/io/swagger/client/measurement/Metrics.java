package io.swagger.client.measurement;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Metrics {
  private AtomicLong successfulRequest;
  private AtomicLong unsuccessfulRequest;
  private List<Long> latency;
  private List<String> record;
//  private ConcurrentHashMap<Integer, Integer> graph;
//  private Long globalStartTime;

  public List<String> getRecord() {
    return record;
  }


  public AtomicLong getSuccessfulRequest() {
    return successfulRequest;
  }

  public AtomicLong getUnsuccessfulRequest() {
    return unsuccessfulRequest;
  }

  public List<Long> getLatency() {
    return latency;
  }

//  public ConcurrentHashMap<Integer, Integer> getGraph() {
//    return graph;
//  }
//
//  public Long getGlobalStartTime() {
//    return globalStartTime;
//  }




  public Metrics(){
    this.latency = Collections.synchronizedList(new LinkedList<Long>());
//    this.graph = new ConcurrentHashMap<>();
//    globalStartTime = System.currentTimeMillis();
    this.record = Collections.synchronizedList(new LinkedList<String>());
    successfulRequest = new AtomicLong();
    unsuccessfulRequest = new AtomicLong();
  }

}
