package com.example.smarttrip;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.example.smarttrip.model.DirectionAPICall;
import com.example.smarttrip.model.GoogleResponse;
import com.example.smarttrip.model.UsersTripInfo;
import com.example.smarttrip.utils.IDirectionAPICall;
import com.example.smarttrip.utils.IOnGetDataListener;
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
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Stack;
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
    public static String now;
    public boolean expanded = true;
    public static Stack<Intent> parents = new Stack<Intent>();
    ProgressBar progresssBar;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parents.push(getIntent());
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
        progresssBar = (ProgressBar) findViewById(R.id.progressBar);
        getRoute(srcAddress, destAddress, mode, wayPointListSet);
//
//        String imageUri = "https://i.imgur.com/jl28EwK.jpeg";
//        ImageView ivBasicImage = (ImageView) findViewById(R.id.imageView);
//        Picasso.get().load(imageUri).fit().into(ivBasicImage);

        //To enable the Up button for an activity that has a parent activity,
        // call the app bar's setDisplayHomeAsUpEnabled() method.
        CollapsingToolbarLayout expandedToolbar = findViewById(R.id.toolbar_layout);
        expandedToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        toolbar = findViewById(R.id.collapsible_toolbar);
//        setSupportActionBar(findViewById(R.id.toolbar));
        setTripTitleText(toolbar);
        Toolbar toolbar2 = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar2);
        if (getSupportActionBar() != null) {
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_launcher_round);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(newdrawable);
//            toolbar2.setNavigationOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent parentActivityIntent = new Intent(v.getContext(), parents.pop().getClass());
//                    parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startService(parentActivityIntent);
//                    finish();
//                }
//            });
        }
        toolbar2.getOverflowIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        toolbar2.inflateMenu(R.menu.menu_main);

        //Initialize fragment
//        Fragment mapsFragment = new MapFragment();
        //open fragment
