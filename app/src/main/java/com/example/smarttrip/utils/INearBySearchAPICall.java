package com.example.smarttrip.utils;

import com.example.smarttrip.model.GoogleResponse;

import java.util.Set;

public interface INearBySearchAPICall {

    void onNearBySearchAPICallSuccess(Set<GoogleResponse> finalResult);

}
