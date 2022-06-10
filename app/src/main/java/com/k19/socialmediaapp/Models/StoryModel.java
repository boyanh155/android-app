package com.k19.socialmediaapp.Models;

import java.util.ArrayList;

public class StoryModel {
    private String storyBy;
    private String storyAt;
    ArrayList<UserStories> stories;

    public StoryModel() {
    }

    public String getStoryBy() {
        return storyBy;
    }

    public void setStoryBy(String storyBy) {
        this.storyBy = storyBy;
    }

    public String getStoryAt() {
        return storyAt;
    }

    public void setStoryAt(String storyAt) {
        this.storyAt = storyAt;
    }

    public ArrayList<UserStories> getStories() {
        return stories;
    }

    public void setStories(ArrayList<UserStories> stories) {
        this.stories = stories;
    }
}
