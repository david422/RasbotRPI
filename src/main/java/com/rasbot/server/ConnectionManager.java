package com.rasbot.server;

import com.rasbot.model.Control;
import com.rasbot.model.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by dawidpodolak on 06.08.2016.
 */
public class ConnectionManager implements ConnectionCallback, MessageCallback {


    private static final Logger logger = Logger.getLogger(ConnectionManager.class.getName());

    private PingServer pingServer;

    private MessageServer messageServer;

    private List<MessageCallback> messageCallbacks = new ArrayList<>();

    public ConnectionManager() {

    }

    public void init() {
        pingServer = new PingServer();
        pingServer.setConnectionCallback(this);
        pingServer.start();

        messageServer = new MessageServer();
        messageServer.setMessageCallback(this);
        messageServer.start();
    }

    @Override
    public void onConnectionEstablished() {
        pingServer.isConnection();
        logger.info("Connection established");
    }

    public void addMessageCallback(MessageCallback callback) {
        messageCallbacks.add(callback);
    }

    @Override
    public void onConnectionInterrupted() {
        logger.info("Connection interrupted");
        onGetMessage(new Message(Control.buildZeroControl()));

    }

    @Override
    public void onGetMessage(Message controlMessage) {
        for (MessageCallback ms : messageCallbacks) {
            ms.onGetMessage(controlMessage);
        }
    }

    public void sendMessage(Message message) {
        messageServer.sendMessage(message);
    }
}
