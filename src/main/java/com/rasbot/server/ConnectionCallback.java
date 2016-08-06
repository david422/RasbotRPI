package com.rasbot.server;

/**
 * Created by dawidpodolak on 06.08.2016.
 */
public interface ConnectionCallback {

    public void onConnectionEstablished();

    public void onConnectionInterrupted();


}
