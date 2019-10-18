package com.upic.filter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ResortsStatistics {
  private List<Long> ResortsGetRecord;
  private List<Long> SeasonsGetRecord;
  private List<Long> SeasonsPostRecord;


  public ResortsStatistics() {
    this.ResortsGetRecord = Collections.synchronizedList(new LinkedList<Long>());
    this.SeasonsGetRecord = Collections.synchronizedList(new LinkedList<Long>());
    this.SeasonsPostRecord = Collections.synchronizedList(new LinkedList<Long>());
  }

  public List<Long> getResortsGetRecord() {
    return ResortsGetRecord;
  }

  public List<Long> getSeasonsGetRecord() {
    return SeasonsGetRecord;
  }

  public List<Long> getSeasonsPostRecord() {
    return SeasonsPostRecord;
  }
}