//        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, mapsFragment).commit();
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
                    //String tripTitle = getTripTitle();
                    String sourceAddress = srcAddress.indexOf(",") > -1 ? srcAddress.substring(0, srcAddress.indexOf(",")) : srcAddress;
                    DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.US);
                    String formattedDate = df.format(new Date());
                    String tripTitle = sourceAddress + " - " + getTripTitle() + " as on " + formattedDate;
                    // Check if user's email is verified
                    // Check if user's email is verified
                    boolean emailVerified = user.isEmailVerified();
                    // The user's ID, unique to the Firebase project. Do NOT use this value to
                    // authenticate with your backend server, if you have one. Use
                    // FirebaseUser.getIdToken() instead.
                    String uid = user.getUid();
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    //databaseReference = firebaseDatabase.getReference().child("UsersTripInfo").child(uid);
                    // getDataFromFirebase();
                    databaseReference = firebaseDatabase.getReference().child("UsersTripInfo").child(uid).push();
                    List<GoogleResponse> mainList = new ArrayList<>();
                    mainList.addAll(wayPointListSet);
                    addDatatoFirebase(tripTitle, tripPath, formattedDate, mainList);
                    Snackbar.make(view, getString(R.string.tripSaved), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(view, "Sign in to Save Trip", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
        NestedScrollView scrollView = findViewById(R.id.nested_scroll_view);
        ImageView transparentImageView = (ImageView) findViewById(R.id.transparent_image);
        transparentImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:

                    case MotionEvent.ACTION_MOVE:
                        // Disallow ScrollView to intercept touch events.
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    default:
                        return true;
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


        ImageButton openDirectionButton = (ImageButton) findViewById(R.id.open_GMaps);
        openDirectionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openDirectionsinGMaps(gMapsString);
            }
        });

        ImageButton shareOnWhatsAppButton = (ImageButton) findViewById(R.id.whatsApp_nav);
        shareOnWhatsAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareOnWhatsApp(gMapsString);
            }
        });

        Button collapseButton = (Button) findViewById(R.id.collapseButton);
        collapseButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_expand_less_24, 0, 0, 0);
        collapseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (expanded) {
                    collapse(findViewById(R.id.wayPointList));
                    collapseButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_expand_more_24, 0, 0, 0);
                    collapseButton.setText(getString(R.string.expand));
                } else {
                    expand(findViewById(R.id.wayPointList));
                    collapseButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_expand_less_24, 0, 0, 0);
                    collapseButton.setText(getString(R.string.collapse));

                }
                expanded = !expanded;  //toggle expanded
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

    public ArrayList<String> getAddresses() {
        if (addressArrayList.size() > 0) {
            addressArrayList.clear();
        }
        addressArrayList.add(srcAddress);
        addressArrayList.add(destAddress);
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
        progresssBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDirectionsAPICallSuccess(JSONObject route, ArrayList<String> wayPointInitialOrder) {
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
            ArrayList<String> wayPointsAddressesList = new ArrayList<>();
            for (int i = 0; i < legs.length(); i++) {
                obj = legs.getJSONObject(i);
                JSONObject distance = obj.getJSONObject("distance");
                JSONObject duration = obj.getJSONObject(("duration"));
                dist += Double.parseDouble(distance.getString("text").replaceAll("[^\\.0123456789]", ""));
                dur += duration.getLong("value");
                strAddress = obj.getString("start_address");
                directionAddressString += encodeAddressString(strAddress) + "/";

            }
            JSONArray wayPointsOrder = route.getJSONArray("waypoint_order");
            wayPointsAddressesList.add(srcAddress);
            for (int j = 0; j < wayPointsOrder.length(); j++) {
                wayPointsAddressesList.add(wayPointInitialOrder.get(wayPointsOrder.getInt(j)));
            }
            wayPointsAddressesList.add(destAddress);
            buildWayPointsList(wayPointsAddressesList);
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
            progresssBar.setVisibility(View.GONE);

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
        //        SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss");
//        now = ISO_8601_FORMAT.format(new Date());
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

    @Exclude
    private void addDatatoFirebase(String tripTitle, String tripPath, String tripTimeStamp, List<GoogleResponse> harvestList) {
        // below 3 lines of code is used to set
        // data in our object class.
        //usersTripInfo.setUid(uid);
        //usersTripInfo.setUserName(name);
        //usersTripInfo.setUserEmail(email);
        usersTripInfo.setUserTripTitle(tripTitle);
        usersTripInfo.setUserTripPath(tripPath);
        usersTripInfo.setUserTripTimeStamp(tripTimeStamp);
        usersTripInfo.setHarvestList(harvestList);
        databaseReference.setValue(usersTripInfo);
    }

    private void openDirectionsinGMaps(String mapsURL) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(mapsURL));
        startActivity(intent);
    }

    private void shareOnWhatsApp(String shareURL) {
        Intent whatsAppIntent = new Intent(Intent.ACTION_SEND);
        whatsAppIntent.setType("text/plain");
        whatsAppIntent.setPackage("com.whatsapp");
        if (whatsAppIntent != null) {
            whatsAppIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    shareURL);
            startActivity(Intent.createChooser(whatsAppIntent, "Share on"));
        } else {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
        }

    }

    private void buildWayPointsList(ArrayList<String> wayPointsAddressesList) {
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                R.layout.waypoint_item, wayPointsAddressesList);
//        ListView listView = (ListView) findViewById(R.id.wayPointList);
//        listView.setAdapter(adapter);
        LinearLayout list = (LinearLayout) findViewById(R.id.wayPointList);
        list.removeAllViews();
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < wayPointsAddressesList.size(); i++) {
            String address = wayPointsAddressesList.get(i);
            View vi = inflater.inflate(R.layout.waypoint_item, null);
            TextView textView = (TextView) vi.findViewById(R.id.address);
            LinearLayout itemLayout = (LinearLayout) vi.findViewById(R.id.itemLayout);
            Button deleteButton = new Button(this, null, R.style.DeleteButton);
            if (i != 0 && i != wayPointsAddressesList.size() - 1) {
                textView.setText((i) + ":  " + address);
                deleteButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_wrong_location_24, 0, 0, 0);
                deleteButton.setTag(address);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        deleteItem(v);
                    }
                });
            } else {
                textView.setText(address);
                int icon = i == 0 ? R.drawable.ic_start : R.drawable.ic_finish;
//                textView.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
                deleteButton.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
            }
            itemLayout.addView(deleteButton);
            list.addView(vi);
        }
    }

    private void deleteItem(View v) {
        String strTag = "";
        if (v.getTag() instanceof String) {
            strTag = (String) v.getTag();

            for (GoogleResponse point : wayPointListSet) {
                if (point.getName() == strTag) {
                    wayPointListSet.remove(point);
                    getRoute(srcAddress, destAddress, mode, wayPointListSet);
                    Toast.makeText(this, "Way point deleted",
                            Toast.LENGTH_LONG).show();
                    break;
                }
            }
        }
    }


    public static void expand(final View view) {
        int wrapContentSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int matchParentSpec = View.MeasureSpec.makeMeasureSpec(((View) view.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        view.measure(matchParentSpec, wrapContentSpec);
        final int targetHeight = view.getMeasuredHeight();

        // For older versions of android < 21
        view.setVisibility(View.VISIBLE);
        view.getLayoutParams().height = 1;
        Animation animation = new Animation() {
            @Override
            public boolean willChangeBounds() {
                return true;
            }

            @Override
            protected void applyTransformation(float insertTime, Transformation transformation) {
                view.getLayoutParams().height = insertTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * insertTime);
                view.requestLayout();
            }
        };

        // Expansion speed of 1dp/ms
        animation.setDuration((int) (targetHeight / view.getContext().getResources().getDisplayMetrics().density));
        view.startAnimation(animation);
    }

    public static void collapse(final View view) {
        final int initialHeight = view.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            public boolean willChangeBounds() {
                return true;
            }

            @Override
            protected void applyTransformation(float insertTime, Transformation transformation) {
                if (insertTime == 1) {
                    view.setVisibility(View.GONE);
                } else {
                    view.getLayoutParams().height = initialHeight - (int) (initialHeight * insertTime);
                    view.requestLayout();
                }
            }
        };

        // Collapse speed of 1dp/ms
        a.setDuration((int) (initialHeight / view.getContext().getResources().getDisplayMetrics().density));
        view.startAnimation(a);
    }


    public void readData(DatabaseReference ref, final IOnGetDataListener listener) {
        listener.onStart();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onFailure();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            MenuItem tripHistoryItem = menu.findItem(R.id.trip_history);
            tripHistoryItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.logout:
                logout();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                finish();
                return true;
            case R.id.trip_history:
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String uid = user.getUid();
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    databaseReference = firebaseDatabase.getReference().child("UsersTripInfo").child(uid);
                    readData(databaseReference, new IOnGetDataListener() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            List<UsersTripInfo> usersTripInfoList = new ArrayList<>();
                            usersTripInfoList.clear();
                            System.out.println("Children Count: " + dataSnapshot.getChildrenCount());
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                UsersTripInfo usersTripInfoRead = postSnapshot.getValue(UsersTripInfo.class);
                                usersTripInfoList.add(usersTripInfoRead);
                            }
                            Intent intent = new Intent(SecondActivity.this, TripHistoryActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("tripHistoryList", (Serializable) usersTripInfoList);
                            intent.putExtras(bundle);
                            startActivityForResult(intent, 128);
                        }

                        @Override
                        public void onStart() {
                            //when starting
                            Log.d("ONSTART", "Started");
                        }

                        @Override
                        public void onFailure() {
                            Log.d("onFailure", "Failed");
                        }
                    });

                }
                //onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(SecondActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


}