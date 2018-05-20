package com.alpoeventapp.qualityapp.views;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alpoeventapp.qualityapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText userEmail;
    private EditText userPassword;
    private TextView info;
    private Button login;
    private TextView userRegistration;
    private FirebaseAuth mFirebaseAuth; //object of FirebaseAuth
    private TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userEmail = findViewById(R.id.etProfileEmail);
        userPassword = findViewById(R.id.etPassword);
        login = findViewById(R.id.btnLogin);
        userRegistration = findViewById(R.id.tvRegister);
        forgotPassword = findViewById(R.id.tvForgotPassword);

        mFirebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mFirebaseAuth.getCurrentUser(); //checks in database if user has already logged in on app, if yes, direct to next activity

        if (user != null) { //if logged in, finish activity and move on to next activity
            finish();   //finish function destroys the active activity
            startActivity(new Intent(MainActivity.this, UserEventsListActivity.class));
        }

        login.setOnClickListener(new View.OnClickListener() {   //validates that fits to saved values
            @Override
            public void onClick(View v) {
                validate(userEmail.getText().toString(), userPassword.getText().toString());
            }
        });

        userRegistration.setOnClickListener(new View.OnClickListener() {    //sends user to registration activity
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PasswordActivity.class));
            }
        });
    }

    private void validate (String userEmail, String userPassword) {

        mFirebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Autentifikācija izdevusies.", Toast.LENGTH_SHORT).show();
                    checkEmailVerification();   //checking if email is verified
                } else {
                    Toast.makeText(MainActivity.this, "Nepareizs e-pasts vai parole!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkEmailVerification() {
        FirebaseUser firebaseUser = mFirebaseAuth.getInstance().getCurrentUser();
        boolean emailflag = firebaseUser.isEmailVerified();
        startActivity (new Intent(MainActivity.this, UserEventsListActivity.class));

//        if(emailflag) {               //uncomment if using email verification
//            startActivity (new Intent(MainActivity.this, UserEventsListActivity.class));
//        } else {    //if user hasn't verified email, signs out
//            Toast.makeText(this, "Apstipriniet e-pasta adresi!", Toast.LENGTH_SHORT).show();
//            mFirebaseAuth.signOut();    //signs out, because we're still signing in
//        }
    }
}
