package com.alpoeventapp.qualityapp.models;

/**
 * Lietotāja profila objekta klase
 */
public class UserProfile {
    private String userName;
    private String userEmail;

    /**
     * Tukšs konstruktors nepieciešams Firebase lietošanai
     */
    public UserProfile() {
    }

    public UserProfile(String userName, String userEmail) {
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

}
