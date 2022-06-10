package com.k19.socialmediaapp.Models;

public class DasboardModel {

    int profile,postImage,save;
    String name,about,love,comment,share,storyPost;


    public DasboardModel(int profile, int postImage, int save, String name, String about, String love, String comment, String share, String storyPost) {
        this.profile = profile;
        this.postImage = postImage;
        this.save = save;
        this.name = name;
        this.about = about;
        this.love = love;
        this.comment = comment;
        this.share = share;
        this.storyPost = storyPost;
    }

    public int getProfile() {
        return profile;
    }

    public void setProfile(int profile) {
        this.profile = profile;
    }

    public int getPostImage() {
        return postImage;
    }

    public void setPostImage(int postImage) {
        this.postImage = postImage;
    }

    public int getSave() {
        return save;
    }

    public void setSave(int save) {
        this.save = save;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getLove() {
        return love;
    }

    public void setLove(String love) {
        this.love = love;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getShare() {
        return share;
    }

    public void setShare(String share) {
        this.share = share;
    }

    public String getStoryPost() {
        return storyPost;
    }

    public void setStoryPost(String storyPost) {
        this.storyPost = storyPost;
    }

}
