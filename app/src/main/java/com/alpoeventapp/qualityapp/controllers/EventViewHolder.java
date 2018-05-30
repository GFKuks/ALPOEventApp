package com.alpoeventapp.qualityapp.controllers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alpoeventapp.qualityapp.R;
import com.alpoeventapp.qualityapp.models.Event;

/**
 * Šo klasi izmanto visas sarakstu aktivitātes, lai Pasākuma objekta informāciju izvietotu
 * saraksta elementā.
 */
public class EventViewHolder extends RecyclerView.ViewHolder {
    private View mView;

    public EventViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        Context context = itemView.getContext();
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