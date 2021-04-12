package com.gbsoft.smartpatient.data;

import androidx.annotation.Keep;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

@Keep
public class Appointment {
    private long id;
    private String name;
    private String patientId;
    private String patientName;
    private String hpId;
    private String hpName;
    private LocalDateTime scheduledTime;
    private Boolean isApproved;

    public Appointment() {
    }

    public Appointment(long id, String name, String patientId, String patientName, String hpId, String hpName, LocalDateTime scheduledTime) {
        this.id = id;
        this.name = name;
        this.patientName = patientName;
        this.hpId = hpId;
        this.hpName = hpName;
        this.scheduledTime = scheduledTime;
        this.isApproved = null;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getHpId() {
        return hpId;
    }

    public void setHpId(String hpId) {
        this.hpId = hpId;
    }

    public String getHpName() {
        return hpName;
    }

    public void setHpName(String hpName) {
        this.hpName = hpName;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public Boolean isApproved() {
        return isApproved;
    }

    public void setApproved(Boolean approved) {
        isApproved = approved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Appointment that = (Appointment) o;
        return id == that.id &&
                isApproved == that.isApproved &&
                name.equals(that.name) &&
                hpId.equals(that.hpId) &&
                hpName.equals(that.hpName) &&
                scheduledTime.equals(that.scheduledTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, hpId, hpName, scheduledTime, isApproved);
    }

    @Override
    public @NotNull String toString() {
        return "Appointment{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", hpId='" + hpId + '\'' +
                ", hpName='" + hpName + '\'' +
                ", scheduledTime=" + scheduledTime +
                ", isApproved=" + isApproved +
                '}';
    }
}
