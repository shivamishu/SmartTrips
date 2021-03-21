package com.example.smarttrip;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment {
    public static ArrayList<Polyline> polylineList = new ArrayList<Polyline>();
    public static GoogleMap map;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        //initialize maps fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                SecondActivity secondActivity = (SecondActivity)getActivity();
                ArrayList<String> addressArrayList = secondActivity.getAddresses();
                List<LatLng> points = secondActivity.getDecodedPoints();
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.addAll(points);
                String src = addressArrayList.get(0);
                String dest = addressArrayList.get(addressArrayList.size() - 1);
                //called when map is loaded and ready to use
                MarkerOptions markerOptions = new MarkerOptions();
//                LatLng srcLoc = new LatLng(37.322998, -122.032181); //sample
//                LatLng destLoc = new LatLng(37.322998, -122.032181); //sample
                LatLng srcLoc = secondActivity.getLatLngCordFromAddress(src);
                LatLng destLoc = secondActivity.getLatLngCordFromAddress(dest);
                markerOptions.position(srcLoc);
                markerOptions.title(src);
                googleMap.clear();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        srcLoc, 10
                ));
                googleMap.addMarker(markerOptions);
                markerOptions.position(destLoc);
                markerOptions.title(dest);
                googleMap.addMarker(markerOptions);
                Polyline polyline = googleMap.addPolyline(polylineOptions);
                polylineList.add(polyline);
                map = googleMap;
            }
        });
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        if(polylineList.size() > 0){
            Polyline line =  polylineList.get(0);
            if(line != null){
                line.remove();
                map.clear();
            }
        }

    }
}