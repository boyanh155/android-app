package com.k19.socialmediaapp.Models;

public class UserStories {
    private String image;
    private String storyAt;

    public UserStories(String image, String storyAt) {
        this.image = image;
        this.storyAt = storyAt;
    }

    public UserStories() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStoryAt() {
        return storyAt;
    }

    public void setStoryAt(String storyAt) {
        this.storyAt = storyAt;
    }
}
