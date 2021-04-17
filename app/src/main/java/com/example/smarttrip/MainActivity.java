package com.example.smarttrip;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.smarttrip.model.GoogleResponse;
import com.example.smarttrip.model.UsersTripInfo;
import com.example.smarttrip.utils.IOnGetDataListener;
import com.example.smarttrip.utils.PlacesAutoComplete;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    //    public Map<String, String> addressMap = new HashMap<>();
    public static AutoCompleteTextView autoCompleteTextViewSrc = null;
    public static AutoCompleteTextView autoCompleteTextViewDest = null;
    public static ChipGroup chipGroup = null;
    private static final int REQUEST_CODE = 0612;
    private static final String TAG = "MainActivity";
    private static final String ARG_NAME = "username";
    FirebaseAuth firebaseAuth;
    GoogleSignInClient googleSignInClient;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    public static Stack<Intent> parents = new Stack<Intent>();


    public static void startActivity(Context context, String username, Uri photoUrl) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(ARG_NAME, username);
        intent.putExtra("imageUri", photoUrl.toString());

        context.startActivity(intent);
    }

    public static void startActivityAsGuest(Context context, String username) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(ARG_NAME, username);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parents.push(getIntent());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        }
        toolbar.getOverflowIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        toolbar.inflateMenu(R.menu.menu_main);

//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //getSupportActionBar().setLogo(R.drawable.ic_launcher_round);
        //getSupportActionBar().setDisplayUseLogoEnabled(true);


        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        //ImageView ivUserImage = (ImageView) findViewById(R.id.ivUserImage);

        //Picasso.get().load(getIntent().getStringExtra("imageUri")).resize(50,50).centerCrop().into(ivUserImage);

        TextView textView = findViewById(R.id.textViewWelcome);

        if (getIntent().hasExtra(ARG_NAME)) {
            textView.setText(String.format("Welcome %s", getIntent().getStringExtra(ARG_NAME)));
        }
        if (getIntent().getStringExtra(ARG_NAME) != "Guest") {
            googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);
            firebaseAuth = FirebaseAuth.getInstance();
        }

        autoCompleteTextViewSrc = findViewById(R.id.autocompleteSrc);
        autoCompleteTextViewDest = findViewById(R.id.autocompleteDest);
        chipGroup = findViewById(R.id.chipGroup);
        //set source adapter
        autoCompleteTextViewSrc.setAdapter(new PlacesAutoComplete(MainActivity.this, android.R.layout.simple_list_item_1));
        //set destination adapter
        autoCompleteTextViewDest.setAdapter(new PlacesAutoComplete(MainActivity.this, android.R.layout.simple_list_item_1));
        //
//        ScrollView scrollView = findViewById(R.id.scrollViewId);
//        scrollView.setOnTouchListener(new View.OnTouchListener(){
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                in.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
//                return true;
//            }
//        });
        //source item click listener
        autoCompleteTextViewSrc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                Log.d("Address : ", autoCompleteTextViewSrc.getText().toString());
//                LatLng latLngCoordinates = getLatLngCordFromAddress(autoCompleteTextViewSrc.getText().toString());
//                addressMap.put("SourceAddress", autoCompleteTextViewSrc.getText().toString());
//                if(latLngCoordinates != null) {
//                    addressMap.put("SourceLatLng", autoCompleteTextViewSrc.getText().toString());
////                    Log.d("Lat Lng : ", " " + latLngCoordinates.latitude + " " + latLngCoordinates.longitude);
//                    Address addressString = getAddressFromLatLngCord(latLngCoordinates);
//                    if(addressString!=null) {
////                        Log.d("Address : ", "" + addressString.toString());
////                        Log.d("Address Line : ",""+ addressString.getAddressLine(0));
////                        Log.d("Phone : ",""+ addressString.getPhone());
////                        Log.d("Pin Code : ",""+ addressString.getPostalCode());
////                        Log.d("Feature : ",""+ addressString.getFeatureName());
////                        Log.d("More : ",""+ addressString.getLocality());
//                    }
//                    else {
////                        Log.d("Adddress","Address Not Found");
//                    }
//                }
//                else {
////                    Log.d("Lat Lng","Lat Lng Not Found");
//                }
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
            }
        });
        //destination item click listener
        autoCompleteTextViewDest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                Log.d("Address : ", autoCompleteTextViewSrc.getText().toString());
