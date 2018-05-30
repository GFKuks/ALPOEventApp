package com.alpoeventapp.qualityapp.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Klase atbilst lietotāja saglabāto pasākumu aktivitātei.
 */
public class SavedEventsListActivity extends AppCompatActivity {
    private static final String TAG = "UserEventListActivity";
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mEventReference;
    private DatabaseReference mSavedEventIndex;
    private FirebaseRecyclerAdapter mFirebaseAdapter;

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "UserEvents: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_events);
        mRecyclerView = findViewById(R.id.recycler_view);
        setTitle(getString(R.string.app_name) + ": "+ getString(R.string.saved_events_title));

        mFirebaseAuth = FirebaseAuth.getInstance();
        mEventReference = FirebaseDatabase.getInstance().getReference().child("events");
        mSavedEventIndex = FirebaseDatabase.getInstance().getReference().child("saved-events").child(mFirebaseAuth.getUid());

        setUpFirebaseAdapter();
    }

    /**
     * Funkfcija sagatavo adapteri, kas, izmantojot atsauci uz datu bāzi, kā arī indeksu sarakstu,
     * izveidos sarakstu ar lietotāja saglabātajiem pasākumiem.
     */
    private void setUpFirebaseAdapter() {

        FirebaseRecyclerOptions<Event> options = new FirebaseRecyclerOptions.Builder<Event>()
                .setIndexedQuery(mSavedEventIndex, mEventReference, Event.class)
                .build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>(options) {
            @Override
            protected void onBindViewHolder(EventViewHolder holder, int position, Event model) {
                holder.bindEvent(model);
            }

            /**
             * onCreateViewHolder darbojas katra viewHolder izveides laikā. Katram viewHolder
             * tiek pievienots notikumu uztvērējs, kas uz klikšķi iegūst pasākuma identifikatoru,
             * lai to padotu nākamajai aktivitātei un parādītu pareizo pasākumu.
             */
            @NonNull
            @Override
            public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse_item, parent, false);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String eventId = mFirebaseAdapter.getRef(mRecyclerView.getChildAdapterPosition(v)).getKey();
                        Log.d(TAG, "onClick: key is " + eventId);
                        Intent intent = new Intent(SavedEventsListActivity.this, EventDetailActivity.class);
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
    /**
     * Atteikšanās funkcija. Lietotājs nokļūst autentifikācijas aktivitātē,
     * tiek pabeigtas visas iepriekšējās aktivitātes.
     */
    private void Logout() {
        mFirebaseAuth.signOut();
        startActivity(new Intent(SavedEventsListActivity.this, MainActivity.class));
        finishAffinity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    /**
     * Navigācijas kods. Atkarībā no izvēlētās opcijas, lietotājs tiek aizvests uz attiecīgo
     * atkivitāti.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {

            case R.id.savedEventsMenu: {
                break;
            }

            case R.id.createEventMenu: {
                startActivity(new Intent(SavedEventsListActivity.this, CreateEventActivity.class));
                break;
            }
            case R.id.logoutMenu: {
                Logout();
                break;
            }
            case R.id.profileMenu: {
                startActivity(new Intent(SavedEventsListActivity.this, ProfileActivity.class));
                break;
            }

            case R.id.findEventMenu: {
                startActivity(new Intent(SavedEventsListActivity.this, BrowseEventsListActivity.class));
                break;
            }

            case R.id.userEventsMenu: {
                startActivity(new Intent(SavedEventsListActivity.this, UserEventListActivity.class));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}