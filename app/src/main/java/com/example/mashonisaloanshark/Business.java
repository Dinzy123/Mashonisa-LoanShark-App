package com.example.mashonisaloanshark;

public class Business {
    private String name;
    private String address;
    private String contact;
    private String description;

    public Business() {
        // Default constructor required for calls to DataSnapshot.getValue(Business.class)
    }

    public Business(String name, String address, String contact, String description) {
        this.name = name;
        this.address = address;
        this.contact = contact;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getContact() {
        return contact;
    }

    public String getDescription() {
        return description;
    }
}
