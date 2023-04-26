package com.example.aitools;

import java.util.Date;
public class ChatMessage {
    private String message;
    private Date timestamp;
    private boolean isSentByCurrentUser;

    public ChatMessage(String message, Date timestamp, boolean isSentByCurrentUser) {
        this.message = message;
        this.timestamp = timestamp;
        this.isSentByCurrentUser = isSentByCurrentUser;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public boolean isSentByCurrentUser() {
        return isSentByCurrentUser;
    }
}

