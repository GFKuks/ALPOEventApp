package com.alpoeventapp.qualityapp.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alpoeventapp.qualityapp.R;
import com.alpoeventapp.qualityapp.models.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserEventsDetailActivity extends AppCompatActivity {

    private EditText eventTitle;
    private EditText eventAddress;
    private EditText eventDate;
    private EditText eventDescription;
    private TextView eventGuestCount;
    private Button eventEdit;

    private FirebaseAuth mFirebaseAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        eventTitle = findViewById(R.id.etBrowseDetailTitle);
        eventAddress = findViewById(R.id.etBrowseDetailAddress);
        eventDate = findViewById(R.id.etBrowseDetailDate);
        eventDescription = findViewById(R.id.etBrowseDetailDescription);
        eventGuestCount = findViewById(R.id.tvBrowseDetailGuestCount);
        eventEdit = findViewById(R.id.btnEventInteract);

        eventEdit.setText(R.string.btn_edit);

        Intent intent = getIntent();
        final String eventId = intent.getStringExtra("eventId");
        final String authorId = intent.getStringExtra("authorId");

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user-events").child(authorId).child(eventId);

        final ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() != 0) {
                    Event event = dataSnapshot.getValue(Event.class);
                    eventTitle.setText(event.getTitle());
                    eventAddress.setText(event.getAddress());
                    eventDate.setText(event.getDate());
                    eventDescription.setText(event.getDescription());

                    String guestCount = "Viesu skaits: " + event.getGuestCount() + "/" + event.getGuestMaxCount();
                    eventGuestCount.setText(guestCount);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UserEventsDetailActivity.this, String.valueOf(databaseError.getCode()), Toast.LENGTH_SHORT).show();
            }
        };

        databaseReference.addValueEventListener(eventListener);

        eventEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserEventsDetailActivity.this, UpdateUserEventActivity.class);
                intent.putExtra("eventId", eventId);
                intent.putExtra("authorId", authorId);

                startActivity(intent);
            }
        });
    }
}