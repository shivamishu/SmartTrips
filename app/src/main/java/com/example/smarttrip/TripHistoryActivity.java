package com.example.smarttrip;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.smarttrip.model.UsersTripInfo;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TripHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_history_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        ArrayList<UsersTripInfo> userTripHistoryList = (ArrayList<UsersTripInfo>) bundle.getSerializable("tripHistoryList");

//        List<String> trip_history_array = new ArrayList<String>();
//        for (int i = 0; i < userTripHistoryList.size(); i++) {
//            UsersTripInfo tripPaths = userTripHistoryList.get(i);
//            trip_history_array.add(tripPaths.getUserTripPath());
//        }
        ArrayAdapter<UsersTripInfo> adapter = new ArrayAdapter<UsersTripInfo>(this,
                R.layout.trip_history_view, userTripHistoryList);
        ListView listView = (ListView) findViewById(R.id.tripHistory_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UsersTripInfo selectedItem = (UsersTripInfo) parent.getItemAtPosition(position);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(selectedItem.getUserTripPath()));
                startActivity(intent);
            }
        });

    }
}