package com.gbsoft.smartpatient.data;

/**
 * Data class that captures newly registered username for registered user retrieved from RemoteRepo
 */
public class RegisteredUser {
    private final String username;

    public RegisteredUser(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
