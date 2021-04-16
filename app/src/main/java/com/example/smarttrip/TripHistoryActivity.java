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

        List<String> trip_title_array = new ArrayList<String>();
        for (int i = 0; i < userTripHistoryList.size(); i++) {
            UsersTripInfo tripTitle = userTripHistoryList.get(i);
            trip_title_array.add(tripTitle.getUserTripTitle());
        }
        List<String> trip_link_array = new ArrayList<String>();
        for (int i = 0; i < userTripHistoryList.size(); i++) {
            UsersTripInfo tripLinks = userTripHistoryList.get(i);
            trip_link_array.add(tripLinks.getUserTripPath());
        }

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.trip_history_view, trip_title_array);
        ListView listView = (ListView) findViewById(R.id.tripHistory_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri uri = Uri.parse(trip_link_array.get(position));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

    }
}