package com.upic.filter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SkierStatistics {
  private List<Long> liftGetRecord;
  private List<Long> liftDayGetRecord;
  private List<Long> liftPostRecord;


  public SkierStatistics() {
    this.liftGetRecord = Collections.synchronizedList(new LinkedList<Long>());
    this.liftDayGetRecord = Collections.synchronizedList(new LinkedList<Long>());
    this.liftPostRecord = Collections.synchronizedList(new LinkedList<Long>());
  }

  public List<Long> getLiftGetRecord() {
    return liftGetRecord;
  }

  public List<Long> getLiftDayGetRecord() {
    return liftDayGetRecord;
  }

  public List<Long> getLiftPostRecord() {
    return liftPostRecord;
  }
}
