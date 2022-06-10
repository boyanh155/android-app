package com.k19.socialmediaapp.Models;

import java.util.Date;

public class chatMessage {
  public String messageId;
  public String senderId;
  public String receiverId;
  public String mess;
  public String sendAdd;
  public Date dateObject;


    public chatMessage(String messageId, String senderId, String receiverId, String mess, String sendAdd) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.mess = mess;
        this.sendAdd = sendAdd;
    }

    public chatMessage() {
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMess() {
        return mess;
    }

    public void setMess(String mess) {
        this.mess = mess;
    }

    public String getSendAdd() {
        return sendAdd;
    }

    public void setSendAdd(String sendAdd) {
        this.sendAdd = sendAdd;
    }
}
