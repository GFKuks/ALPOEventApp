package com.alpoeventapp.qualityapp.models;

import java.util.HashMap;
import java.util.Map;

public class Event {

    public String eventId;
    public String title;
    public String address;
    public String date;
    public String description;
    public String authorId;
    public int guestCount = 1;  //Pasākuma organizators vienmēŗ ieskaitīts kā viesis
    public int guestMaxCount;
    public Map<String, Boolean> guests = new HashMap<>();


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

    public void setGuestCount(int guestCount) {
        this.guestCount = guestCount;
    }

    public void setGuestMaxCount(int guestMaxCount) {
        this.guestMaxCount = guestMaxCount;
    }

    public void setGuests(Map<String, Boolean> guests) {
        this.guests = guests;
    }
}
