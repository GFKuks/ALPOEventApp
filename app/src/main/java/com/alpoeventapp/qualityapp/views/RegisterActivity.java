package com.alpoeventapp.qualityapp.views;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alpoeventapp.qualityapp.R;
import com.alpoeventapp.qualityapp.models.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText userName;
    private EditText userEmail;
    private EditText userPassword;
    private Button regButton;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;

    String username, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userName = findViewById(R.id.etUsername);
        userEmail = findViewById(R.id.etProfileEmail);
        userPassword = findViewById(R.id.etPassword);
        regButton = findViewById(R.id.btnRegister);

        mFirebaseAuth = FirebaseAuth.getInstance();

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    //Upload data to the database
                    String user_email = userEmail.getText().toString().trim();  // .trim() removes whitespaces
                    String user_password = userPassword.getText().toString().trim();

                    mFirebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                sendUserData(); //sends user data to database after authentification is done
                                Toast.makeText(RegisterActivity.this, "Lietotājs veiksmīgi reģistrēts. Nosūtīts apstiprinājuma e-pasts.", Toast.LENGTH_SHORT).show();
//                                mFirebaseAuth.signOut();
                                finish();
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
//                                sendEmailVerification();
//                                Toast.makeText(RegisterActivity.this, "Lietotājs veiksmīgi reģistrēts", Toast.LENGTH_SHORT).show();
//                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            } else {
                                Toast.makeText(RegisterActivity.this, "Kļūda reģistrācijā", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    private boolean validate() {
        boolean result = false;

        username = userName.getText().toString();
        email = userEmail.getText().toString();
        password = userPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Lūdzu, aizpildiet visus laukus!", Toast.LENGTH_SHORT).show();
        } else {
            result = true;
        }

        return result;
    }

    private void sendEmailVerification() {
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();   //get user who is trying to authorize
        if (firebaseUser != null) {  //user isn't null, then sending email verification
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        sendUserData(); //sends user data to database after authentification is done
                        Toast.makeText(RegisterActivity.this, "Lietotājs veiksmīgi reģistrēts. Nosūtīts apstiprinājuma e-pasts.", Toast.LENGTH_SHORT).show();
//                        mFirebaseAuth.signOut();  //add back in for email verificaiton
                        finish();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    } else {    //piemēram gadījumā, ja nav interneta
                        Toast.makeText(RegisterActivity.this, "Apstiprinājuma e-pasts nav nosūtīts.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserData() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        UserProfile userProfile = new UserProfile(username, email);

        mDatabaseReference.child("users").child(mFirebaseAuth.getUid()).setValue(userProfile);
    }


}
