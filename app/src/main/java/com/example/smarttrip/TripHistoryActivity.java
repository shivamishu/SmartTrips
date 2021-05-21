package com.example.smarttrip;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.example.smarttrip.model.GoogleResponse;
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
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Stack;
import java.util.TimeZone;

public class TripHistoryActivity extends AppCompatActivity {
    ArrayList<UsersTripInfo> userTripHistoryList = new ArrayList<>();
    public static Stack<Intent> parents = new Stack<Intent>();
    public RecyclerView recyclerCard;


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
//            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent parentActivityIntent = new Intent(v.getContext(), parents.pop().getClass());
//                    parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startService(parentActivityIntent);
//                    finish();
//                }
//            });
        }
        toolbar.getOverflowIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        toolbar.inflateMenu(R.menu.menu_main);


        Bundle bundle = getIntent().getExtras();
        userTripHistoryList = (ArrayList<UsersTripInfo>) bundle.getSerializable("tripHistoryList");
        recyclerCard = findViewById(R.id.tripHistory_list);
        recyclerCard.setAdapter(new TripHistoryActivity.MainCardAdapter(this, userTripHistoryList));
//        List<String> trip_title_array = new ArrayList<String>();
//        for (int i = 0; i < userTripHistoryList.size(); i++) {
//            UsersTripInfo tripTitle = userTripHistoryList.get(i);
//            trip_title_array.add(tripTitle.getUserTripTitle());
//        }
//        List<String> trip_link_array = new ArrayList<String>();
//        for (int i = 0; i < userTripHistoryList.size(); i++) {
//            UsersTripInfo tripLinks = userTripHistoryList.get(i);
//            trip_link_array.add(tripLinks.getUserTripPath());
//        }
//
//        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.trip_history_view, trip_title_array);
//        ListView listView = (ListView) findViewById(R.id.tripHistory_list);
//        listView.setAdapter(adapter);


//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Uri uri = Uri.parse(trip_link_array.get(position));
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                startActivity(intent);
//            }
//        });

    }

    public class MainCardViewHolder extends RecyclerView.ViewHolder {

        public TextView titleView;
        public TextView distanceView;
        public TextView timeView;
        public View item;
        public TextView tripTimestamp;
        public TextView tripMode;


        public MainCardViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.historyTitleView);
            timeView = itemView.findViewById(R.id.historyTime);
            distanceView = itemView.findViewById(R.id.historyDistance);
            item = itemView;
            tripTimestamp = itemView.findViewById(R.id.tripTimestamp);
            tripMode = itemView.findViewById(R.id.tripMode);

        }
    }

    public class MainCardAdapter extends RecyclerView.Adapter<TripHistoryActivity.MainCardViewHolder> {
        private ArrayList<UsersTripInfo> dataList;
        private Context context;

        public MainCardAdapter(Context context, ArrayList<UsersTripInfo> dataList) {
            this.context = context;
            this.dataList = dataList;
        }


        @NonNull
        @Override
        public TripHistoryActivity.MainCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            CardView itemView = (CardView) LayoutInflater.from(context).inflate(R.layout.trip_history_view, parent, false);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    CheckBox cb = v.findViewById(R.id.checkBox);
//                    cb.toggle();
//                }
//            });
            return new TripHistoryActivity.MainCardViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull TripHistoryActivity.MainCardViewHolder holder, int position) {
            UsersTripInfo item = dataList.get(position);
            holder.titleView.setText(item.getUserTripTitle());
            holder.distanceView.setText("Total Distance: "+ item.getTotalDistance());
            holder.timeView.setText("Total Time: "+ item.getTotalTime());
            holder.tripTimestamp.setText("Trip Date: " + item.getUserTripTimeStamp());
            String tripMode = item.getTripMode();
            int modeIcon;
            switch (tripMode) {
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

            holder.tripMode.setCompoundDrawablesWithIntrinsicBounds(0, 0, modeIcon, 0);


            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(item.getUserTripPath());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }

            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
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
                finish();
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