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

/**
 * Klase, kas atbilst autentifikācijas aktivitātei.
 */
public class MainActivity extends AppCompatActivity {

    private EditText userEmail;
    private EditText userPassword;
    private FirebaseAuth mFirebaseAuth; //object of FirebaseAuth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userEmail = findViewById(R.id.etProfileEmail);
        userPassword = findViewById(R.id.etPassword);
        Button login = findViewById(R.id.btnLogin);
        TextView userRegistration = findViewById(R.id.tvRegister);
        TextView forgotPassword = findViewById(R.id.tvForgotPassword);

        mFirebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mFirebaseAuth.getCurrentUser(); //mainīgais, ar kuru pārbaudīs, vai lietotājs ir autentificējies

        if (user != null) { //ja lietotājs ir autentificējies
            startActivity(new Intent(MainActivity.this, UserEventListActivity.class));
            finish();   //tiek izlaists autentifikācijas logs
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
                finish();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PasswordActivity.class));
                finish();
            }
        });
    }

    /**
     * Validācijas funkcija, kas pārbauda, vai pastāv e-pasts ar tādu paroli, un ja ir,
     * vai šis e-pasts ir verificēts.
     */
    private void validate (String userEmail, String userPassword) {

        mFirebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, R.string.AUTH_MSG_1, Toast.LENGTH_SHORT).show();
                    checkEmailVerification();   //checking if email is verified
                } else {
                    Toast.makeText(MainActivity.this, R.string.AUTH_ERR_1, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * E-pasta verifikācijas funkcija
     */
    private void checkEmailVerification() {
        FirebaseUser firebaseUser = mFirebaseAuth.getInstance().getCurrentUser();
        boolean emailflag = firebaseUser.isEmailVerified();

        if(emailflag) {
            startActivity (new Intent(MainActivity.this, UserEventListActivity.class));
            finish();
        } else {
            Toast.makeText(this, R.string.AUTH_ERR_2, Toast.LENGTH_SHORT).show();
            mFirebaseAuth.signOut();    //atteikšanās no darba, ja nav verificēts e-pasts
        }
    }

}
