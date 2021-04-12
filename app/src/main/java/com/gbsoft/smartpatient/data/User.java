package com.gbsoft.smartpatient.data;

import androidx.annotation.Keep;

import org.jetbrains.annotations.NotNull;

@Keep
public class User {
    public static final String GENDER_MALE = "MALE";
    public static final String GENDER_FEMALE = "FEMALE";
    public static final String GENDER_OTHERS = "OTHERS";

    private String id;
    private String name;
    private long phoneNum;
    private String email;
    private String password;
    private String photoUrl;
    private String gender;

    public User() {
        this.id = "";
        this.name = "";
        this.phoneNum = -1;
        this.email = "";
        this.password = "";
        this.photoUrl = "";
        this.gender = GENDER_OTHERS;
    }

    public User(String id, String name, long phoneNum, String email, String password, String gender) {
        this.id = id;
        this.name = name;
        this.phoneNum = phoneNum;
        this.email = email;
        this.password = password;
        this.photoUrl = "";
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(long phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public @NotNull String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", phoneNum=" + phoneNum +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }
}
