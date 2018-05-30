package com.alpoeventapp.qualityapp.views;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Klase nodrošina lietotāja izveidoto pasākumu sarakstu izveidi.
 */

public class UserEventListActivity extends AppCompatActivity {
    private static final String TAG = "UserEventListActivity";
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mEventReference;
    private DatabaseReference mUserEventIndex;
    private FirebaseRecyclerAdapter mFirebaseAdapter;

    private RecyclerView mRecyclerView;

    /**
     * Sākot aktivitāti, papildus parastajam xml izkārtojuma failam, tiek izmantots mRecyclerView
     * kā izkārtojums saraksta elementiem.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "UserEvents: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_events);
        setTitle(getString(R.string.app_name) + ": "+ getString(R.string.user_events_title));
        final View parentLayout = findViewById(android.R.id.content);
        mRecyclerView = findViewById(R.id.recycler_view);

/**
 * References uz Firebase datu bāzi. mFirebaseAuth palīdzēs iegūt lietotāja identifikatoru.
 * mEventReference ir norāde uz pasākumu mezglu un mUserEventIndex ir norāde uz lietotāja
 * pasākumu indeksu mezglu.
 */
        mFirebaseAuth = FirebaseAuth.getInstance();
        mEventReference = FirebaseDatabase.getInstance().getReference().child("events");
        mUserEventIndex = FirebaseDatabase.getInstance().getReference().child("user-events").child(mFirebaseAuth.getUid());

        mUserEventIndex.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) { //Ja lietotājam nav pasākumu, parādās paziņojums
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

    /**
     * Adaptera izveidošanas funkcija. Šajā sarakstā tiek izmantots lietotāja pasākumu indekss,
     * lai atlasītu rezultātus. Tiek norādīts, ka adapteris izmanto Event klasi, kas tiks ievietota
     * EventViewHolder viewHolder funkcijās, lai piesaistītu elementus sarakstam.
     */
    private void setUpFirebaseAdapter() {

        FirebaseRecyclerOptions<Event> options = new FirebaseRecyclerOptions.Builder<Event>()
                .setIndexedQuery(mUserEventIndex, mEventReference, Event.class)
                .build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Event, EventViewHolder> (options) {
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
                        Intent intent = new Intent(UserEventListActivity.this, UserEventDetailActivity.class);
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
        startActivity(new Intent(UserEventListActivity.this, MainActivity.class));
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
                startActivity(new Intent(UserEventListActivity.this, SavedEventsListActivity.class));
                break;
            }

            case R.id.createEventMenu: {
                startActivity(new Intent(UserEventListActivity.this, CreateEventActivity.class));
                break;
            }
            case R.id.logoutMenu: {
                Logout();
                break;
            }
            case R.id.profileMenu: {
                startActivity(new Intent(UserEventListActivity.this, ProfileActivity.class));
                break;
            }

            case R.id.findEventMenu: {
                startActivity(new Intent(UserEventListActivity.this, BrowseEventsListActivity.class));
                break;
            }

            case R.id.userEventsMenu: {
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
