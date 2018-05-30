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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Klase, kas atbilst paroles atiestatīšanas aktivitātei
 */
public class PasswordActivity extends AppCompatActivity {

    private EditText passwordEmail;
    private FirebaseAuth mFirebaseAuth; //FirebaseAuth object deals with firebase authentication functionality

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        passwordEmail = findViewById(R.id.etPasswordEmail);
        Button resetPassword = findViewById(R.id.btnPasswordReset);
        mFirebaseAuth = FirebaseAuth.getInstance();

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = passwordEmail.getText().toString().trim();

                if (userEmail.equals("")) {
                    Toast.makeText(PasswordActivity.this, "Lūdzu, ievadiet profila e-pastu!", Toast.LENGTH_SHORT).show();
                } else {
                    mFirebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(PasswordActivity.this, "Saite uz atiestatīšanu nosūtīta uz e-pastu.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(PasswordActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(PasswordActivity.this, "Kļūda atiestatīšanas e-pasta sūtīšanā.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PasswordActivity.this, MainActivity.class));
        finish();
    }
}
