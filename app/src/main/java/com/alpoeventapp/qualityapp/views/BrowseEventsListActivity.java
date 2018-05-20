package com.alpoeventapp.qualityapp.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.alpoeventapp.qualityapp.R;
import com.alpoeventapp.qualityapp.controllers.BrowseEventViewHolder;
import com.alpoeventapp.qualityapp.models.Event;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BrowseEventsListActivity extends AppCompatActivity {
    private static final String TAG = "BrowseEventsListActivit";
    private DatabaseReference mEventReference;
    private FirebaseRecyclerAdapter mFirebaseAdapter;

    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_events);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mEventReference = FirebaseDatabase.getInstance().getReference().child("events");
        setUpFirebaseAdapter();

    }

    private void setUpFirebaseAdapter() {
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Event, BrowseEventViewHolder> (Event.class,
                R.layout.browse_item, BrowseEventViewHolder.class, mEventReference) {
            @Override
            protected void populateViewHolder(BrowseEventViewHolder viewHolder, Event model, int position) {
                viewHolder.bindEvent(model);
            }
        };
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mFirebaseAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFirebaseAdapter.cleanup();
    }
}
