package com.gbsoft.smartpatient.data;

import androidx.annotation.Keep;

import org.jetbrains.annotations.NotNull;

@Keep
public class Patient extends User {
    private String address;
    private int age;

    public Patient() {
        super();
    }

    public Patient(String id, String name, String address, int age, long phoneNumber, String email, String password, String gender) {
        super(id, name, phoneNumber, email, password, gender);
        this.address = address;
        this.age = age;
    }

    public Patient(String name, String address, int age, long phoneNumber, String email, String password, String gender) {
        super("", name, phoneNumber, email, password, gender);
        this.address = address;
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public int getAge() {
        return age;
    }

    @Override
    public @NotNull String toString() {
        return "Patient{" +
                "address='" + address + '\'' +
                ", age=" + age +
                "} " + super.toString();
    }
}
