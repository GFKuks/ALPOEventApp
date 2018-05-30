package com.alpoeventapp.qualityapp.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alpoeventapp.qualityapp.R;
import com.alpoeventapp.qualityapp.models.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Klase, kas atbilst lietotāja pasākuma detalizēta skata aktivitātei
 */
public class UserEventDetailActivity extends AppCompatActivity {

    private EditText eventTitle;
    private EditText eventAddress;
    private EditText eventDate;
    private EditText eventDescription;
    private TextView eventGuestCount;

    private String eventId;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        eventTitle = findViewById(R.id.etBrowseDetailTitle);
        eventAddress = findViewById(R.id.etBrowseDetailAddress);
        eventDate = findViewById(R.id.etBrowseDetailDate);
        eventDescription = findViewById(R.id.etBrowseDetailDescription);
        eventGuestCount = findViewById(R.id.tvBrowseDetailGuestCount);
        Button eventEdit = findViewById(R.id.btnEventInteract);

        setTitle(getString(R.string.app_name) + ": "+ getString(R.string.user_events_title));
        eventEdit.setText(R.string.btn_edit);

        Intent intent = getIntent();

        eventId = intent.getStringExtra("eventId");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("events").child(eventId);

        ValueEventListener eventListener = new ValueEventListener() {   //notikumu uztvērējs, ar kura
            @Override                                                   //palīdzību aizpilda laukus
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
                Toast.makeText(UserEventDetailActivity.this, String.valueOf(databaseError.getCode()), Toast.LENGTH_SHORT).show();
            }
        };

        databaseReference.addValueEventListener(eventListener);

        eventEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserEventDetailActivity.this, UserEventUpdateActivity.class);
                intent.putExtra("eventId", eventId);

                startActivity(intent);
            }
        });
    }
}