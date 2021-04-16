package com.example.smarttrip.utils;

import org.json.JSONObject;

import java.util.ArrayList;

public interface IDirectionAPICall {
    void onDirectionsAPICall();

    void onDirectionsAPICallSuccess(JSONObject route, ArrayList<String> wayPointInitialOrder);
}
