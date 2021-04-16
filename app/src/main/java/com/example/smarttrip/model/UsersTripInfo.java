package com.example.smarttrip.model;


import java.io.Serializable;

public class UsersTripInfo implements Serializable {
//    private String uid;
//    private String userName;
//    private String userEmail;
    private String userTripPath;
    private String userTripTitle;
    private String userTripTimeStamp;


//    public String getUid() {
//        return uid;
//    }
//
//    public void setUid(String uid) {
//        this.uid = uid;
//    }
//
//    public String getUserName() {
//        return userName;
//    }
//
//    public void setUserName(String userName) {
//        this.userName = userName;
//    }
//
//    public String getUserEmail() {
//        return userEmail;
//    }
//
//    public void setUserEmail(String userEmail) {
//        this.userEmail = userEmail;
//    }

    public String getUserTripPath() {
        return userTripPath;
    }

    public void setUserTripPath(String userTripPath) {
        this.userTripPath = userTripPath;
    }

    public String getUserTripTimeStamp() {
        return userTripTimeStamp;
    }

    public void setUserTripTimeStamp(String userTripTimeStamp) {
        this.userTripTimeStamp = userTripTimeStamp;
    }

    public String getUserTripTitle() {
        return userTripTitle;
    }

    public void setUserTripTitle(String userTripTitle) {
        this.userTripTitle = userTripTitle;
    }

    @Override
    public String toString() {
        return "UsersTripInfo{" +
                "userTripPath='" + userTripPath + '\'' +
                ", userTripTitle='" + userTripTitle + '\'' +
                ", userTripTimeStamp='" + userTripTimeStamp + '\'' +
                '}';
    }
}
