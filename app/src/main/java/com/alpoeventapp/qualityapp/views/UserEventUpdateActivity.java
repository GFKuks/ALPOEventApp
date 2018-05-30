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

/**
 * Šī klase apraksta lietotāja pasākumu rediģēšanas aktivitātes darbību
 */
public class UserEventUpdateActivity extends AppCompatActivity {

    private EditText eventTitle;
    private EditText eventAddress;
    private EditText eventDate;
    private EditText eventDescription;
    private EditText eventMaxGuestCount;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;

    private String title;
    private String address;
    private String date;
    private String description;
    private String authorId;
    private String eventId;
    private int guestMaxCount;

    /**
     * onCreate funkcija darbojas aktivitātes sākšanas vai atsākšanas brīdī.
     * @param savedInstanceState - izmantots, lai saglabātu iepriekš ievadītu informāciju.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_event);
        setTitle(getString(R.string.app_name) + ": "+ getString(R.string.user_events_title));

        eventTitle = findViewById(R.id.etEditEventTitle);
        eventAddress = findViewById(R.id.etEditEventAddress);
        eventDate = findViewById(R.id.etEditEventDate);
        eventDescription = findViewById(R.id.etEditEventDescription);
        eventMaxGuestCount = findViewById(R.id.etEditMaxGuestCount);

        Button delete = findViewById(R.id.btnDeleteEvent);
        Button confirm = findViewById(R.id.btnSaveEventChanges);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        eventId = intent.getStringExtra("eventId");
        authorId = mFirebaseAuth.getUid();


        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("events").child(eventId);

        /**
         * Notikumu uztvērējs novēro pasākumu, un atjaunina par to uzrādīto informāciju.
         */
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
                Toast.makeText(UserEventUpdateActivity.this, String.valueOf(databaseError.getCode()), Toast.LENGTH_SHORT).show();
            }
        };

        databaseReference.addValueEventListener(eventListener);

        /**
         * Apstiprinājuma poga pārbauda, vai izpildās validācija. Ja jā, tad tiek saglabāts izmaiņas
         * un lietotājs tiek aizvests uz pasākuma detalizēto skatu.
         */

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    updateEventData(eventId, title, address, date, description, authorId, guestMaxCount);
                    Toast.makeText(UserEventUpdateActivity.this, "Izmaiņas saglabātas.", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(UserEventUpdateActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(UserEventUpdateActivity.this, "Kļūda pasākuma izmaiņu saglabāšanā.", Toast.LENGTH_SHORT).show();
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

    /**
     * Funkcija pārbauda, vai pasākums atbilst noteikumiem
     */
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
            Toast.makeText(this, R.string.REG_ERR_2, Toast.LENGTH_SHORT).show();
        } else if (title.length() > 25) {
            Toast.makeText(this, "Nosaukums nedrīkst būt garāks par 25 rakstu zīmēm!", Toast.LENGTH_SHORT).show();
        } else if (address.length() > 50) {
            Toast.makeText(this, "Adrese nedrīkst būt garāka par 50 rakstu zīmēm!", Toast.LENGTH_SHORT).show();
        } else if (description.length() > 250) {
            Toast.makeText(this, "Apraksts nedrīkst būt garāks par 250 rakstu zīmēm!", Toast.LENGTH_SHORT).show();
        } else {
            authorId = mFirebaseAuth.getUid();
            result = true;
        }

        return result;
    }

    /**
     * Funkcija atjaunina pasākuma mezgla vērtības ar jaunajām norādītajām.
     * @param eventId - parametrs tiek iegūts sākoties aktivitātes darbam, no iepriekšējās aktivitātes
     * @param title - {..
     * @param address - ..
     * @param date - ..
     * @param description - ..} parametri iegūti no mainīgajiem, kas aizpildīti validācijas laikā
     * @param authorId - parametrs iegūts no esošā lietotāja autorizācijas informācijas
     * @param guestMaxCount - parametrs iegūts no mainīgā, kas aizpildīts validācijas laikā
     */
    private void updateEventData(String eventId, String title, String address, String date, String description, String authorId, int guestMaxCount) {
        Event updatedEvent = new Event(eventId, title, address, date, description, authorId, guestMaxCount);
        Map<String, Object> eventValues = updatedEvent.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/events/" + eventId, eventValues);

        mDatabaseReference.updateChildren(childUpdates);
    }

    /**
     * Funkcija izdzēš pasākuma objektu no /events/ mezgla, kā arī izdzēš pasākuma atslēgas ierakstu
     * no lietotāja pasākumu mezgla.
     */
    private void deleteEvent() {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/events/" + eventId, null);
        mDatabaseReference.updateChildren(childUpdates);
        mDatabaseReference.child("user-events").child(authorId).child(eventId).removeValue();

        Toast.makeText(UserEventUpdateActivity.this, R.string.EVT_MSG_3, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(UserEventUpdateActivity.this, MainActivity.class));
    }
}
