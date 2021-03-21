package com.example.smarttrip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.smarttrip.utils.PlacesAutoComplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
//    public Map<String, String> addressMap = new HashMap<>();
    public static AutoCompleteTextView autoCompleteTextViewSrc = null;
    public static AutoCompleteTextView autoCompleteTextViewDest = null;
    public  static ChipGroup chipGroup = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        nextButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                openStep2Activity();
            }
        });
    }

    private void openStep2Activity(){
        Intent intent = new Intent(this, SecondActivity.class);
        Bundle bundle = new Bundle();
        String srcAddress = autoCompleteTextViewSrc.getText().toString();
        String destAddress = autoCompleteTextViewDest.getText().toString();
        String checkedChip = getSelectedMode();
        Log.d("src", srcAddress);
        Log.d("dest", destAddress);
        Log.d("Mode", checkedChip);
        if(!srcAddress.toString().isEmpty() && !destAddress.toString().isEmpty()){
            bundle.putString("SourceAddress", srcAddress);
            bundle.putString("DestinationAddress", destAddress);
            bundle.putString("Mode", checkedChip);
            intent.putExtras(bundle);
            //call second activity
            startActivity(intent);
        }else{
            Toast.makeText(this, getString(R.string.errorSrcDest),
                    Toast.LENGTH_LONG).show();
        }
    }
    private String getSelectedMode(){
        int i = 0;
        String checked = "driving";
        while (i < 4) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if (chip.isChecked() ) {
                switch(i) {
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
        };
        return checked;
    }
    private LatLng getLatLngCordFromAddress(String address){

        Geocoder geoCoder = new Geocoder(MainActivity.this);
        List<Address> addressesList;

        try {
            addressesList = geoCoder.getFromLocationName(address, 1);
            if(addressesList != null){
                Address addressItem = addressesList.get(0);
                LatLng latLng = new LatLng(addressItem.getLatitude(), addressItem.getLongitude());
                return latLng;
            }
            else{
                return null;
            }
        }
        catch (Exception err){
            err.printStackTrace();
            return null;
        }

    }

    private Address getAddressFromLatLngCord(LatLng latLng){
        Geocoder geoCoder = new Geocoder(MainActivity.this);
        List<Address> addressesList;
        try {
            addressesList = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 5);
            if(addressesList != null){
                Address addressLine = addressesList.get(0);
                return addressLine;
            }
            else{
                return null;
            }
        }
        catch (Exception err){
            err.printStackTrace();
            return null;
        }

    }

}
