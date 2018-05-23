package com.alpoeventapp.qualityapp.views;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alpoeventapp.qualityapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdatePassword extends AppCompatActivity {

    private Button update;
    private EditText newPassword;
    private EditText newPasswordConfirm;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        update = findViewById(R.id.btnUpdatePassword);
        newPassword = findViewById(R.id.etNewPassword);
        newPasswordConfirm = findViewById(R.id.etNewPasswordConfirm);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String updatedPassword = newPassword.getText().toString();
                String confirmPassword = newPasswordConfirm.getText().toString();

                if (!updatedPassword.equals(confirmPassword)) {
                    Toast.makeText(UpdatePassword.this, "Paroļu lauki nesakrīt!", Toast.LENGTH_SHORT).show();
                } else if (updatedPassword.length() < 6) {
                    Toast.makeText(UpdatePassword.this, "Parolei jāsastāv no vismaz 6 simboliem!", Toast.LENGTH_SHORT).show();
                } else {
                    mFirebaseUser.updatePassword(updatedPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(UpdatePassword.this, "Parole veiksmīgi nomainīta", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(UpdatePassword.this, "Kļūda paroles maiņā", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
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
}
