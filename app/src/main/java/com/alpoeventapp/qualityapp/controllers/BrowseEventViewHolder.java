package com.alpoeventapp.qualityapp.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alpoeventapp.qualityapp.R;
import com.alpoeventapp.qualityapp.models.Event;
import com.alpoeventapp.qualityapp.views.EventDetailActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BrowseEventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    View mView;
    Context mContext;

    public BrowseEventViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    public void bindEvent(Event event) {
        EditText title = mView.findViewById(R.id.etBrowseTitle);
        EditText address = mView.findViewById(R.id.etBrowseAddress);
        EditText date = mView.findViewById(R.id.etBrowseDate);
        EditText guestCount = mView.findViewById(R.id.etBrowseGuestCount);

        title.setText(event.getTitle());
        address.setText(event.getAddress());
        date.setText(event.getDate());

        String guestCapacity = event.getGuestCount() + "/" + event.getGuestMaxCount();
        guestCount.setText(guestCapacity);

    }

    @Override
    public void onClick(View view) {
        final ArrayList<Event> events = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref = ref.child("events");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    events.add(snapshot.getValue(Event.class));
                }

                int itemPosition = getLayoutPosition();

                Intent intent = new Intent(mContext, EventDetailActivity.class);

                Bundle eventList = new Bundle();
                eventList.putSerializable("eventList", events);
                intent.putExtra("position", itemPosition + "");
                intent.putExtra("bundle", eventList);

                mContext.startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
