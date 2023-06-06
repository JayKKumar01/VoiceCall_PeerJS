package com.testing.testingapp;

import java.io.Serializable;

public class UserModel implements Serializable {
    private String name;
    private String userId;

    public UserModel() {
        // Default constructor required for Firebase
    }

    public UserModel(String name, String userId) {
        this.name = name;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

