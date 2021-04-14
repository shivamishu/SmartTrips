package com.example.smarttrip.model;

public class UsersTripInfo {
    private String uid;
    private String userName;
    private String userEmail;
    private String userTripPath;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserTripPath() {
        return userTripPath;
    }

    public void setUserTripPath(String userTripPath) {
        this.userTripPath = userTripPath;
    }
}
