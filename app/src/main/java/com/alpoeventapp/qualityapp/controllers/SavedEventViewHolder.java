package com.alpoeventapp.qualityapp.controllers;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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

public class SavedEventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private View mView;
    private Context mContext;

    public SavedEventViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
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
                String passedEventId = events.get(itemPosition).getEventId();
                String passedAuthorId = events.get(itemPosition).getAuthorId();

                Intent intent = new Intent(mContext, EventDetailActivity.class);
                intent.putExtra("eventId", passedEventId);
                intent.putExtra("authorId", passedAuthorId);

                mContext.startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
