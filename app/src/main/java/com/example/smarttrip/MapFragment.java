package com.example.smarttrip;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.smarttrip.model.GoogleResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MapFragment extends Fragment {
    public static ArrayList<Polyline> polylineList = new ArrayList<Polyline>();

    //    public static GoogleMap map;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        //initialize maps fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
//                map = googleMap;
                SecondActivity secondActivity = (SecondActivity) getActivity();
                ArrayList<String> addressArrayList = secondActivity.getAddresses();
                List<LatLng> points = secondActivity.getDecodedPoints();
                HashSet<GoogleResponse> waypointsSet = secondActivity.getWayPoints();
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

                //set src marker
                markerOptions.position(srcLoc);
                markerOptions.title(src);
                googleMap.clear();
                googleMap.addMarker(markerOptions);

                //add waypoint markers
                if (waypointsSet.size() > 0) {
                    for (GoogleResponse waypoint : waypointsSet) {
                        LatLng loc = new LatLng(waypoint.getLat(), waypoint.getLng());
                        markerOptions.position(loc);
                        markerOptions.title(waypoint.getName());
                        googleMap.addMarker(markerOptions);
                    }
                }
//                googleMap.clear();
//                Marker markerNameSrc = googleMap.addMarker(new MarkerOptions().position(srcLoc).title(src));
//                Marker markerNameDest = googleMap.addMarker(new MarkerOptions().position(destLoc).title(dest));
//                markerNameDest.remove();
//                markerNameSrc.remove();
                //set dest marker
                markerOptions.position(destLoc);
                markerOptions.title(dest);
                googleMap.addMarker(markerOptions);

                //animate camera
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        srcLoc, 10
                ));

                //add polyline
                Polyline polyline = googleMap.addPolyline(polylineOptions);
                polylineList.add(polyline);
            }
        });
        return view;
    }
//    @Override
//    public void onResume() {
//        super.onResume();
//        if(polylineList.size() > 0){
//            Polyline line =  polylineList.get(0);
//            if(line != null){
//                line.remove();
//                map.clear();
//            }
//        }

    //    }
    public void setGoogleMapOptions() {

    }
}