package com.alpoeventapp.qualityapp.views;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class EventDetailActivity extends AppCompatActivity {
    private static final String TAG = "EventDetailActivity";

    private EditText eventTitle;
    private EditText eventAddress;
    private EditText eventDate;
    private EditText eventDescription;
    private TextView eventGuestCount;
    private Button eventAttendToggle;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;
//    private DatabaseReference mAuthorDatabaseReference;
    private DatabaseReference mSavedEventDatabaseReference;

    private String eventId;
    private String authorId;
    private boolean userAttendance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        eventTitle = findViewById(R.id.etBrowseDetailTitle);
        eventAddress = findViewById(R.id.etBrowseDetailAddress);
        eventDate = findViewById(R.id.etBrowseDetailDate);
        eventDescription = findViewById(R.id.etBrowseDetailDescription);
        eventGuestCount = findViewById(R.id.tvBrowseDetailGuestCount);
        eventAttendToggle = findViewById(R.id.btnEventInteract);

        Intent intent = getIntent();
        eventId = intent.getStringExtra("eventId");
        final View parentLayout = findViewById(android.R.id.content);

        mFirebaseAuth = mFirebaseAuth.getInstance();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("events").child(eventId);
        mSavedEventDatabaseReference = FirebaseDatabase.getInstance().getReference().child("saved-events").child(mFirebaseAuth.getUid());


        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    Event event = dataSnapshot.getValue(Event.class);
                    eventTitle.setText(event.getTitle());
                    eventAddress.setText(event.getAddress());
                    eventDate.setText(event.getDate());
                    eventDescription.setText(event.getDescription());

                    authorId = event.getAuthorId();

                    String guestCount = "Viesu skaits: " + event.getGuestCount() + "/" + event.getGuestMaxCount();
                    eventGuestCount.setText(guestCount);

                    if ((!isUserAttending(mSavedEventDatabaseReference)) && (!authorId.equals(mFirebaseAuth.getUid()))) {
                        eventAttendToggle.setText("Pieteikties");
                    } else {
                        eventAttendToggle.setText("Atteikties");
                    }

                    if (authorId.equals(mFirebaseAuth.getUid())) {
                        eventAttendToggle.setEnabled(false);
                        Snackbar.make(parentLayout, "Nav iespējams atteikties no sava pasākuma! Ja vēlaties to dzēst, dodaties uz 'Mani pasākumi'!", Snackbar.LENGTH_INDEFINITE).show();

                    } else if ((event.getGuestCount() < event.getGuestMaxCount()) && (!authorId.equals(mFirebaseAuth.getUid()))) {
                        eventAttendToggle.setEnabled(true);
                    }
//                    else {
//                        eventAttendToggle.setEnabled(false);
//                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EventDetailActivity.this, String.valueOf(databaseError.getCode()), Toast.LENGTH_SHORT).show();
            }
        });



        eventAttendToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEventInteraction(mDatabaseReference);
            }
        });
    }

    private void onEventInteraction(DatabaseReference eventRef) {
        eventRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Event event = mutableData.getValue(Event.class);
                if (event == null) {
                    return Transaction.success(mutableData);
                }

                if (isUserAttending(mSavedEventDatabaseReference)){
                    event.setGuestCount(event.getGuestCount() - 1);
                    mSavedEventDatabaseReference.child(eventId).removeValue();

                } else {
                    event.setGuestCount(event.getGuestCount() + 1);
                    Map<String, Object> savedEvent = new HashMap<>();
                    savedEvent.put(event.getEventId(), true);
                    mSavedEventDatabaseReference.updateChildren(savedEvent);
                }
                // Set value and report transaction success
                mutableData.setValue(event);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
            }
        });
    }

    private boolean isUserAttending(DatabaseReference guestRef) {
        guestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(eventId)) {
                    userAttendance = true;
                    Log.d(TAG, "onDataChange: " + userAttendance);
                } else {
                    userAttendance = false;
                    Log.d(TAG, "onDataChange: " + userAttendance);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return userAttendance;
    }
}
