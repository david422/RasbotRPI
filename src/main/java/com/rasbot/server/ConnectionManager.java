package com.rasbot.server;

import com.rasbot.model.Control;

import java.util.logging.Logger;

/**
 * Created by dawidpodolak on 06.08.2016.
 */
public class ConnectionManager implements ConnectionCallback {


    private static final Logger logger = Logger.getLogger(ConnectionManager.class.getName());

    private PingServer pingServer;

    private MessageServer messageServer;

    private MessageCallback messageCallback;

    public ConnectionManager() {

    }

    public void init(){
        pingServer = new PingServer();
        pingServer.setConnectionCallback(this);
        pingServer.start();

        messageServer = new MessageServer();
        messageServer.setMessageCallback(messageCallback);
        messageServer.start();
    }

    public void setMessageCallback(MessageCallback messageCallback) {
        this.messageCallback = messageCallback;
    }

    @Override
    public void onConnectionEstablished() {
        pingServer.isConnection();
        logger.info("Connection established");
    }

    @Override
    public void onConnectionInterrupted() {
        logger.info("Connection interrupted");
        if (messageCallback != null){
            Control control = new Control(0, 0);
            messageCallback.onGetMessage(control);
        }
    }
}
