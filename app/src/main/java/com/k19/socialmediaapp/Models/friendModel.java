package com.k19.socialmediaapp.Models;

public class friendModel {
    int profile;
    String name;

    public friendModel(int profile, String name) {
        this.profile = profile;
        this.name = name;
    }

    public int getProfile() {
        return profile;
    }

    public void setProfile(int profile) {
        this.profile = profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
