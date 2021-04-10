package com.example.smarttrip.model;

import android.os.AsyncTask;
import android.util.Log;

import com.example.smarttrip.utils.INearBySearchAPICall;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NearBySearchAPICall extends AsyncTask<String, Void, String> {
    private INearBySearchAPICall executor;



    public NearBySearchAPICall(INearBySearchAPICall executor) {
        this.executor = executor;
    }


    @Override
    protected String doInBackground(String... urls) {

        String result;

        String urli = urls[0];
        URL url = null;
        try {
            url = new URL(urli);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        InputStream inputStream = null;
        try {
            inputStream = url.openConnection().getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder buffer = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String read = null;
        while (true) {
            try {
                if (!((read = bufferedReader.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            buffer.append(read).append("\n");
        }
        result = buffer.toString();
        return result;
    }

    protected void onPostExecute(String result) {
        Set<GoogleResponse> listLocation = new HashSet();
        ObjectMapper objectMapper = new ObjectMapper();
            try {
                Map<String, Object> responseObject = objectMapper.readValue(result, Map.class);
                if (responseObject != null && responseObject.get("results") != null) {
                    List<Object> resultObject = (List<Object>) responseObject.get("results");

                    for (Object res : resultObject) {
                        GoogleResponse locationData = new GoogleResponse();
                        if (res != null) {
                            Map<String, Object> locationDataMap = (Map) res;
                            if (locationDataMap != null && locationDataMap.get("geometry") != null) {
                                Map<String, Object> geometryMap = (Map) locationDataMap.get("geometry");
                                if (geometryMap != null && geometryMap.get("location") != null) {
                                    Map<String, Object> location = (Map) geometryMap.get("location");
                                    locationData.setLng((double) location.get("lng"));
                                    locationData.setLat((double) location.get("lat"));
                                }
                            }
                            locationData.setName(String.valueOf(locationDataMap.get("name")));
                            if (locationDataMap.get("rating") != null) {
                                locationData.setRating(String.valueOf(locationDataMap.get("rating")));
                            }

                            Map<String, Object> openingHours = (Map) locationDataMap.get("opening_hours");
                            if (openingHours != null) {
                                locationData.setOpenNow(String.valueOf(openingHours.get("open_now")));
                            }

                            listLocation.add(locationData);
                        }
                    }
                }

//        listLocation.forEach(data -> {
//            Log.d("Co-ordinates to verify", String.valueOf(data));
//
//        });

            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        Log.d("HashSetSize======================================>", String.valueOf(listLocation.size()));
        executor.onNearBySearchAPICallSuccess(listLocation);

    }

}
