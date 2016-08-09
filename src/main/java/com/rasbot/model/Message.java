package com.rasbot.model;

import com.google.gson.Gson;

/**
 * Created by dawidpodolak on 08.08.2016.
 */
public class Message {

    public static final String CONTROL = "control";

    private String messageType;

    private String messagePaylod;

    public Control getControl(Gson gson){
        return gson.fromJson(messagePaylod, Control.class);
    }
}
