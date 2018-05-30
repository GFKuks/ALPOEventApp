package com.alpoeventapp.qualityapp.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Pasākuma objekta klase
 */
public class Event {

    private String eventId;
    private String title;
    private String address;
    private String date;
    private String description;
    private String authorId;
    private int guestCount = 1;  //Pasākuma organizators vienmēŗ ieskaitīts kā viesis
    private int guestMaxCount;

    /**
     * Tukšs konstruktors nepieciešams Firebase lietošanai
     */
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

    /**
     * toMap funkcija HashMap objektā ievieto pasākuma objekta informāciju, kura pēc tam tiek ielikta
     * atbilstošā pasākuma mezgla apakšā, ar attiecīgajiem nosaukumiem.
     */
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
//        result.put("guests", guests);

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

    public String getEventId() {
        return eventId;
    }

    public int getGuestMaxCount() {
        return guestMaxCount;
    }

    public void setGuestCount(int guestCount) {
        this.guestCount = guestCount;
    }

}
