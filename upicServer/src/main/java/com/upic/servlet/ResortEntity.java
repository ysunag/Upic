package com.upic.servlet;

import java.sql.Timestamp;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "resort", schema = "upic_schema", catalog = "")
public class ResortEntity {
  private int resortId;
  private String resortName;
  private Timestamp createdAt;

  @Id
  @Column(name = "resort_id", nullable = false)
  public int getResortId() {
    return resortId;
  }

  public void setResortId(int resortId) {
    this.resortId = resortId;
  }

  @Basic
  @Column(name = "resort_name", nullable = false)
  public String getResortName() {
    return resortName;
  }

  public void setResortName(String resortName) {
    this.resortName = resortName;
  }

  @Basic
  @Column(name = "created_at", nullable = false)
  public Timestamp getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Timestamp createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ResortEntity that = (ResortEntity) o;

    if (resortId != that.resortId) return false;
    if (resortName != null ? !resortName.equals(that.resortName) : that.resortName != null)
      return false;
    if (createdAt != null ? !createdAt.equals(that.createdAt) : that.createdAt != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = resortId;
    result = 31 * result + (resortName != null ? resortName.hashCode() : 0);
    result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
    return result;
  }
}
