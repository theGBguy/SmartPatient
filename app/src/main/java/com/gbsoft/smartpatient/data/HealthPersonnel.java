package com.gbsoft.smartpatient.data;

import androidx.annotation.Keep;

import org.jetbrains.annotations.NotNull;

@Keep
public class HealthPersonnel extends User {
    private String availability;
    private String qualification;
    private long nmcNumber;
    private String speciality;

    public HealthPersonnel() {
        super();
    }

    public HealthPersonnel(String id, String name, String qualification, long nmcNumber, String speciality, long phoneNumber, String email, String password, String gender) {
        super(id, name, phoneNumber, email, password, gender);
        this.availability = "(unspecified)";
        this.qualification = qualification;
        this.nmcNumber = nmcNumber;
        this.speciality = speciality;
    }

    public HealthPersonnel(String name, String qualification, long nmcNumber, String speciality, long phoneNumber, String email, String password, String gender) {
        super("", name, phoneNumber, email, password, gender);
        this.availability = "(unspecified)";
        this.qualification = qualification;
        this.nmcNumber = nmcNumber;
        this.speciality = speciality;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getQualification() {
        return qualification;
    }

    public long getNmcNumber() {
        return nmcNumber;
    }

    public String getSpeciality() {
        return speciality;
    }

    @Override
    public @NotNull String toString() {
        return "HealthPersonnel{" +
                "availability='" + availability + '\'' +
                ", qualification='" + qualification + '\'' +
                ", nmcNumber=" + nmcNumber +
                ", speciality='" + speciality + '\'' +
                "} " + super.toString();
    }
}
