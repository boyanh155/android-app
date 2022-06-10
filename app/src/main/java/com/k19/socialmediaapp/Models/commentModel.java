package com.k19.socialmediaapp.Models;

public class commentModel {
    private String commentBody;
    private String commentAt;
    private  String commentBy;

    public commentModel(String commentBody, String commentAt, String commentBy) {
        this.commentBody = commentBody;
        this.commentAt = commentAt;
        this.commentBy = commentBy;
    }

    public commentModel() {
    }

    public String getCommentBody() {
        return commentBody;
    }

    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
    }

    public String getCommentAt() {
        return commentAt;
    }

    public void setCommentAt(String commentAt) {
        this.commentAt = commentAt;
    }

    public String getCommentBy() {
        return commentBy;
    }

    public void setCommentBy(String commentBy) {
        this.commentBy = commentBy;
    }
}
