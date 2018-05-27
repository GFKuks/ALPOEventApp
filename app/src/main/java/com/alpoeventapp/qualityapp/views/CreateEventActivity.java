package com.alpoeventapp.qualityapp.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alpoeventapp.qualityapp.R;
import com.alpoeventapp.qualityapp.models.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CreateEventActivity extends AppCompatActivity {

    private EditText eventTitle;
    private EditText eventAddress;
    private EditText eventDate;
    private EditText eventDescription;
    private EditText eventMaxGuestCount;
    private Button cancel, confirm;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;

    private String title;
    private String address;
    private String date;
    private String description;
    private String authorId;
    private int guestMaxCount;
    private String guests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        eventTitle = findViewById(R.id.etEventCreateTitle);
        eventAddress = findViewById(R.id.etEventCreateAddress);
        eventDate = findViewById(R.id.etEventCreateDate);
        eventDescription = findViewById(R.id.etEventCreateDescription);
        eventMaxGuestCount = findViewById(R.id.etMaxGuestCount);

        cancel = findViewById(R.id.btnCancelEventNew);
        confirm = findViewById(R.id.btnConfirmEventNew);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    sendEventData(title, address, date, description, authorId, guestMaxCount);
                    Toast.makeText(CreateEventActivity.this, "Pasākums izveidots.", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(CreateEventActivity.this, UserEventsListActivity.class));
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private boolean validate() {
        boolean result = false;

        title = eventTitle.getText().toString();
        address = eventAddress.getText().toString();
        date = eventDate.getText().toString();
        description = eventDescription.getText().toString();

        if (eventMaxGuestCount.getText().toString().matches("")) {
            guestMaxCount = 20;
        } else {
            guestMaxCount = Integer.valueOf(eventMaxGuestCount.getText().toString());
        }

        if (title.isEmpty() || address.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Lūdzu, aizpildiet obligātos laukus!", Toast.LENGTH_SHORT).show();
        } else if (title.length() > 25) {
            Toast.makeText(this, "Nosaukums nedrīkst būt garāks par 25 rakstu zīmēm!", Toast.LENGTH_SHORT).show();
        } else {
            authorId = mFirebaseAuth.getUid();
            result = true;
        }

        return result;
    }

    /**
     * Metode, izmantojot atsauci uz Firebase datu bāzi, iegūst unikālu identifikatoru "events" zarā,
     * un tajā ievieto jaunu pasākuma ierakstu.
     */
    private void sendEventData(String title, String address, String date, String description, String authorId, int guestMaxCount) {
        String key = mDatabaseReference.child("events").push().getKey();
        Event newEvent = new Event(key, title, address, date, description, authorId, guestMaxCount);
        Map<String, Object> eventValues = newEvent.toMap();
        Map<String, Object> ownerValues = new HashMap<>();
        ownerValues.put(key, true);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/events/" + key, eventValues);
//        childUpdates.put("/user-events/" + authorId + "/" + ownerValues, null);

        mDatabaseReference.updateChildren(childUpdates);
        mDatabaseReference.child("user-events").child(mFirebaseAuth.getUid()).updateChildren(ownerValues);

    }
}
