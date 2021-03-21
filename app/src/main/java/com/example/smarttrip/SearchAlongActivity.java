package com.example.smarttrip;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class SearchAlongActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_along_search);
        TextView titleAlong = (TextView) findViewById(R.id.along_title);
        Bundle bundle = getIntent().getExtras();
        String filterType = bundle.getString("filterType");
        String title = String.format(getString(R.string.add_places_along_the_route2), filterType);
        titleAlong.setText(title);
//        setSupportActionBar(findViewById(R.id.toolbar));
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                Snackbar.make(view, getString(R.string.filter_added), Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        ArrayList<String> data = new ArrayList<String>();
        for (int i = 0; i < 30; i++) {
            data.add("Place " + i);
        }
        FloatingActionButton addToTrip = (FloatingActionButton) findViewById(R.id.fab);
        addToTrip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.filter_added), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                finish();
            }
        });
        RecyclerView recyclerCard = findViewById(R.id.card_recycler_view);
        recyclerCard.setAdapter(new MainCardAdapter(this, data));
    }

    public class MainCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView titleView;
//        public TextView distanceView;
//        public TextView timeView;

        public MainCardViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.titleView);
        }

        @Override
        public void onClick(View v) {
            CheckBox cb = v.findViewById(R.id.checkBox);
        }
    }

    public class MainCardAdapter extends RecyclerView.Adapter<SearchAlongActivity.MainCardViewHolder> {
        private ArrayList<String> dataList;
        private Context context;

        public MainCardAdapter(Context context, ArrayList<String> dataList) {
            this.context = context;
            this.dataList = dataList;
        }


        @NonNull
        @Override
        public MainCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            CardView itemView = (CardView) LayoutInflater.from(context).inflate(R.layout.card_item_view, parent, false);
            itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = v.findViewById(R.id.checkBox);
                    cb.toggle();
                }
            });
            return new MainCardViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MainCardViewHolder holder, int position) {
            String item = dataList.get(position);
            holder.titleView.setText(item);
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }

}
