package com.alpoeventapp.qualityapp.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alpoeventapp.qualityapp.R;
import com.alpoeventapp.qualityapp.models.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Klase, kas atbilst profila skata aktivitātei.
 */
public class ProfileActivity extends AppCompatActivity {

    private EditText profileUsername;
    private EditText profileEmail;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ValueEventListener databaseListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle(getString(R.string.app_name) + ": "+ getString(R.string.profile_title));

        profileUsername = findViewById(R.id.etProfileUsername);
        profileEmail = findViewById(R.id.etProfileEmail);
        Button profileUpdate = findViewById(R.id.btnProfileUpdate);
        Button changePassword = findViewById(R.id.btnChangePassword);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mDatabaseReference = mFirebaseDatabase.getReference().child("users").child(mFirebaseAuth.getUid());

        databaseListener = new ValueEventListener() {  //reference uz lietotāja id lietotāju mezglā
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {   //darbojas uz datu izmaiņām un aktivitātes sākumā
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);  //saņemts objekts un no tā
                profileUsername.setText(userProfile.getUserName());                 //ar getteriem iegūtas vērtības
                profileEmail.setText(userProfile.getUserEmail());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Kļūda Firebase savienojumā.", Toast.LENGTH_SHORT);
            }
        };

        mDatabaseReference.addValueEventListener(databaseListener);

        profileUpdate.setOnClickListener(new View.OnClickListener() {   //pāreja uz profila atjaunināšanas aktivitāti
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, UpdateProfile.class));
                mDatabaseReference.removeEventListener(databaseListener);
                finish();
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {  //pāreja uz paroles maiņas aktivitāti
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, UpdatePassword.class));
            }
        });
    }

    /**
     * Atteikšanās funkcija. Lietotājs nokļūst autentifikācijas aktivitātē,
     * tiek pabeigtas visas iepriekšējās aktivitātes.
     */
    private void Logout() {
        mDatabaseReference.removeEventListener(databaseListener);
        mFirebaseAuth.signOut();
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
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
                startActivity(new Intent(ProfileActivity.this, SavedEventsListActivity.class));
                break;
            }

            case R.id.createEventMenu: {
                startActivity(new Intent(ProfileActivity.this, CreateEventActivity.class));
                break;
            }
            case R.id.logoutMenu: {
                Logout();
                break;
            }
            case R.id.profileMenu: {
                break;
            }

            case R.id.findEventMenu: {
                startActivity(new Intent(ProfileActivity.this, BrowseEventsListActivity.class));
                break;
            }

            case R.id.userEventsMenu: {
                startActivity(new Intent(ProfileActivity.this, UserEventListActivity.class));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
