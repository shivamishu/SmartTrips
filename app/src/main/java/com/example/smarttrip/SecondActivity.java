package com.example.smarttrip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarttrip.model.DirectionAPICall;

import com.example.smarttrip.utils.IDirectionAPICall;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.PolyUtil;
//import com.squareup.picasso.Picasso;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SecondActivity extends AppCompatActivity implements IDirectionAPICall {
    public static String srcAddress = "";
    public static String destAddress = "";
    public static String mode = "driving";
    public static List<LatLng> decodedPoints;
    public static ArrayList<String> addressArrayList = new ArrayList<String>();
    //    public TextView tripTitleTextView;
    public TextView distanceView;
    public TextView timeView;
    public TextView modeView;
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        setSupportActionBar(findViewById(R.id.toolbar));
//        tripTitleTextView = (TextView)findViewById(R.id.tripTitle);
//        tripTitleTextView = (TextView)findViewById(R.id.toolbar);
        distanceView = (TextView) findViewById(R.id.distanceText);
        timeView = (TextView) findViewById(R.id.timeText);
        modeView = (TextView) findViewById(R.id.mode);
        Bundle bundle = getIntent().getExtras();
        srcAddress = bundle.getString("SourceAddress");
        destAddress = bundle.getString("DestinationAddress");
        mode = bundle.getString("Mode");
        setModeViewIcon(mode);
        getRoute(srcAddress, destAddress, mode);
//
//        String imageUri = "https://i.imgur.com/jl28EwK.jpeg";
//        ImageView ivBasicImage = (ImageView) findViewById(R.id.imageView);
//        Picasso.get().load(imageUri).fit().into(ivBasicImage);

        //To enable the Up button for an activity that has a parent activity,
        // call the app bar's setDisplayHomeAsUpEnabled() method.
        CollapsingToolbarLayout expandedToolbar = findViewById(R.id.toolbar_layout);
        expandedToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        setSupportActionBar(findViewById(R.id.toolbar)); //toolbar in activity_detail_scrolling.xml
        toolbar = findViewById(R.id.toolbar);
        setTripTitleText(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Initialize fragment
        Fragment mapsFragment = new MapFragment();
        //open fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, mapsFragment).commit();

        //floating button save trip attaching click function
        FloatingActionButton saveTrip = (FloatingActionButton) findViewById(R.id.saveTrip);
        saveTrip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.tripSaved), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
//        LinearLayout mapLayout = findViewById(R.id.mapLayout);
//        mapLayout.setOnTouchListener(new View.OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//                v.getParent().requestDisallowInterceptTouchEvent(true);
//                if((event.getAction() == MotionEvent.ACTION_UP) && (event.getAction() == MotionEvent.ACTION_MASK)){
//                    v.getParent().requestDisallowInterceptTouchEvent(false);
//                }
//                return true;
//            }
//        });
//        mapLayout.setOnTouchListener { view, event ->
//                view.parent.requestDisallowInterceptTouchEvent(true)
//            if ((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
//                view.parent.requestDisallowInterceptTouchEvent(false)
//            }
//            return@setOnTouchListener false
//        }
        Button popularButton = (Button) findViewById(R.id.popular);

        popularButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openSearchAlongActivity("popular places");
            }
        });
        Button restaurantButton = (Button) findViewById(R.id.restaurant);

        restaurantButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openSearchAlongActivity("restaurants");
            }
        });
        Button gasButton = (Button) findViewById(R.id.gas);

        gasButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openSearchAlongActivity("gas station");
            }
        });
        Button evButton = (Button) findViewById(R.id.charger);

        evButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openSearchAlongActivity("charging point");
            }
        });

    }

    private void openSearchAlongActivity(String filterType) {
        Intent intent = new Intent(this, SearchAlongActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("filterType", filterType);
        bundle.putParcelableArrayList("waypointlist", (ArrayList<? extends Parcelable>) decodedPoints);
        intent.putExtras(bundle);
        //call second activity
        try{
            startActivity(intent);
        }catch (Exception err){
            Log.d("Error", err.toString());
        }

    }

    public ArrayList<String> getAddresses() {
        addressArrayList.add(srcAddress);
        addressArrayList.add(destAddress);
        return addressArrayList;
    }

    public List<LatLng> getDecodedPoints() {
        return decodedPoints;
    }

    public void getRoute(String src, String dest, String mode) {
        new DirectionAPICall(this, src, dest, mode).executeGetRoute();
    }

    @Override
    public void onDirectionsAPICall() {
        //show busy indicator
    }

    @Override
    public void onDirectionsAPICallSuccess(JSONObject route) {
        //if busy indicator is on, then close it
        try {
            JSONObject polyline = route.getJSONObject("overview_polyline");
            String encodedPolyline = polyline.getString("points");
            decodePolyline(encodedPolyline);
            JSONArray legs = route.getJSONArray("legs");
            JSONObject rLeg = legs.getJSONObject(0);
            JSONObject distance = rLeg.getJSONObject("distance");
            String distanceString = distance.getString("text");
            JSONObject duration = rLeg.getJSONObject(("duration"));
            String durationString = duration.getString("text");
            if (!distanceString.isEmpty() && !durationString.isEmpty()) {
//
                distanceString = distanceString.replace("mi", getString(R.string.miles));
                setTripTravelDetails(distanceString, durationString);
            } else {
                Toast.makeText(this, getString(R.string.errorRoute),
                        Toast.LENGTH_LONG).show();
            }

        } catch (JSONException err) {
            err.printStackTrace();
            Toast.makeText(this, getString(R.string.errorRoute),
                    Toast.LENGTH_LONG).show();
        }

    }

    private void setTripTitleText(Toolbar toolbar) {
        String destinationAddress = destAddress.indexOf(",") > -1 ? destAddress.substring(0, destAddress.indexOf(",")) : destAddress;
        toolbar.setTitle(destinationAddress + " " + getString(R.string.tripDetails));
    }

    private void setTripTravelDetails(String distanceString, String durationString) {
//        distanceView.setText(getString(R.string.distance) + " " + distanceString);
//        timeView.setText(getString(R.string.time) + " " + durationString);
        distanceView.setText(distanceString);
        timeView.setText(durationString);
    }

    private void decodePolyline(String encodedPolylinePoints) {
        if (decodedPoints != null) {
            decodedPoints.clear();
        }
        decodedPoints = PolyUtil.decode(encodedPolylinePoints);
    }

    private void setModeViewIcon(String mode) {
        int modeIcon;
        switch (mode) {
            case "bicycling":
                modeIcon = R.drawable.ic_baseline_directions_bike_24;
                break;
            case "walking":
                modeIcon = R.drawable.ic_baseline_directions_walk_24;
                break;
            case "transit":
                modeIcon = R.drawable.ic_baseline_directions_transit_24;
                break;
            default:
                modeIcon = R.drawable.ic_baseline_directions_car_24;
        }
        modeView.setCompoundDrawablesWithIntrinsicBounds(0, 0, modeIcon, 0);
    }

    public LatLng getLatLngCordFromAddress(String address) {

        Geocoder geoCoder = new Geocoder(SecondActivity.this);
        List<Address> addressesList;

        try {
            addressesList = geoCoder.getFromLocationName(address, 1);
            if (addressesList != null) {
                Address addressItem = addressesList.get(0);
                LatLng latLng = new LatLng(addressItem.getLatitude(), addressItem.getLongitude());
                return latLng;
            } else {
                return null;
            }
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }

    }
}