package com.upic.filter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SkierStatistics {
//  private List<Long> liftGetRecord;
//  private List<Long> liftDayGetRecord;
//  private List<Long> liftPostRecord;
  private List<String> records;


  public SkierStatistics() {
//    this.liftGetRecord = Collections.synchronizedList(new LinkedList<Long>());
//    this.liftDayGetRecord = Collections.synchronizedList(new LinkedList<Long>());
//    this.liftPostRecord = Collections.synchronizedList(new LinkedList<Long>());
    this.records = Collections.synchronizedList(new LinkedList<String>());
  }

//  public List<Long> getLiftGetRecord() {
//    return liftGetRecord;
//  }
//
//  public List<Long> getLiftDayGetRecord() {
//    return liftDayGetRecord;
//  }
//
//  public List<Long> getLiftPostRecord() {
//    return liftPostRecord;
//  }
   public List<String> getRecords() {
  return records;
}
}
