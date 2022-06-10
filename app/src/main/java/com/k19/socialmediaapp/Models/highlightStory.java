package com.k19.socialmediaapp.Models;

import java.util.ArrayList;

public class highlightStory {
    private String storyBy;
    private String storyAt;
    ArrayList<UserStories> stories;

    public highlightStory(String storyBy, String storyAt, ArrayList<UserStories> stories) {
        this.storyBy = storyBy;
        this.storyAt = storyAt;
        this.stories = stories;
    }

    public highlightStory() {
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
