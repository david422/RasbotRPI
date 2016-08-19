package com.rasbot.camera;

import com.google.gson.Gson;
import com.rasbot.model.CameraSettings;
import com.rasbot.model.Message;
import com.rasbot.server.ConnectionManager;
import com.rasbot.server.MessageCallback;

import java.util.logging.Logger;

/**
 * Created by dpodolak on 19.08.16.
 */
public class LocalCameraManager implements CameraManager{

    private Gson gson;


    private static final Logger logger = Logger.getLogger(LocalCameraManager.class.getName()
    );
    private ConnectionManager connectionManager;

    public LocalCameraManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;

        gson = new Gson();
    }

    @Override
    public MessageCallback getMessageCallback() {
        return messageCallback;
    }

    private MessageCallback messageCallback = message -> {

        logger.info("camera received settings");
        if (message == null){
            return;
        }

        if (message.getMessageType() == null){
            return;
        }

        switch (message.getMessageType()){
            case Message.CAMERA:
                setCameraSettings(message);
                break;
            case Message.READ_SETTINGS:
                CameraSettings settings = CameraUtils.readCameraSettings(gson);
                Message m = new Message(gson.toJson(settings));
                m.setType(Message.READ_SETTINGS);
                connectionManager.sendMessage(m);
                break;
        }
    };

    private void setCameraSettings(Message message) {
        CameraSettings cameraSettings = message.getCameraSettings(gson);

        if (cameraSettings == null){
            return;
        }

        logger.info(cameraSettings.toString());

        CameraUtils.saveSettings(cameraSettings, gson);

        connectionManager.sendMessage(message);
    }

    @Override
    public void start() {
        logger.info("Start camera");
    }
}
