//package com.upic.servlet;
//
//import java.sql.Timestamp;
//
//import javax.persistence.Basic;
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.Id;
//import javax.persistence.Table;
//
//@Entity
//@Table(name = "lift", schema = "upic_schema", catalog = "")
//public class LiftEntity {
//  private int liftId;
//  private int resortId;
//  private String seasonId;
//  private String dayId;
//  private int skierId;
//  private int liftTime;
//  private Timestamp createdAt;
//
//  @Id
//  @GeneratedValue
//  @Column(name = "lift_id", nullable = false)
//  public int getLiftId() {
//    return liftId;
//  }
//
//  public void setLiftId(int liftId) {
//    this.liftId = liftId;
//  }
//
//  @Basic
//  @Column(name = "resort_id", nullable = false)
//  public int getResortId() {
//    return resortId;
//  }
//
//  public void setResortId(int resortId) {
//    this.resortId = resortId;
//  }
//
//  @Basic
//  @Column(name = "season_id", nullable = false)
//  public String getSeasonId() {
//    return seasonId;
//  }
//
//  public void setSeasonId(String seasonId) {
//    this.seasonId = seasonId;
//  }
//
//  @Basic
//  @Column(name = "day_id", nullable = false)
//  public String getDayId() {
//    return dayId;
//  }
//
//  public void setDayId(String dayId) {
//    this.dayId = dayId;
//  }
//
//  @Basic
//  @Column(name = "skier_id", nullable = false)
//  public int getSkierId() {
//    return skierId;
//  }
//
//  public void setSkierId(int skierId) {
//    this.skierId = skierId;
//  }
//
//  @Basic
//  @Column(name = "lift_time", nullable = false)
//  public int getLiftTime() {
//    return liftTime;
//  }
//
//  public void setLiftTime(int liftTime) {
//    this.liftTime = liftTime;
//  }
//
//  @Basic
//  @Column(name = "created_at", nullable = false)
//  public Timestamp getCreatedAt() {
//    return createdAt;
//  }
//
//  public void setCreatedAt(Timestamp createdAt) {
//    this.createdAt = createdAt;
//  }
//
//  @Override
//  public boolean equals(Object o) {
//    if (this == o) return true;
//    if (o == null || getClass() != o.getClass()) return false;
//
//    LiftEntity that = (LiftEntity) o;
//
//    if (liftId != that.liftId) return false;
//    if (resortId != that.resortId) return false;
//    if (skierId != that.skierId) return false;
//    if (liftTime != that.liftTime) return false;
//    if (seasonId != null ? !seasonId.equals(that.seasonId) : that.seasonId != null) return false;
//    if (dayId != null ? !dayId.equals(that.dayId) : that.dayId != null) return false;
//    if (createdAt != null ? !createdAt.equals(that.createdAt) : that.createdAt != null)
//      return false;
//
//    return true;
//  }
//
//  @Override
//  public int hashCode() {
//    int result = liftId;
//    result = 31 * result + resortId;
//    result = 31 * result + (seasonId != null ? seasonId.hashCode() : 0);
//    result = 31 * result + (dayId != null ? dayId.hashCode() : 0);
//    result = 31 * result + skierId;
//    result = 31 * result + liftTime;
//    result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
//    return result;
//  }
//}
