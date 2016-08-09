package com.rasbot.event;

/**
 * Created by dawidpodolak on 08.08.2016.
 */
public class MessageEvent {
    private String message;

    public MessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
