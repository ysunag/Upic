package com.upic.filter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SkierStatistics {
  private List<String> liftGetRecord;
  private List<String> liftDayGetRecord;
  private List<String> liftPostRecord;
//  private List<String> records;


  public SkierStatistics() {
    this.liftGetRecord = Collections.synchronizedList(new LinkedList<String>());
    this.liftDayGetRecord = Collections.synchronizedList(new LinkedList<String>());
    this.liftPostRecord = Collections.synchronizedList(new LinkedList<String>());
//    this.records = Collections.synchronizedList(new LinkedList<String>());
  }

  public List<String> getLiftGetRecord() {
    return liftGetRecord;
  }

  public List<String> getLiftDayGetRecord() {
    return liftDayGetRecord;
  }

  public List<String> getLiftPostRecord() {
    return liftPostRecord;
  }
//   public List<String> getRecords() {
//  return records;
//}
}
