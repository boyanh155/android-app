package com.k19.socialmediaapp.Models;

public class postModel {
   private String postID;
   private String postDescription;
   private String postImage;
   private String postedBy;
   private String postedAt;
   private int postLove;
   private  int commentCount;


    public postModel(String postID, String postDescription, String postImage, String postedBy, String postedAt) {
        this.postID = postID;
        this.postDescription = postDescription;
        this.postImage = postImage;
        this.postedBy = postedBy;
        this.postedAt = postedAt;
    }

    public postModel() {
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(String postedAt) {
        this.postedAt = postedAt;
    }

    public int getPostLove() {
        return postLove;
    }

    public void setPostLove(int postLove) {
        this.postLove = postLove;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}
