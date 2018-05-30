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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * Klase, kas atbilst visu pasākumu apskates aktivitātei.
 */
public class BrowseEventsListActivity extends AppCompatActivity {
    private static final String TAG = "BrowseEventsListActivit";
    private FirebaseAuth mFirebaseAuth;
    private FirebaseRecyclerAdapter mFirebaseAdapter;

    private RecyclerView mRecyclerView;
    private String queryParam;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_events);
        setTitle(getString(R.string.app_name) + ": "+ getString(R.string.event_title));

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFirebaseAuth = FirebaseAuth.getInstance();


        queryParam = "";    //Vaicājums noklusēti nav iestatīts. Iestata, ja padots no
                            //iepriekšējās aktivitātes
        Intent intent = getIntent();
        if (intent.getStringExtra("query") != null) {
            queryParam = intent.getStringExtra("query");
        }

        setUpFirebaseAdapter();

    }
    /**
     * Funkfcija sagatavo adapteri, kas, izmantojot atsauci uz datu bāzi, izveidos sarakstu
     * ar visiem pieejamajiem pasākumiem. Ja uz šo skatu nonāca izmantojot meklēšanas
     * funkcionalitāti, String objekts tiek padots kā parametrs, ļaujot atlasīt visus datus,
     * kas sākas ar norādīto frāzi.
     */
    private void setUpFirebaseAdapter() {

        Query query = FirebaseDatabase.getInstance().getReference().child("events").orderByChild("title")
                .startAt(queryParam).endAt(queryParam + "\uf8ff");

        FirebaseRecyclerOptions<Event> options = new FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(query, Event.class)
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
                        Intent intent = new Intent(BrowseEventsListActivity.this, EventDetailActivity.class);
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
        startActivity(new Intent(BrowseEventsListActivity.this, MainActivity.class));
        finishAffinity();
    }

    /**
     * Aktivitātes darba beigšanas laikā, jābeidz novērot izmaiņas adaptera referencē.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFirebaseAdapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_browse, menu);
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
                startActivity(new Intent(BrowseEventsListActivity.this, SavedEventsListActivity.class));
                break;
            }

            case R.id.createEventMenu: {
                startActivity(new Intent(BrowseEventsListActivity.this, CreateEventActivity.class));
                break;
            }
            case R.id.logoutMenu: {
                Logout();
                break;
            }
            case R.id.profileMenu: {
                startActivity(new Intent(BrowseEventsListActivity.this, ProfileActivity.class));
                break;
            }

            case R.id.findEventMenu: {
                break;
            }

            case R.id.userEventsMenu: {
                startActivity(new Intent(BrowseEventsListActivity.this, UserEventListActivity.class));
                break;
            }

            case R.id.actionSearch: {
                Intent intent = new Intent(BrowseEventsListActivity.this, SearchActivity.class); //creates intent with context and class
                startActivity(intent);  //starts activity with specified intent (this context and SearchActivity class)
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
