package com.example.smarttrip.model;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.smarttrip.BuildConfig;
import com.example.smarttrip.utils.IDirectionAPICall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DirectionAPICall {

    public static final String apiKey = BuildConfig.MAPS_API_KEY;
    private IDirectionAPICall executor;
    private String srcString;
    private String destString;
    private String mode;

    public DirectionAPICall(IDirectionAPICall executor, String srcString, String destString, String mode) {
        this.executor = executor;
        this.srcString = srcString;
        this.destString = destString;
        this.mode = mode;
    }

    public void executeGetRoute() {
        try {
            String org = "&origin=" + URLEncoder.encode(srcString, "utf-8");
            String dest = "&destination=" + URLEncoder.encode(destString, "utf-8");
            String travelMode = "&mode=" + mode;
            String apiURL = "https://maps.googleapis.com/maps/api/directions/json?key=" + apiKey + org + dest + travelMode;
            new DirectionsAPIAsyncCall().execute(apiURL);
//
        } catch (UnsupportedEncodingException err) {
            err.printStackTrace();
        }
    }

    private class DirectionsAPIAsyncCall extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                String apiUrl = urls[0];
                URL url = new URL(apiUrl);
                InputStream inputStream = url.openConnection().getInputStream();
                StringBuilder buffer = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String read;
                while ((read = bufferedReader.readLine()) != null) {
                    buffer.append(read).append("\n");
                }
                return buffer.toString();
            } catch (IOException | NullPointerException err) {
                err.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray routeArray = jsonObject.getJSONArray("routes");
//            if(routeArray.length() > 0){
                JSONObject routes = routeArray.getJSONObject(0);
//                JSONArray legs = routes.getJSONArray("legs");
//                JSONObject rLeg = legs.getJSONObject(0);
                executor.onDirectionsAPICallSuccess(routes);
//                JSONObject distance = rLeg.getJSONObject("distance");
//                String distanceString = distance.getString("text");
//                JSONObject duration = rLeg.getJSONObject(("duration"));
//                String durationString = duration.getString("text");
//            }
            } catch (JSONException err) {
                err.printStackTrace();
            }


        }
    }

}
