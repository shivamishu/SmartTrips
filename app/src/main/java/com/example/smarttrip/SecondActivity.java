package com.example.smarttrip;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.smarttrip.model.DirectionAPICall;
import com.example.smarttrip.model.GoogleResponse;
import com.example.smarttrip.model.UsersTripInfo;
import com.example.smarttrip.utils.IDirectionAPICall;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

//import com.squareup.picasso.Picasso;


public class SecondActivity extends AppCompatActivity implements IDirectionAPICall {
    private static final int REQUEST_CODE = 0612;
    public static String srcAddress = "";
    public static String destAddress = "";
    public static String mode = "driving";
    public static List<LatLng> decodedPoints;
    public static ArrayList<String> addressArrayList = new ArrayList<>();
    //    public TextView tripTitleTextView;
    public TextView distanceView;
    public TextView timeView;
    public TextView modeView;
    public Toolbar toolbar;
    public HashSet<GoogleResponse> wayPointListSet = new HashSet<>();
    public String gMapsString;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    UsersTripInfo usersTripInfo;


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
        getRoute(srcAddress, destAddress, mode, wayPointListSet);
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
        usersTripInfo = new UsersTripInfo();

        //floating button save trip attaching click function
        FloatingActionButton saveTrip = (FloatingActionButton) findViewById(R.id.saveTrip);
        saveTrip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // Name, email address, and profile photo Url
                    String name = user.getDisplayName();
                    String email = user.getEmail();
                    Uri photoUrl = user.getPhotoUrl();
                    String tripPath = gMapsString;
                    String tripTitle = getTripTitle();
                    // Check if user's email is verified
                    boolean emailVerified = user.isEmailVerified();
                    // The user's ID, unique to the Firebase project. Do NOT use this value to
                    // authenticate with your backend server, if you have one. Use
                    // FirebaseUser.getIdToken() instead.
                    String uid = user.getUid();
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    databaseReference = firebaseDatabase.getReference().child("UsersTripInfo").child(uid);

                    // getDataFromFirebase();

