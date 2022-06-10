package com.k19.socialmediaapp.Models;

public class chatModel {
    private String chatBy;
    private String chat;
    private String chatAt;

    public chatModel(String chatBy, String chat, String chatAt) {
        this.chatBy = chatBy;
        this.chat = chat;
        this.chatAt = chatAt;
    }

    public chatModel() {
    }

    public String getChatBy() {
        return chatBy;
    }

    public void setChatBy(String chatBy) {
        this.chatBy = chatBy;
    }

    public String getChat() {
        return chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public String getChatAt() {
        return chatAt;
    }

    public void setChatAt(String chatAt) {
        this.chatAt = chatAt;
    }
}
