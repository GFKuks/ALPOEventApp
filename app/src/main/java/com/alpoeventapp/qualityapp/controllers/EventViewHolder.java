package com.alpoeventapp.qualityapp.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alpoeventapp.qualityapp.R;
import com.alpoeventapp.qualityapp.models.Event;
import com.alpoeventapp.qualityapp.views.EventDetailActivity;
import com.alpoeventapp.qualityapp.views.UserEventsDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventViewHolder extends RecyclerView.ViewHolder
//        implements View.OnClickListener
{
    private static final String TAG = "EventViewHolder";
    private View mView;
    private Context mContext;

    public EventViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
    }

    public void bindEvent(Event event) {
        TextView title = mView.findViewById(R.id.tvBrowseTitle);
        TextView address = mView.findViewById(R.id.tvBrowseAddress);
        TextView date = mView.findViewById(R.id.tvBrowseDate);
        TextView guestCount = mView.findViewById(R.id.tvBrowseGuestCount);

        title.setText(event.getTitle());
        address.setText(event.getAddress());
        date.setText(event.getDate());

        String guestCapacity = event.getGuestCount() + "/" + event.getGuestMaxCount();
        guestCount.setText(guestCapacity);

    }
}