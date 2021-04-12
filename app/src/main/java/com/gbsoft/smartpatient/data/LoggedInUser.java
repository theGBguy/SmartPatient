package com.gbsoft.smartpatient.data;

/**
 * Data class that captures user information for logged in users retrieved from RemoteRepo
 */
public class LoggedInUser {

    private final String userId;
    private final String name;

    public LoggedInUser(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }
}