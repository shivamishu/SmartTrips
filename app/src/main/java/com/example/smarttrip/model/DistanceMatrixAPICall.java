package com.example.smarttrip.model;

import android.os.AsyncTask;

import com.example.smarttrip.utils.IDistanceMatrixAPICall;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class DistanceMatrixAPICall extends AsyncTask<String, Void, String> {
    private IDistanceMatrixAPICall executor;
    private GoogleResponse googleResponse;

    public DistanceMatrixAPICall(IDistanceMatrixAPICall executor, GoogleResponse googleResponse) {
        this.executor = executor;
        this.googleResponse = googleResponse;
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
        ObjectMapper objectMapper = new ObjectMapper();
        DistanceMatrixResponse distanceMatrixData = new DistanceMatrixResponse();
        try {
            Map<String, Object> responseObject = objectMapper.readValue(result, Map.class);
            if (responseObject != null && responseObject.get("rows") != null) {
                List<Object> results = (List<Object>) responseObject.get("rows");

                for (Object res : results) {
                    if (res != null) {
                        Map<String, Object> distanceMatrixDataMap = (Map) res;
                        if (distanceMatrixDataMap != null && distanceMatrixDataMap.get("elements") != null) {
                            List<Object> elementList = (List<Object>) distanceMatrixDataMap.get("elements");
                            for (Object element : elementList) {
                                Map<String, Object> elementDataMap = (Map) element;
                                if (elementDataMap != null && elementDataMap.get("distance") != null) {

                                    Map<String, Object> distance = (Map) elementDataMap.get("distance");
                                    distanceMatrixData.setDistanceText(String.valueOf(distance.get("text")));
                                    distanceMatrixData.setDistanceValue(String.valueOf(distance.get("value")));
                                    Map<String, Object> duration = (Map) elementDataMap.get("duration");
                                    distanceMatrixData.setDurationText(String.valueOf(duration.get("text")));
                                    distanceMatrixData.setDurationValue(String.valueOf(duration.get("value")));
                                }
                            }

                        }
                    }
                }
            }

        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        googleResponse.setDistanceText(distanceMatrixData.getDistanceText());
        googleResponse.setDistanceValue(distanceMatrixData.getDistanceValue());
        googleResponse.setDurationText(distanceMatrixData.getDurationText());
        googleResponse.setDurationValue(distanceMatrixData.getDurationValue());
        executor.onDistanceMatrixAPICallSuccess(googleResponse);
    }
}