//                LatLng latLngCoordinates = getLatLngCordFromAddress(autoCompleteTextViewDest.getText().toString());
//                addressMap.put("DestinationAddress", autoCompleteTextViewDest.getText().toString());
//                if(latLngCoordinates != null) {
//                    addressMap.put("DestinationLatLng", autoCompleteTextViewDest.getText().toString());
////                    Log.d("Lat Lng : ", " " + latLngCoordinates.latitude + " " + latLngCoordinates.longitude);
//                    Address addressString = getAddressFromLatLngCord(latLngCoordinates);
//                    if(addressString!=null) {
////                        Log.d("Address : ", "" + addressString.toString());
////                        Log.d("Address Line : ",""+ addressString.getAddressLine(0));
////                        Log.d("Phone : ",""+ addressString.getPhone());
////                        Log.d("Pin Code : ",""+ addressString.getPostalCode());
////                        Log.d("Feature : ",""+ addressString.getFeatureName());
////                        Log.d("More : ",""+ addressString.getLocality());
//                    }
//                    else {
////                        Log.d("Adddress","Address Not Found");
//                    }
//                }
//                else {
////                    Log.d("Lat Lng","Lat Lng Not Found");
//                }
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
            }
        });

        Button nextButton = (Button) findViewById(R.id.button);

        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openStep2Activity();
            }
        });
    }

    private void openStep2Activity() {
        Intent intent = new Intent(this, SecondActivity.class);
        Bundle bundle = new Bundle();
        String srcAddress = autoCompleteTextViewSrc.getText().toString();
        String destAddress = autoCompleteTextViewDest.getText().toString();
        String checkedChip = getSelectedMode();
        Log.d("src", srcAddress);
        Log.d("dest", destAddress);
        Log.d("Mode", checkedChip);
        if (!srcAddress.toString().isEmpty() && !destAddress.toString().isEmpty()) {
            bundle.putString("SourceAddress", srcAddress);
            bundle.putString("DestinationAddress", destAddress);
            bundle.putString("Mode", checkedChip);
            intent.putExtras(bundle);
            //call second activity
            startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.errorSrcDest),
                    Toast.LENGTH_LONG).show();
        }
    }

    private String getSelectedMode() {
        int i = 0;
        String checked = "driving";
        while (i < 4) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if (chip.isChecked()) {
                switch (i) {
                    case 1:
                        checked = "bicycling";
                        break;
                    case 2:
                        checked = "walking";
                        break;
                    case 3:
                        checked = "transit";
                        break;
                    default:
                        checked = "driving";
                }
            }
            i++;
        }
        ;
        return checked;
    }

    private LatLng getLatLngCordFromAddress(String address) {

        Geocoder geoCoder = new Geocoder(MainActivity.this);
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

    private Address getAddressFromLatLngCord(LatLng latLng) {
        Geocoder geoCoder = new Geocoder(MainActivity.this);
        List<Address> addressesList;
        try {
            addressesList = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 5);
            if (addressesList != null) {
                Address addressLine = addressesList.get(0);
                return addressLine;
            } else {
                return null;
            }
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }

    }

//    private void getDataFromFirebase() {
//
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                usersTripInfoList.clear();
//                System.out.println("Children Count: " + snapshot.getChildrenCount());
//                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                    UsersTripInfo usersTripInfoRead = postSnapshot.getValue(UsersTripInfo.class);
//                    System.out.println("The message from database: " + usersTripInfoRead.getUserTripPath());
//                    usersTripInfoList.add(usersTripInfoRead);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                System.out.println("The read failed: " + error.getMessage());
//            }
//        });
//
//    }

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
                                System.out.println("The message from database: " + usersTripInfoRead.getUserTripPath());
                                usersTripInfoList.add(usersTripInfoRead);
                            }

                            Intent intent = new Intent(MainActivity.this, TripHistoryActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("tripHistoryList", (Serializable) usersTripInfoList);
                            intent.putExtras(bundle);
                            startActivityForResult(intent, 127);
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

    private void logout () {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
