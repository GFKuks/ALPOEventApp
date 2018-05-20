package com.alpoeventapp.qualityapp.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alpoeventapp.qualityapp.R;
import com.alpoeventapp.qualityapp.models.Event;

public class UserEventsDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_events_detail);

        Intent intent = getIntent();
//        Event event = intent.get
    }
}
