package com.example.smarttrip.model;


import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;

public class UsersTripInfo implements Serializable {


    //    private String uid;
//    private String userName;
//    private String userEmail;
    private String userTripPath;
    private String userTripTitle;
    private String userTripTimeStamp;
    private List<GoogleResponse> harvestList;
    private String totalTime;
    private String totalDistance;
    private String tripMode;



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

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(String totalDistance) {
        this.totalDistance = totalDistance;
    }

    public String getTripMode() {
        return tripMode;
    }

    public void setTripMode(String tripMode) {
        this.tripMode = tripMode;
    }

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

    public List<GoogleResponse> getHarvestList() {
        return harvestList;
    }

    public void setHarvestList(List<GoogleResponse> harvestList) {
        this.harvestList = harvestList;
    }

    @Override
    public String toString() {
        return "UsersTripInfo{" +
                "userTripPath='" + userTripPath + '\'' +
                ", userTripTitle='" + userTripTitle + '\'' +
                ", userTripTimeStamp='" + userTripTimeStamp + '\'' +
                ", harvestList=" + harvestList +
                '}';
    }
}
