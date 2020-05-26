package com.example.awesomechat;

import android.net.Uri;

public class AwesomeMessage {

    private String text;
    private String name;
    private String imageUrl;
    private String sender;
    private String recepient;
    private boolean isMine;
    private String messageDate;


    public AwesomeMessage() {
    }

    public AwesomeMessage(String text, String name, String sender, String recepient, boolean isMine, String imageUrl) {
        this.text = text;
        this.name = name;
        this.imageUrl = imageUrl;
        this.sender = sender;
        this.recepient = recepient;
        this.isMine = isMine;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecepient() {
        return recepient;
    }

    public void setRecepient(String recepient) {
        this.recepient = recepient;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(String messageDate) {
        this.messageDate = messageDate;
    }
}
