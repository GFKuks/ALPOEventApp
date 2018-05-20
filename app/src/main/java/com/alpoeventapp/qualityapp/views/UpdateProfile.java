package com.alpoeventapp.qualityapp.views;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alpoeventapp.qualityapp.R;
import com.alpoeventapp.qualityapp.models.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateProfile extends AppCompatActivity {

    private EditText newUsername;
    private EditText userEmail;
    private Button save;
    private Button deleteProfile;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        newUsername = findViewById(R.id.etProfileUsername);
        userEmail = findViewById(R.id.etProfileEmail);
        save = findViewById(R.id.btnSave);
        deleteProfile = findViewById(R.id.btnDeleteProfile);

        userEmail.setEnabled(false); //makes it uneditable

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        final DatabaseReference databaseReference = mFirebaseDatabase.getReference().child("users").child(mFirebaseAuth.getUid());
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference.addValueEventListener(new ValueEventListener() {  //has reference to auth user id
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {   //on change or when app starts
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);  //parameter for getvalue is class name
                newUsername.setText(userProfile.getUserName().toString());
                userEmail.setText(userProfile.getUserEmail().toString());
            }                                                                        //retrieves data of user with Uid

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UpdateProfile.this, String.valueOf(databaseError.getCode()), Toast.LENGTH_SHORT);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = newUsername.getText().toString().trim();
                String email = userEmail.getText().toString().trim();

                UserProfile userProfile = new UserProfile(username, email);

                databaseReference.setValue(userProfile);

                finish();   //finishing activity takes you back to previous activity
            }
        });

        deleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAccount() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    startActivity(new Intent(UpdateProfile.this, MainActivity.class));
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateProfile.this, "Kļūda dzēšot profilu!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
