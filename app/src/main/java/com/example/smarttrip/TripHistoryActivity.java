package com.example.smarttrip;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.example.smarttrip.model.UsersTripInfo;
import com.example.smarttrip.utils.IOnGetDataListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TripHistoryActivity extends AppCompatActivity {
    public static Stack<Intent> parents = new Stack<Intent>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parents.push(getIntent());
        setContentView(R.layout.trip_history_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_launcher_round);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(newdrawable);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
            toolbar.setNavigationIcon(newdrawable);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent parentActivityIntent = new Intent(v.getContext(), parents.pop().getClass());
                    parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startService(parentActivityIntent);
                    finish();
                }
            });
        }
        toolbar.getOverflowIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        toolbar.inflateMenu(R.menu.menu_main);


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
        MenuItem tripHistoryItem = menu.findItem(R.id.trip_history);
        tripHistoryItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivityForResult(intent, 100);
                finish();
                return true;
            case android.R.id.home:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(TripHistoryActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}