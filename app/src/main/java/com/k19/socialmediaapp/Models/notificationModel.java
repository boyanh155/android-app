package com.k19.socialmediaapp.Models;

public class notificationModel {
    String profile;

    String notificationBy;
    String notification;

    public notificationModel(String profile, String notification) {
        this.profile = profile;
        this.notification = notification;
    }

    public notificationModel() {
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getNotificationBy() {
        return notificationBy;
    }

    public void setNotificationBy(String notificationBy) {
        this.notificationBy = notificationBy;
    }
}
