package com.example.smarttrip.utils;

import org.json.JSONObject;

public interface IDirectionAPICall {
    void onDirectionsAPICall();

    void onDirectionsAPICallSuccess(JSONObject route);
}
