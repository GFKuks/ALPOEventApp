package com.alpoeventapp.qualityapp.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alpoeventapp.qualityapp.R;
import com.alpoeventapp.qualityapp.models.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UpdateUserEventActivity extends AppCompatActivity {

    private EditText eventTitle;
    private EditText eventAddress;
    private EditText eventDate;
    private EditText eventDescription;
    private EditText eventMaxGuestCount;
    private Button delete, confirm;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;

    private String title;
    private String address;
    private String date;
    private String description;
    private String authorId;
    private String eventId;
    private int guestMaxCount;
    private String guests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_event);

        eventTitle = findViewById(R.id.etEditEventTitle);
        eventAddress = findViewById(R.id.etEditEventAddress);
        eventDate = findViewById(R.id.etEditEventDate);
        eventDescription = findViewById(R.id.etEditEventDescription);
        eventMaxGuestCount = findViewById(R.id.etEditMaxGuestCount);

        delete = findViewById(R.id.btnDeleteEvent);
        confirm = findViewById(R.id.btnSaveEventChanges);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        eventId = intent.getStringExtra("eventId");
        authorId = intent.getStringExtra("authorId");


        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user-events").child(authorId).child(eventId);

        final ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    Event event = dataSnapshot.getValue(Event.class);
                    eventTitle.setText(event.getTitle());
                    eventAddress.setText(event.getAddress());
                    eventDate.setText(event.getDate());
                    eventDescription.setText(event.getDescription());
                    eventMaxGuestCount.setText(String.valueOf(event.getGuestMaxCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UpdateUserEventActivity.this, String.valueOf(databaseError.getCode()), Toast.LENGTH_SHORT).show();
            }
        };

        databaseReference.addValueEventListener(eventListener);


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    updateEventData(eventId, title, address, date, description, authorId, guestMaxCount);
                    Toast.makeText(UpdateUserEventActivity.this, "Izmaiņas saglabātas.", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(UpdateUserEventActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(UpdateUserEventActivity.this, "Kļūda pasākuma izmaiņu saglabāšanā.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.removeEventListener(eventListener);
                deleteEvent();
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
        } else {
            authorId = mFirebaseAuth.getUid();
            result = true;
        }

        return result;
    }

    private void updateEventData(String eventId, String title, String address, String date, String description, String authorId, int guestMaxCount) {
        Event updatedEvent = new Event(eventId, title, address, date, description, authorId, guestMaxCount);
        Map<String, Object> eventValues = updatedEvent.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/events/" + eventId, eventValues);
        childUpdates.put("/user-events/" + authorId + "/" + eventId, eventValues);

        mDatabaseReference.updateChildren(childUpdates);
    }

    private void deleteEvent() {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/events/" + eventId, null);
        childUpdates.put("/user-events/" + authorId + "/" + eventId, null);
        mDatabaseReference.updateChildren(childUpdates);

        Toast.makeText(UpdateUserEventActivity.this, "Pasākums izdzēsts.", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(UpdateUserEventActivity.this, MainActivity.class));
    }
}
