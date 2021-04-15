package com.example.smarttrip;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smarttrip.utils.PlacesAutoComplete;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //    public Map<String, String> addressMap = new HashMap<>();
    public static AutoCompleteTextView autoCompleteTextViewSrc = null;
    public static AutoCompleteTextView autoCompleteTextViewDest = null;
    public static ChipGroup chipGroup = null;
    private static final int REQUEST_CODE = 0612;


    private static final String TAG = "MainActivity";
    private static final String ARG_NAME = "username";
    FirebaseAuth firebaseAuth;
    GoogleSignInClient googleSignInClient;

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
        setContentView(R.layout.activity_main);

        //ImageView ivUserImage = (ImageView) findViewById(R.id.ivUserImage);

        //Picasso.get().load(getIntent().getStringExtra("imageUri")).resize(50,50).centerCrop().into(ivUserImage);

        TextView textView = findViewById(R.id.textViewWelcome);

        if (getIntent().hasExtra(ARG_NAME)) {
            textView.setText(String.format("Welcome - %s", getIntent().getStringExtra(ARG_NAME)));
        }
        if (getIntent().getStringExtra(ARG_NAME) != "Guest") {
            findViewById(R.id.buttonLogout).setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonLogout:
                signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
        }
    }

    private void signOut() {
        // Firebase sign out
        firebaseAuth.signOut();
        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Google Sign In failed, update UI appropriately
                        Log.w(TAG, "Signed out of google");
                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        firebaseAuth.signOut();
        // Google revoke access
        googleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Google Sign In failed, update UI appropriately
                        Log.w(TAG, "Revoked Access");
                    }
                });
    }

}
