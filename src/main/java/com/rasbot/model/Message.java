package com.rasbot.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dawidpodolak on 08.08.2016.
 */
public class Message {

    public static final String CONTROL = "control";
    public static final String CAMERA = "camera";
    public static final String SETTINGS = "settings";

    @SerializedName("type")
    private String messageType;

    @SerializedName("object")
    private Object messagePaylod;



    public Message(Object messagePaylod) {
        this.messagePaylod = messagePaylod;
    }

    public Control getControl(Gson gson){
        if (messageType != null && messageType.equals(CONTROL)){
            return gson.fromJson(messagePaylod.toString(), Control.class);
        }else {
            return null;
        }
    }

    public CameraSettings getCameraSettings(Gson gson){
        String string = messagePaylod.toString();

        if (messageType != null && messageType.equals(CAMERA)){
            return gson.fromJson(string, CameraSettings.class);
        }else{
            return null;
        }
    }

    public String getJsonMessage(){
        return new Gson().toJson(this);
    }

    @Override
    public String toString() {
        return String.format("type %s, object: %s", messageType, messagePaylod);
    }

    public String getMessageType() {
        return messageType;
    }
}
