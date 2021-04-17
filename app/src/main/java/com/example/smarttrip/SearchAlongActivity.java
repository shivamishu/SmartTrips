package com.example.smarttrip;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttrip.model.GoogleResponse;
import com.example.smarttrip.model.NearBySearchAPICall;
import com.example.smarttrip.utils.IDistanceMatrixAPICall;
import com.example.smarttrip.utils.IOnGetDataListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

public class SearchAlongActivity extends AppCompatActivity implements IDistanceMatrixAPICall {
    public static final String apiKey = BuildConfig.MAPS_API_KEY;
    ArrayList<GoogleResponse> data = new ArrayList<>();
    public HashSet<GoogleResponse> selectedList = new HashSet<>();
    public RecyclerView recyclerCard;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    public static Stack<Intent> parents = new Stack<Intent>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_along_search);
        parents.push(getIntent());
        //TextView titleAlong = (TextView) findViewById(R.id.along_title);
        Bundle bundle = getIntent().getExtras();
        selectedList = (HashSet<GoogleResponse>) getIntent().getSerializableExtra("selectedList");
        String filterType = bundle.getString("filterType");
        String title;
        switch (filterType) {
            case "tourist_attraction":
                title = "popular places";
                break;
            case "restaurant":
                title = "restaurants";
                break;
            case "gas_station":
                title = "gas stations";
                break;
            case "ev charger":
                title = "ev chargers";
                break;
            default:
                title = "places";
        }
        title = String.format(getString(R.string.add_places_along_the_route2), title);
        //titleAlong.setText(title);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            Drawable drawable= ContextCompat.getDrawable(this, R.drawable.ic_launcher_round);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
            //newdrawable.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(newdrawable);
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
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
            mTitle.setText(title);
        }
        toolbar.getOverflowIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        toolbar.inflateMenu(R.menu.menu_main);
        String mode = bundle.getString("mode");
        String srcAddress = bundle.getString("srcAddress");
        List<LatLng> polyPoints = bundle.getParcelableArrayList("waypointlist");
        List<LatLng> upperBound = new ArrayList<>();
        List<LatLng> lowerBound = new ArrayList<>();
        int radius = 10000;

        int polyLength = polyPoints.size();
        Log.d("AllWayPointsLength==============================================>", String.valueOf(polyLength));

        for (int j = 0; j <= polyLength - 1; j++) {
            LatLng pLatLng = polyPoints.get(j);
            double lat = pLatLng.latitude;
            double lng = pLatLng.longitude;
            int R = 6378137;
            double pi = 3.14;
            //distance in meters
            double upper_offset = 300;
            double lower_offset = -300;
            double Lat_up = upper_offset / R;
            double Lat_down = lower_offset / R;
            //OffsetPosition, decimal degrees
            double lat_upper = lat + (Lat_up * 180) / pi;
            double lat_lower = lat + (Lat_down * 180) / pi;
            LatLng latLngUpper = new LatLng(lat_upper, lng);
            LatLng latLngLower = new LatLng(lat_lower, lng);
            upperBound.add(latLngUpper);
            lowerBound.add(latLngLower);
        }

        Log.d("AllWayPointsLength==============================================>", String.valueOf(polyPoints));
        Log.d("UpperBoundAppendLowerBound======================================>", String.valueOf(upperBound));
        Log.d("LowerBound======================================================>", String.valueOf(lowerBound));
        Collections.reverse(lowerBound);
        Log.d("ReverseLowerBound===============================================>", String.valueOf(lowerBound));
        upperBound.addAll(lowerBound);
        Log.d("UpperBoundAppendLowerBound======================================>", String.valueOf(upperBound));

        for (int i = 0; i <= polyPoints.size(); i += 40) {
            LatLng cords = polyPoints.get(i);
            Log.d("cordinates are ", cords.latitude + "," + cords.longitude);
            boolean containsvalidCords = true;
            containsvalidCords = PolyUtil.containsLocation(cords.latitude, cords.longitude, upperBound, true);
            if (containsvalidCords == true) {
                Log.d("CordinatesWithinPolygon=========================================>", cords.latitude + "," + cords.longitude);
                String apiURL = null;
                try {
                    String locationCords = "&location=" + URLEncoder.encode(String.valueOf(cords.latitude), "utf-8") + "," + URLEncoder.encode(String.valueOf(cords.longitude), "utf-8");
                    String wayptRadius = "&radius=" + URLEncoder.encode(String.valueOf(radius), "utf-8");
                    String searchType = "&type=" + filterType;
                    searchType = filterType == "ev charger" ? "&keyword=ev charger" : searchType;
                    apiURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=" + apiKey + locationCords + wayptRadius + searchType;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.d("ApiUrl==========================================================>", apiURL);
                new NearBySearchAPICall(this, mode, srcAddress).execute(apiURL);

            }
        }

//        setSupportActionBar(findViewById(R.id.toolbar));
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                Snackbar.make(view, getString(R.string.filter_added), Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        ArrayList<String> data = new ArrayList<String>();
//        for (int i = 0; i < 30; i++) {
//            data.add("Place " + i);
//        }
        FloatingActionButton addToTrip = (FloatingActionButton) findViewById(R.id.fab);
        addToTrip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.filter_added), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent data = new Intent();
                data.putExtra("selectedList", selectedList);
                // Activity finished ok, return the data
                setResult(RESULT_OK, data);
                finish();
            }
        });
        recyclerCard = findViewById(R.id.card_recycler_view);
        recyclerCard.setAdapter(new MainCardAdapter(this, data));
    }

    public class MainCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView titleView;
        public TextView distanceView;
        public TextView timeView;
        public CheckBox checkBox;
        public View item;
        public TextView openNowView;
        public RatingBar ratingBarView;

        public MainCardViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.titleView);
            timeView = itemView.findViewById(R.id.time);
            distanceView = itemView.findViewById(R.id.distance);
            checkBox = itemView.findViewById(R.id.checkBox);
            item = itemView;
            openNowView = itemView.findViewById(R.id.opening);
            ratingBarView = itemView.findViewById(R.id.ratingBar);
        }

        @Override
        public void onClick(View v) {
            CheckBox cb = v.findViewById(R.id.checkBox);
        }
    }

    public class MainCardAdapter extends RecyclerView.Adapter<SearchAlongActivity.MainCardViewHolder> {
        private ArrayList<GoogleResponse> dataList;
        private Context context;

        public MainCardAdapter(Context context, ArrayList<GoogleResponse> dataList) {
            this.context = context;
            this.dataList = dataList;
        }


        @NonNull
        @Override
        public MainCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            CardView itemView = (CardView) LayoutInflater.from(context).inflate(R.layout.card_item_view, parent, false);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    CheckBox cb = v.findViewById(R.id.checkBox);
//                    cb.toggle();
//                }
//            });
            return new MainCardViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MainCardViewHolder holder, int position) {
            GoogleResponse item = dataList.get(position);
            holder.titleView.setText(item.getName());
            holder.timeView.setText(item.getDurationText());
            holder.distanceView.setText(item.getDistanceText());
            holder.openNowView.setText(item.getOpenNow());
            if (item.getRating() != null && StringUtils.isNotEmpty(item.getRating())) {
                holder.ratingBarView.setRating(Float.parseFloat(item.getRating()));
            }
            if (selectedList.contains(item)) {
                CheckBox checkBox = holder.checkBox;
                checkBox.setChecked(true);
            }
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox cb = holder.checkBox;
                    cb.toggle();
                    GoogleResponse currItem = dataList.get(position);
                    if (cb.isChecked()) {
                        selectedList.add(currItem);
                    } else {
                        //remove from list
                        selectedList.remove(currItem);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }

    @Override
    public void onDistanceMatrixAPICallSuccess(GoogleResponse finalResult) {

//        Log.d("NearByNames=====================================================>", finalResult.getName());
//        Log.d("NearByDistanceText==============================================>", finalResult.getDistanceText());
//        Log.d("NearByDistanceValue=============================================>", finalResult.getDistanceValue());
//        Log.d("NearByDurationText==============================================>", finalResult.getDurationText());
//        Log.d("NearByDurationValue=============================================>", finalResult.getDurationValue());
        String isOpen;
        String distanceText = finalResult.getDistanceText().replace("mi", getString(R.string.miles));
        finalResult.setDistanceText(distanceText);
        if (finalResult.getOpenNow() != null && StringUtils.isNotEmpty(finalResult.getOpenNow())) {
            if (finalResult.getOpenNow() == "true") {
                isOpen = getString(R.string.opening);
                ;
            } else {
                isOpen = getString(R.string.closed);
                ;
            }
            // Log.d("NearByOpenNow===================================================>", finalResult.getOpenNow());
            finalResult.setOpenNow(isOpen);
        } else {
            finalResult.setOpenNow("NA");
        }
        int size = data.size();
        data.add(finalResult);
//        RecyclerView recyclerCard = findViewById(R.id.card_recycler_view);
//        recyclerCard.setAdapter(new MainCardAdapter(this, data));
//        recyclerCard.getAdapter().notifyDataSetChanged();
        recyclerCard.getAdapter().notifyItemInserted(size);

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
                startActivityForResult(intent, 129);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout () {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(SearchAlongActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
