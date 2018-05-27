package com.alpoeventapp.qualityapp.views;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.alpoeventapp.qualityapp.R;
import com.alpoeventapp.qualityapp.controllers.EventViewHolder;
import com.alpoeventapp.qualityapp.models.Event;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserEventsListActivity extends AppCompatActivity {
    private static final String TAG = "UserEventsListActivity";
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mEventReference;
    private DatabaseReference mUserEventIndex;
    private FirebaseRecyclerAdapter mFirebaseAdapter;

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "UserEvents: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_events);
        final View parentLayout = findViewById(android.R.id.content);
        mRecyclerView = findViewById(R.id.recycler_view);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mFirebaseAuth = FirebaseAuth.getInstance();
        mEventReference = FirebaseDatabase.getInstance().getReference().child("events");//.child(mFirebaseAuth.getUid());
        mUserEventIndex = FirebaseDatabase.getInstance().getReference().child("user-events").child(mFirebaseAuth.getUid());

        mUserEventIndex.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    Snackbar.make(parentLayout, "Jums nav pasākumu! Lai izveidotu pasākumu, atveriet izvēlni augšējā labajā stūrī!", Snackbar.LENGTH_INDEFINITE).show();
                } else {
                    setUpFirebaseAdapter();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void setUpFirebaseAdapter() {

        FirebaseRecyclerOptions<Event> options = new FirebaseRecyclerOptions.Builder<Event>()
                .setIndexedQuery(mUserEventIndex, mEventReference, Event.class)
                .build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Event, EventViewHolder> (options) {
            @Override
            protected void onBindViewHolder(EventViewHolder holder, int position, Event model) {
                holder.bindEvent(model);
            }

            @NonNull
            @Override
            public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse_item, parent, false);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String eventId = mFirebaseAdapter.getRef(mRecyclerView.getChildAdapterPosition(v)).getKey();
                        Log.d(TAG, "onClick: key is " + eventId);
                        Intent intent = new Intent(UserEventsListActivity.this, UserEventsDetailActivity.class);
                        intent.putExtra("eventId", eventId);

                        startActivity(intent);
                    }
                });
                return new EventViewHolder(view);
            }


        };
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mFirebaseAdapter);

        mFirebaseAdapter.startListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFirebaseAdapter.stopListening();
    }

    private void Logout() {
        mFirebaseAuth.signOut();
        finish();
        startActivity(new Intent(UserEventsListActivity.this, MainActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {

            case R.id.savedEventsMenu: {
                startActivity(new Intent(UserEventsListActivity.this, SavedEventsListActivity.class));
                break;
            }

            case R.id.createEventMenu: {
                startActivity(new Intent(UserEventsListActivity.this, CreateEventActivity.class));
                break;
            }
            case R.id.logoutMenu: {
                Logout();
                break;
            }
            case R.id.profileMenu: {
                startActivity(new Intent(UserEventsListActivity.this, ProfileActivity.class));
                break;
            }

            case R.id.findEventMenu: {
                startActivity(new Intent(UserEventsListActivity.this, BrowseEventsListActivity.class));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