                    databaseReference = firebaseDatabase.getReference().child("UsersTripInfo").child(uid).push();
                    SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
                    String now = ISO_8601_FORMAT.format(new Date());
                    addDatatoFirebase(uid, name, email, tripTitle, tripPath, now);
                    Snackbar.make(view, getString(R.string.tripSaved), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(view, "SignIn to Save Trip", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
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
                openSearchAlongActivity("tourist_attraction");
            }
        });
        Button restaurantButton = (Button) findViewById(R.id.restaurant);

        restaurantButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openSearchAlongActivity("restaurant");
            }
        });
        Button gasButton = (Button) findViewById(R.id.gas);

        gasButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openSearchAlongActivity("gas_station");
            }
        });
        Button evButton = (Button) findViewById(R.id.charger);

        evButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openSearchAlongActivity("ev charger");
            }
        });

        Button openDirectionButton = (Button) findViewById(R.id.open_nav);
        openDirectionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openDirectionsinGMaps();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data.hasExtra("selectedList")) {
                HashSet<GoogleResponse> results = (HashSet<GoogleResponse>) data.getSerializableExtra("selectedList");
                wayPointListSet.addAll(results);
                getRoute(srcAddress, destAddress, mode, wayPointListSet);
                Toast.makeText(this, getString(R.string.waypointsupdated),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void openSearchAlongActivity(String filterType) {
        Intent intent = new Intent(this, SearchAlongActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("filterType", filterType);
        bundle.putParcelableArrayList("waypointlist", (ArrayList<? extends Parcelable>) decodedPoints);
        bundle.putString("mode", mode);
        LatLng srcLoc = this.getLatLngCordFromAddress(srcAddress);
        bundle.putString("srcAddress", srcLoc.latitude + "," + srcLoc.longitude);
        intent.putExtras(bundle);
        intent.putExtra("selectedList", wayPointListSet);
        //call second activity
        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (Exception err) {
            Log.d("Error", err.toString());
        }

    }

    private void openDirectionsinGMaps() {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(gMapsString));
        startActivity(intent);
    }

    public ArrayList<String> getAddresses() {
        if (addressArrayList.size() > 0) {
            addressArrayList.clear();
            addressArrayList.add(srcAddress);
            addressArrayList.add(destAddress);
        } else {
            addressArrayList.add(srcAddress);
            addressArrayList.add(destAddress);
        }
        return addressArrayList;
    }

    public List<LatLng> getDecodedPoints() {
        return decodedPoints;
    }

    public HashSet<GoogleResponse> getWayPoints() {
        return wayPointListSet;
    }

    public void getRoute(String src, String dest, String mode, HashSet<GoogleResponse> wayPoints) {
        new DirectionAPICall(this, src, dest, mode, wayPointListSet).executeGetRoute();
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
            Double dist = 0.0;
            long dur = 0;
            String directionAddressString = "/";
            String strAddress;
            JSONObject obj = legs.getJSONObject(0);
            //changes for multiple points
            for (int i = 0; i < legs.length(); i++) {
                obj = legs.getJSONObject(i);
                JSONObject distance = obj.getJSONObject("distance");
                JSONObject duration = obj.getJSONObject(("duration"));
                dist += Double.parseDouble(distance.getString("text").replaceAll("[^\\.0123456789]", ""));
                dur += duration.getLong("value");
                strAddress = obj.getString("start_address");
                directionAddressString += encodeAddressString(strAddress) + "/";

            }
            strAddress = obj.getString("end_address");
            directionAddressString += encodeAddressString(strAddress);
            gMapsString = "https://www.google.com/maps/dir" + directionAddressString;   //used for opening gmaps


            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.CEILING);
            String distanceString = df.format(dist);
            distanceString += " " + getString(R.string.miles);
            int day = (int) TimeUnit.SECONDS.toDays(dur);
            long hours = TimeUnit.SECONDS.toHours(dur) - (day * 24);
            long minute = TimeUnit.SECONDS.toMinutes(dur) - (TimeUnit.SECONDS.toHours(dur) * 60);
            String durationString = "";
            if (day > 0) {
                durationString = day + " d ";
            }
            if (hours > 0) {
                durationString = durationString + hours + " hrs ";
            }
            if (minute > 0) {
                durationString = durationString + minute + " mins";
            }

            //Initialize fragment
            Fragment mapsFragment = new MapFragment();
            //open fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, mapsFragment).commit();

//            JSONObject rLeg = legs.getJSONObject(0);
//            JSONObject distance = rLeg.getJSONObject("distance");
//            String distanceString = distance.getString("text");
//            JSONObject duration = rLeg.getJSONObject(("duration"));
//            String durationString = duration.getString("text");
            //end of changes
            if (!distanceString.isEmpty() && !durationString.isEmpty()) {
//
//                distanceString = distanceString.replace("mi", getString(R.string.miles));
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

    public String encodeAddressString(String address) {
        String currAddressString = address;
        try {
            currAddressString = currAddressString.replaceAll("\\/", "");
            currAddressString = currAddressString.replaceAll("\\,", ",+");
            currAddressString = currAddressString.replaceAll("\\ ", "+");
            currAddressString = currAddressString.replaceAll("\\++", "+");
            currAddressString = URLEncoder.encode(currAddressString, "utf-8");
            currAddressString = currAddressString.replaceAll("\\%2C", ",");
            currAddressString = currAddressString.replaceAll("\\%2B", "+");
        } catch (
                UnsupportedEncodingException err) {
            err.printStackTrace();
        }
        return currAddressString;
    }

    private String getTripTitle() {
        String destinationAddress = destAddress.indexOf(",") > -1 ? destAddress.substring(0, destAddress.indexOf(",")) : destAddress;
        return destinationAddress + " " + getString(R.string.tripDetails);
    }

    private void setTripTitleText(Toolbar toolbar) {
        String destinationAddress = destAddress.indexOf(",") > -1 ? destAddress.substring(0, destAddress.indexOf(",")) : destAddress;
        toolbar.setTitle(getTripTitle());
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

    private void addDatatoFirebase(String uid, String name, String email,String tripTitle, String tripPath, String tripTimeStamp) {
        // below 3 lines of code is used to set
        // data in our object class.
        usersTripInfo.setUid(uid);
        usersTripInfo.setUserName(name);
        usersTripInfo.setUserEmail(email);
        usersTripInfo.setUserTripTitle(tripTitle);
        usersTripInfo.setUserTripPath(tripPath);
        usersTripInfo.setUserTripTimeStamp(tripTimeStamp);
        databaseReference.setValue(usersTripInfo);
    }

//    private void getDataFromFirebase() {
//         List<UsersTripInfo> usersTripInfoList =  new ArrayList<>();
//
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                usersTripInfoList.clear();
//                System.out.println("Children Count: " + snapshot.getChildrenCount());
//                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
//                    UsersTripInfo usersTripInfoRead =  postSnapshot.getValue(UsersTripInfo.class);
//                    System.out.println("The message from database: " + usersTripInfoRead.getUserTripPath());
//                    usersTripInfoList.add(usersTripInfoRead);
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                System.out.println("The read failed: " + error.getMessage());
//            }
//        });
//
//    }

}