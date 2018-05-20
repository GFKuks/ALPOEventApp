package com.alpoeventapp.qualityapp.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.alpoeventapp.qualityapp.R;
import com.alpoeventapp.qualityapp.controllers.BrowseEventViewHolder;
import com.alpoeventapp.qualityapp.controllers.UserEventViewHolder;
import com.alpoeventapp.qualityapp.models.Event;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserEventsListActivity extends AppCompatActivity {
    private static final String TAG = "UserEventsListActivity";
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mEventReference;
    private FirebaseRecyclerAdapter mFirebaseAdapter;

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "UserEvents: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_events);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mFirebaseAuth = FirebaseAuth.getInstance();
        mEventReference = FirebaseDatabase.getInstance().getReference().child("user-events").child(mFirebaseAuth.getUid());
        setUpFirebaseAdapter();

    }

    private void setUpFirebaseAdapter() {
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Event, UserEventViewHolder> (Event.class,
                R.layout.browse_item, UserEventViewHolder.class, mEventReference) {
            @Override
            protected void populateViewHolder(UserEventViewHolder viewHolder, Event model, int position) {
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
