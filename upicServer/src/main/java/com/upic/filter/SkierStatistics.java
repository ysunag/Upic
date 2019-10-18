package com.upic.filter;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SkierStatistics {
  private AtomicLong maxLiftGetLatency;
  private AtomicLong maxLiftDayGetLatency;
  private AtomicLong maxLiftPostLatency;

  private AtomicLong totalLiftGetLatency;
  private AtomicLong totalLiftDayGetLatency;
  private AtomicLong totalLiftPostLatency;

  private AtomicInteger LiftGetNum;
  private AtomicInteger LiftDayGetNum;
  private AtomicInteger LiftPostNum;


  public SkierStatistics() {
    this.maxLiftGetLatency = new AtomicLong();
    this.maxLiftDayGetLatency = new AtomicLong();
    this.maxLiftPostLatency = new AtomicLong();

    this.totalLiftGetLatency = new AtomicLong();
    this.totalLiftDayGetLatency = new AtomicLong();
    this.totalLiftPostLatency = new AtomicLong();

    this.LiftGetNum = new AtomicInteger();
    this.LiftDayGetNum = new AtomicInteger();
    this.LiftPostNum = new AtomicInteger();
  }

  public AtomicLong getMaxLiftGetLatency() {
    return maxLiftGetLatency;
  }

  public AtomicLong getMaxLiftDayGetLatency() {
    return maxLiftDayGetLatency;
  }

  public AtomicLong getMaxLiftPostLatency() {
    return maxLiftPostLatency;
  }


  public AtomicLong getTotalLiftGetLatency() {
    return totalLiftGetLatency;
  }

  public AtomicLong getTotalLiftDayGetLatency() {
    return totalLiftDayGetLatency;
  }

  public AtomicLong getTotalLiftPostLatency() {
    return totalLiftPostLatency;
  }

  public AtomicInteger getLiftGetNum() {
    return LiftGetNum;
  }

  public AtomicInteger getLiftDayGetNum() {
    return LiftDayGetNum;
  }

  public AtomicInteger getLiftPostNum() {
    return LiftPostNum;
  }
}
