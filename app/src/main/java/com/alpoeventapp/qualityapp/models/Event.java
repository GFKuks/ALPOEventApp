package com.alpoeventapp.qualityapp.models;

import java.util.HashMap;
import java.util.Map;

public class Event {

    private String eventId;
    private String title;
    private String address;
    private String date;
    private String description;
    private String authorId;
    private int guestCount = 0;
    private int guestMaxCount;
    private Map<String, Boolean> guests = new HashMap<>();


    public Event() {
    }

    public Event(String eventId, String title, String address, String date, String description, String authorId, int guestMaxCount) {
        this.eventId = eventId;
        this.title = title;
        this.address = address;
        this.date = date;
        this.description = description;
        this.authorId = authorId;
        this.guestMaxCount = guestMaxCount;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("eventId", eventId);
        result.put("title", title);
        result.put("address", address);
        result.put("date", date);
        result.put("description", description);
        result.put("authorId", authorId);
        result.put("guestCount", guestCount);
        result.put("guestMaxCount", guestMaxCount);
        result.put("guests", guests);

        return result;
    }

    public String getTitle() {
        return title;
    }

    public String getAddress() {
        return address;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthorId() {
        return authorId;
    }

    public int getGuestCount() {
        return guestCount;
    }

    public Map<String, Boolean> getGuests() {
        return guests;
    }

    public String getEventId() {
        return eventId;
    }

    public int getGuestMaxCount() {
        return guestMaxCount;
    }
}
